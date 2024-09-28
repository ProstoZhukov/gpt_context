package ru.tensor.sbis.common.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import ru.tensor.sbis.entrypoint_guard.bcr.EntryPointBroadcastReceiver;

/**
 * Class that provides events when device screen was on or off.
 */
public class ScreenStateUtils {

    @NonNull
    private final Context mContext;

    @NonNull
    private final IntentFilter mScreenStateIntentFilter;

    @NonNull
    private final PublishSubject<Boolean> mScreenStateSubject;

    @NonNull
    private final Observable<Boolean> mScreenStateObservable;

    public ScreenStateUtils(@NonNull Context context) {
        mContext = context;
        mScreenStateIntentFilter = new IntentFilter();
        mScreenStateIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        mScreenStateIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenStateSubject = PublishSubject.create();
        final BroadcastReceiver screenStateReceiver = new ScreenStatusReceiver();

        mScreenStateObservable = mScreenStateSubject
                .distinctUntilChanged()
                .doOnSubscribe(disposable ->
                        ContextCompat.registerReceiver(
                                mContext,
                                screenStateReceiver,
                                mScreenStateIntentFilter,
                                ContextCompat.RECEIVER_NOT_EXPORTED
                        )
                )
                .doOnDispose(() -> mContext.unregisterReceiver(screenStateReceiver))
                .replay(1)
                .refCount();
    }

    @NonNull
    public Observable<Boolean> screenStateObservable() {
        return mScreenStateObservable;
    }

    private class ScreenStatusReceiver extends EntryPointBroadcastReceiver {

        @Override
        protected void onReady(@NonNull Context context, @NonNull Intent intent) {
            mScreenStateSubject.onNext(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SCREEN_ON));
        }

    }

}
