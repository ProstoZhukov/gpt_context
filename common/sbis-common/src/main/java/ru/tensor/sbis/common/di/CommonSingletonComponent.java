package ru.tensor.sbis.common.di;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import dagger.BindsInstance;
import dagger.Component;
import kotlin.Deprecated;
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext;
import ru.tensor.sbis.common.feature.AndroidSystem;
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker;
import ru.tensor.sbis.common.rx.RxBus;
import ru.tensor.sbis.common.util.AlternativeNetworkUtils;
import ru.tensor.sbis.common.util.ClipboardManager;
import ru.tensor.sbis.common.util.DeviceUtils;
import ru.tensor.sbis.common.util.FileUriUtil;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.common.util.ResourceProvider;
import ru.tensor.sbis.common.util.StringProvider;
import ru.tensor.sbis.common.util.date.DateFormatUtils;
import ru.tensor.sbis.common.util.scroll.ScrollHelper;
import ru.tensor.sbis.common.util.uri.UriWrapper;
import ru.tensor.sbis.plugin_struct.feature.Feature;

/**
 * Created by kabramov on 25.01.17.
 */
@CommonScope
@Component(
        modules = {
                CommonSingletonModule.class
        }
)
public interface CommonSingletonComponent extends Feature {

    // getters here
    @Deprecated(message = "Используйте context из вашего компонента. Данный метод предназначен только для внутреннего использования внутри модуля (аля internal)")
    Context getContext();

    SbisThemedContext getThemedContext();

    AppLifecycleTracker getAppLifecycleTracker();

    NetworkUtils getNetworkUtils();

    AlternativeNetworkUtils getAlternativeNetworkUtils();

    FileUriUtil getFileUriUtil();

    ContentResolver getContentResolver();

    AssetManager getAssetManager();

    DateFormatUtils getDateFormatUtils();

    UriWrapper getUriWrapper();

    StringProvider getStringProvider();

    ClipboardManager getClipboardManager();

    RxBus getRxBus();

    SharedPreferences getSharedPreferences();

    ScrollHelper getScrollHelper();

    DeviceUtils getDeviceUtils();

    ResourceProvider getResourceProvider();

    AndroidSystem getAndroidSystem();

    //endregion

    @Component.Factory
    interface Factory {

        CommonSingletonComponent create(
                @BindsInstance @NonNull Context applicationContext,
                @BindsInstance @NonNull SbisThemedContext themedApplicationContext
        );
    }

    public static String COMMON_THEMED_APP_CONTEXT = "COMMON_THEMED_APP_CONTEXT";
}
