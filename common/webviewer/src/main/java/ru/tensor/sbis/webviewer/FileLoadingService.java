package ru.tensor.sbis.webviewer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import ru.tensor.sbis.entrypoint_guard.service.EntryPointIntentService;
import ru.tensor.sbis.event_bus.EventBusUtilsKt;
import ru.tensor.sbis.network_native.error.SbisError;
import ru.tensor.sbis.network_native.httpclient.FileDownloadEvent;
import ru.tensor.sbis.network_native.httpclient.Server;
import ru.tensor.sbis.pushnotification_utils.PendingIntentSupportUtils;
import ru.tensor.sbis.pushnotification_utils.notification.channels.NotificationChannelUtils;
import ru.tensor.sbis.webviewer.data.FileLoadedEvent;
import timber.log.Timber;

/**
 * Сервис для скачивания файлов
 * <p>
 * {@link FileLoadingService#loadFile}
 * <p>
 * Используется в WebView, {@link DocumentWebView.LoadFileByLinkInterface#onFileLoading}
 * <p>
 * В других местах использовать только при острой нужде, например,
 * если требуется отображать уведомлени о скачивании.
 * В прочих сценариях следует рассмотреть скачивание через контроллер, см. DownloadFeature
 *
 * @author ma.kolpakov
 */
public class FileLoadingService extends EntryPointIntentService {
    private static final String KEY_URL = "url";
    private static final String KEY_FILE = "file";
    private static final String KEY_INTENT = "intent";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_ACTION = "action";

    private static final int SERVICE_ID = 166458;

    private final Set<String> mLoadingUuidSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<String> mLoadingUrlSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final FileLoadingBinder mBinder = new FileLoadingBinder();

    public static void loadFile(@NonNull Context context,
                                @NonNull String url,
                                @NonNull String fileNameToStore,
                                @Nullable String uuid,
                                @NonNull Intent notificationIntent,
                                @NonNull FileLoadedEvent.FileAction action) {
        Intent loadingService = new Intent(context, FileLoadingService.class);
        loadingService.putExtra(KEY_URL, url);
        loadingService.putExtra(KEY_FILE, fileNameToStore);
        loadingService.putExtra(KEY_INTENT, notificationIntent);
        if (uuid != null) {
            loadingService.putExtra(KEY_UUID, uuid);
        }
        loadingService.putExtra(KEY_ACTION, action.getValue());

        context.startService(loadingService);
    }

    public FileLoadingService() {
        super("FileLoadingService");
    }

    @Override
    protected void onReady() {
        // ignored
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(KEY_INTENT)) {
            Intent activityIntent = intent.getParcelableExtra(KEY_INTENT);
            if (activityIntent != null){
                PendingIntent pendingIntent = PendingIntentSupportUtils.getUpdateActivityImmutable(
                        getApplicationContext(),
                        0,
                        activityIntent,
                        0
                );

                NotificationChannelUtils.submitFileLoadingChannel(this);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannelUtils.FILE_LOADING_CHANNEL_ID);
                builder.setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.stat_sys_download)
                        .setContentTitle(getString(R.string.webviewer_notification_title))
                        .setContentText(getString(R.string.webviewer_notification_text))
                        .setOngoing(true);

                startForeground(SERVICE_ID, builder.build());

                if (intent.hasExtra(KEY_UUID)) {
                    mLoadingUuidSet.add(intent.getStringExtra(KEY_UUID));
                }
                if (intent.hasExtra(KEY_URL)) {
                    mLoadingUrlSet.add(intent.getStringExtra(KEY_URL));
                }
            } else{
                Timber.e("Activity intent is null. Main intent info: %s", intent.toUri(0));
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    protected void onHandleIntent(@Nullable final Intent intent) {
        if (intent != null) {
            final String url = intent.getStringExtra(KEY_URL);
            final String file = intent.getStringExtra(KEY_FILE);
            final String uuid = intent.getStringExtra(KEY_UUID);
            final int actionValue = intent.getIntExtra(KEY_ACTION, 0);

            if ((uuid != null && url != null) && (!mLoadingUuidSet.contains(uuid) || !mLoadingUrlSet.contains(url))) {
                return;
            }

            if (url != null && !url.isEmpty() && file != null && !file.isEmpty()) {
                Server.getInstance().downloadFile(url, new File(file), new FileDownloadEvent() {
                    @Override
                    public void onSuccess() {
                        postResult(true, file, uuid, url, actionValue);
                    }

                    @Override
                    public void onFail(SbisError sbisError) {
                        File badFile = new File(file);

                        if (badFile.exists()) {
                            //noinspection ResultOfMethodCallIgnored
                            badFile.delete();
                        }

                        postResult(false, file, uuid, url, actionValue);
                    }

                    @Override
                    public void onProgress(int i) {

                    }
                });
            }

            if (uuid != null) {
                mLoadingUuidSet.remove(uuid);
            }
            if (url != null) {
                mLoadingUrlSet.remove(url);
            }
        }
    }

    private void postResult(boolean isSuccess, String file, String uuid, String url, int action) {
        EventBusUtilsKt.postEventOnEventBusScope(new FileLoadedEvent(isSuccess, file, uuid, url, FileLoadedEvent.FileAction.fromInt(action)));
    }

    public class FileLoadingBinder extends android.os.Binder {
        @SuppressWarnings("unused")
        public boolean isAttachmentLoading(@NonNull String attachmentUuid) {
            return mLoadingUuidSet.contains(attachmentUuid);
        }

        @SuppressWarnings("unused")
        @NonNull
        public ArrayList<String> getLoadingAttachments() {
            return new ArrayList<>(mLoadingUuidSet);
        }

        @NonNull
        public ArrayList<String> getLoadingUrls() {
            return new ArrayList<>(mLoadingUrlSet);
        }
    }
}
