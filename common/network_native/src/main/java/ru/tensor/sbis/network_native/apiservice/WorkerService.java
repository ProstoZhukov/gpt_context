package ru.tensor.sbis.network_native.apiservice;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import ru.tensor.sbis.entrypoint_guard.service.EntryPointService;
import ru.tensor.sbis.network_native.BuildConfig;
import ru.tensor.sbis.network_native.command.BaseCommand;
import timber.log.Timber;

/**
 * Legacy-код
 * <p>
 * Created by ss.buvaylink on 02.02.2016.
 */
@SuppressWarnings({"rawtypes", "unused"})
public class WorkerService extends EntryPointService implements BaseCommand.CommandExecutorInterface {

    private static final String TAG = WorkerService.class.getCanonicalName();

    public static final String ACTION_EXECUTE_COMMAND = TAG + ".ACTION_EXECUTE_COMMAND";
    public static final String EXTRA_COMMAND = TAG + ".EXTRA_COMMAND";

    public static final String EXTRA_COMMAND_NAME = TAG + ".EXTRA_COMMAND_NAME";
    public static final String EXTRA_ERROR_MESSAGE = TAG + ".EXTRA_ERROR_MESSAGE";
    public static final String EXTRA_ERROR_CODE = TAG + ".EXTRA_ERROR_CODE";
    public static final String EXTRA_ERROR_BODY_MESSAGE = TAG + ".EXTRA_ERROR_BODY_MESSAGE";
    public static final String EXTRA_ERROR_BODY_MESSAGE_DETAILS = TAG + ".EXTRA_ERROR_BODY_MESSAGE_DETAILS";
    public static final String EXTRA_ERROR_BODY_CODE = TAG + ".EXTRA_ERROR_BODY_CODE";
    public static final String EXTRA_HAS_ERROR_BODY_DATA = TAG + ".EXTRA_HAS_ERROR_BODY_DATA";
    public static final String EXTRA_IS_SUCCESS = TAG + ".EXTRA_IS_SUCCESS";
    private static final int PURGE_THRESHOLD = 10;
    private static final int STATE_IDLE = 0;
    private static final int STATE_ADDING = 1;
    private static final int STATE_FINISHING = 2;

    private static final boolean MAY_INTERRUPT = true;

    private static final int NUM_THREADS = 3;

    private static final long EXECUTOR_SHUTDOWN_TIMEOUT_IN_SECONDS = 30;

    public static void runCommand(@NonNull Context context, @NonNull BaseCommand command) {
        //noinspection ConstantConditions
        if (context == null) {
            if (BuildConfig.DEBUG) {
                throw new IllegalStateException("Context is null! It seems, method was called " +
                        "after context destroying(Command name: " + command.getCommandName() + ").");
            } else {
                Timber.e("Detected attempt to run command " + command.getCommandName() + " with null context.");
                return;
            }
        }
        //noinspection deprecation
        if (ContextUtilsDuplicate.isAppBackgroundServiceLimited(context)) {
            Timber.e("Попытка фонового приложения создать фоновую службу " + command.getCommandName() +
                    ", что Android SDK: " + Build.VERSION.SDK_INT + " не позволяет");
            return;
        }
        Intent i = new Intent(context, WorkerService.class);
        i.setAction(WorkerService.ACTION_EXECUTE_COMMAND);
        i.putExtra(WorkerService.EXTRA_COMMAND, command);
        context.startService(i);
    }

    @NonNull
    private final WorkerBinder mWorkerBinder = new WorkerBinder();
    private final AtomicInteger mState = new AtomicInteger(STATE_IDLE);
    private final AtomicBoolean mPurgeState = new AtomicBoolean(false);
    ThreadPoolExecutor mExecutorService;
    Set<BaseCommand> mPendingCommands;

    private volatile boolean mBoundState;

    @Override
    protected void onReady() {
        mPendingCommands = Collections.newSetFromMap(new ConcurrentHashMap<>());
        mExecutorService = createExecutor(NUM_THREADS);
    }

    @NonNull
    protected ThreadPoolExecutor createExecutor(@SuppressWarnings("SameParameterValue") int numThreads) {
        return new ThreadPoolExecutor(numThreads, numThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals(ACTION_EXECUTE_COMMAND)) {
                BaseCommand baseCommand = getCommand(intent);
                if (baseCommand != null) {
                    if (mState.compareAndSet(STATE_IDLE, STATE_ADDING)) {
                        runCommand(baseCommand);
                        mState.set(STATE_IDLE);
                    } else {
                        Timber.d("%s was sent to a new WorkerService", baseCommand.getCommandName());
                        stopSelf();
                        runCommand(getApplicationContext(), baseCommand);
                    }
                }
            } else {
                Timber.e("WorkerService: invalid action = %s", action);
                stopSelf();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mExecutorService.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!mExecutorService.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)) {
                mExecutorService.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!mExecutorService.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)) {
                    Timber.e("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            mExecutorService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        mBoundState = true;
        return mWorkerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mBoundState = false;
        if (mPendingCommands.isEmpty() && mState.compareAndSet(STATE_IDLE, STATE_FINISHING)) {
            stopSelf();
        }
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        mBoundState = true;
        if (mState.compareAndSet(STATE_FINISHING, STATE_IDLE)) {
            Timber.d("WorkerService state restored on rebind.");
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    Future runCommand(@NonNull BaseCommand baseCommand) {
        clearSameCommands(baseCommand.getCommandName());
        checkQueueSize();
        baseCommand.setExecutor(this);
        baseCommand.setContext(getApplicationContext());
        Future future = mExecutorService.submit(baseCommand);
        baseCommand.setFuture(future);
        mPendingCommands.add(baseCommand);
        return future;
    }

    @Nullable
    private BaseCommand getCommand(@NonNull Intent intent) {
        return intent.getParcelableExtra(EXTRA_COMMAND);
    }

    @Override
    public void removeCommand(@NonNull BaseCommand baseCommand) {
        mPendingCommands.remove(baseCommand);
        if (mPendingCommands.isEmpty() && !mBoundState && mState.compareAndSet(STATE_IDLE, STATE_FINISHING)) {
            stopSelf();
        }
    }

    @Override
    public boolean isCommandInQueue(@NonNull BaseCommand baseCommand) {
        return mPendingCommands.contains(baseCommand);
    }

    void clearSameCommands(@NonNull String commandName) {
        for (BaseCommand command : mPendingCommands) {
            if (!commandName.equals(BaseCommand.COMMAND_NAME) && commandName.equals(command.getCommandName())) {
                cancelCommand(command);
            }
        }
    }

    void cancelCommand(@NonNull BaseCommand command) {
        command.cancel(MAY_INTERRUPT);
        mPendingCommands.remove(command);
    }

    private void checkQueueSize() {
        if (mPurgeState.compareAndSet(false, true)) {
            int size = mExecutorService.getQueue().size();
            if (size > PURGE_THRESHOLD) {
                mExecutorService.purge();
                Timber.d("WorkerService with " + size + " commands were purged. "
                        + mExecutorService.getQueue().size() + " commands left.");
            }
            mPurgeState.set(false);
        }
    }

    public boolean isCommandRunning(@NonNull String commandName) {
        for (BaseCommand command : mPendingCommands) {
            if (!commandName.equals(BaseCommand.COMMAND_NAME) && commandName.equals(command.getCommandName())) {
                return true;
            }
        }
        return false;
    }

    public class WorkerBinder extends Binder {

        @NonNull
        public WorkerService getService() {
            return WorkerService.this;
        }

    }

}