package ru.tensor.sbis.common.di;

import android.content.Context;

import androidx.annotation.NonNull;

import io.reactivex.Observable;
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext;
import ru.tensor.sbis.design.utils.image_loading.reloadmanager.ImageReloadManager;
import ru.tensor.sbis.fresco_view.util.draweeview.DraweeViewRetryManager;

/**
 * Created by ba.zelenin on 24.05.2018.
 */
public class CommonSingletonComponentInitializer {

    @NonNull
    public CommonSingletonComponent init(
            @NonNull Context applicationContext,
            @NonNull SbisThemedContext themedApplicationContext
    ) {
        CommonSingletonComponent component = DaggerCommonSingletonComponent.factory().create(
                applicationContext,
                themedApplicationContext
        );
        Observable<Boolean> networkStateObservable =
                component.getNetworkUtils().nonDistinctNetworkStateObservable();
        DraweeViewRetryManager.INSTANCE.initialize(networkStateObservable);
        ImageReloadManager.INSTANCE.initialize(networkStateObservable);
        return component;
    }
}