package ru.tensor.sbis.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.BehaviorSubject;
import ru.tensor.sbis.plugin_struct.feature.Feature;

/**
 * Created by kabramov on 08.02.17.
 */
public class NetworkUtils implements Feature {

    @NonNull
    private final ConnectivityManager mConnectivityManager;
    @NonNull
    private final BehaviorSubject<Object> mNetworkConnectionBehaviorSubject;
    @NonNull
    private final Observable<Boolean> mConnectivityStateObservable;

    public NetworkUtils(@NonNull Context context) {
        mConnectivityManager = getConnectivityManager(context);
        mNetworkConnectionBehaviorSubject = BehaviorSubject.create();
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .build();
        NetworkCallback networkCallback = new NetworkCallback();
        mConnectivityStateObservable = mNetworkConnectionBehaviorSubject
                .doOnSubscribe(disposable -> {
                    mNetworkConnectionBehaviorSubject.onNext(new Object());
                    mConnectivityManager.registerNetworkCallback(networkRequest, networkCallback);
                })
                .doOnDispose(() -> mConnectivityManager.unregisterNetworkCallback(networkCallback))
                .debounce(1, TimeUnit.SECONDS)
                .flatMap(result -> Observable.just(isConnected()))
                .observeOn(AndroidSchedulers.mainThread())
                .replay(1)
                .refCount();
    }

    public static boolean isConnected(@NonNull Context context) {
        return isConnected(getConnectivityManager(context));
    }

    public static boolean isConnected(@NonNull ConnectivityManager connectivityManager) {
        //Оборачиваем в try/catch, т.к. при неясных обстоятельсвах бросается SecurityException
        //Проблема в коде Android 11 https://issuetracker.google.com/issues/175055271
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            } else {
                //Метод getActiveNetworkInfo помечен устаревшим с 29 API
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    switch (activeNetwork.getType()) {
                        case ConnectivityManager.TYPE_MOBILE:
                        case ConnectivityManager.TYPE_WIFI:
                        case ConnectivityManager.TYPE_VPN:
                        case ConnectivityManager.TYPE_ETHERNET:
                            return true;
                        default:
                            return false;
                    }
                }
            }
        } catch (Exception exception) {
            CommonUtils.handleException(exception);
            return false;
        }
        return false;
    }

    public boolean isConnected() {
        return isConnected(mConnectivityManager);
    }

    /**
     * Возвращает имя активной сети
     * Если активная сеть отсутствует или не имеет соединения, то вернётся строка "Disconnected"
     *
     * @deprecated Метод {@link ConnectivityManager#getActiveNetworkInfo()} помечен устаревшим с 29 API,
     * замена для {@link NetworkInfo#getTypeName()} не предложена
     */
    @Deprecated()
    @NonNull
    public String getCurrentNetworkName() {
        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnected()) {
            return "Disconnected";
        } else {
            return activeNetwork.getTypeName();
        }
    }

    /**
     * @return {@link Observable<Boolean>} с событиями изменения доступности сети
     */
    @NonNull
    public Observable<Boolean> networkStateObservable() {
        return mConnectivityStateObservable.distinctUntilChanged();
    }

    /**
     * @return {@link Observable<Boolean>} с событиями изменения доступности сети, включая
     * повторяющиеся (актуально, когда сеть меняется)
     */
    @NonNull
    public Observable<Boolean> nonDistinctNetworkStateObservable() {
        return mConnectivityStateObservable;
    }

    private class NetworkCallback extends ConnectivityManager.NetworkCallback {

        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            mNetworkConnectionBehaviorSubject.onNext(new Object());
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            mNetworkConnectionBehaviorSubject.onNext(new Object());
        }
    }

    @NonNull
    private static ConnectivityManager getConnectivityManager(@NonNull Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
