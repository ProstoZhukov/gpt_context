package ru.tensor.sbis.network_native.command;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Future;

import ru.tensor.sbis.network_native.apiservice.WorkerService;
import ru.tensor.sbis.network_native.apiservice.event.RequestIsDoneEvent;
import ru.tensor.sbis.network_native.event.RequestAuthTokenEvent;
import ru.tensor.sbis.event_bus.EventBusUtilsKt;
import timber.log.Timber;

/**
 * Legacy-код
 */
@SuppressWarnings("unused")
public abstract class BaseCommand implements Parcelable, Runnable {

    protected static final int ERROR_CODE_UNAUTHORIZED = 401;

    public static final String COMMAND_NAME = "ru.tensor.sbis.BASE_COMMAND";

    public static final int RESPONSE_SUCCESS = 0;
    public static final int RESPONSE_FAILURE = 1;

    public interface CommandExecutorInterface {
        void removeCommand(@NonNull BaseCommand baseCommand);

        boolean isCommandInQueue(@NonNull BaseCommand baseCommand);
    }

    private WeakReference<CommandExecutorInterface> mExecutor;
    @SuppressWarnings("rawtypes")
    private Future mFuture;
    private boolean mIsRunning;
    private final String mCommandName;
    private boolean mIsSessionChecked;

    protected Context mContext;

    @SuppressWarnings("unused")
    public BaseCommand() {
        this(COMMAND_NAME);
    }

    public BaseCommand(String commandName) {
        mCommandName = commandName;
    }

    public String getCommandName() {
        return mCommandName;
    }

    @Override
    public final void run() {
        mIsRunning = true;
        try {
            doExecute();
        } catch (Exception e) {
            //noinspection ConstantConditions
            if (!(e instanceof IOException)) {
                Timber.e(e);
            } else {
                Timber.d(e);
            }
            Bundle bundle = new Bundle();
            bundle.putString(WorkerService.EXTRA_COMMAND_NAME, mCommandName);
            notifyFailure(bundle);
        }
        mIsRunning = false;
        removeCommand();
    }

    protected void removeCommand() {
        CommandExecutorInterface executor = getExecutor();
        if (executor == null) {
            return;
        }
        executor.removeCommand(this);
    }

    @Nullable
    private CommandExecutorInterface getExecutor() {
        WeakReference<CommandExecutorInterface> executorReference = mExecutor;
        if (executorReference == null) {
            return null;
        }
        return executorReference.get();
    }

    public void setExecutor(@NonNull CommandExecutorInterface executor) {
        mExecutor = new WeakReference<>(executor);
    }

    public void setFuture(@SuppressWarnings("rawtypes") @NonNull Future future) {
        mFuture = future;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean cancel(boolean interrupt) {
        return !(mFuture.isCancelled() || mFuture.isDone() || !mFuture.cancel(interrupt));
    }

    public void setContext(@NonNull Context context) {
        mContext = context.getApplicationContext();
    }

    protected abstract void doExecute();

    protected void notifySuccess(Bundle data) {
        sendUpdate(RESPONSE_SUCCESS, data);
    }

    protected void notifyFailure(Bundle data) {
        if (mIsSessionChecked || !checkSession(data)) {
            sendUpdate(RESPONSE_FAILURE, data);
        }
    }

    protected boolean checkSession(@Nullable final Bundle data) {
        mIsSessionChecked = true;
        if (data != null) {
            int errorCode = data.getInt(WorkerService.EXTRA_ERROR_CODE);
            if (errorCode == ERROR_CODE_UNAUTHORIZED) {
                Timber.d("Ошибка при выполнении запроса ApiService, отсутствует сессия (401)");
                EventBusUtilsKt.postEventOnEventBusScope(new RequestAuthTokenEvent(new RequestAuthTokenEvent.AuthListener() {
                    @Override
                    public void onSuccess() {
                        doExecute();
                    }

                    @Override
                    public void onFailure() {
                        sendUpdate(RESPONSE_FAILURE, data);
                    }
                }));
                return true;
            }
        }
        return false;
    }

    private void sendUpdate(int resultCode, Bundle data) {
        CommandExecutorInterface executor = getExecutor();
        if (executor == null || executor.isCommandInQueue(this)) {
            EventBusUtilsKt.postEventOnEventBusScope(new RequestIsDoneEvent(resultCode, data));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mCommandName);
    }

    public BaseCommand(@NonNull Parcel in) {
        mCommandName = in.readString();
    }
}