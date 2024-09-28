package ru.tensor.sbis.common.di;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import ru.tensor.sbis.common.util.AlternativeNetworkUtils;
import ru.tensor.sbis.design.R;
import ru.tensor.sbis.common.feature.AndroidSystem;
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker;
import ru.tensor.sbis.common.rx.RxBus;
import ru.tensor.sbis.common.util.ClipboardManager;
import ru.tensor.sbis.common.util.DeviceUtils;
import ru.tensor.sbis.common.util.FileUriUtil;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.common.util.ResourceProvider;
import ru.tensor.sbis.common.util.StringProvider;
import ru.tensor.sbis.common.util.date.DateFormatUtils;
import ru.tensor.sbis.common.util.scroll.ScrollHelper;
import ru.tensor.sbis.common.util.scroll.ScrollHelperImpl;
import ru.tensor.sbis.common.util.uri.UriWrapper;
import ru.tensor.sbis.common.util.uri.UriWrapperImpl;

/**
 * Created by kabramov on 25.01.17.
 */
@Module
public class CommonSingletonModule {

    //region Utils etc
    @NonNull
    @Provides
    @CommonScope
    SharedPreferences provideDefaultSharedPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    @Provides
    @CommonScope
    NetworkUtils provideNetworkUtils(@NonNull Context context) {
        return new NetworkUtils(context);
    }

    @NonNull
    @Provides
    @CommonScope
    AlternativeNetworkUtils provideAlternativeNetworkUtils(@NonNull Context context) {
        return new AlternativeNetworkUtils(context);
    }

    @NonNull
    @Provides
    @CommonScope
    FileUriUtil provideFileUriUtil(@NonNull Context context) {
        return new FileUriUtil(context);
    }

    @NonNull
    @Provides
    @CommonScope
    DateFormatUtils provideDateFormatUtils() {
        return new DateFormatUtils();
    }

    @NonNull
    @Provides
    @CommonScope
    StringProvider provideStringProvider(@NonNull Context context) {
        return new StringProvider(context);
    }

    @NonNull
    @Provides
    @CommonScope
    ClipboardManager provideClipboardManager(@NonNull Context context) {
        return new ClipboardManager(context);
    }

    @NonNull
    @Provides
    @CommonScope
    AppLifecycleTracker provideAppLifecycleTracker() {
        return AppLifecycleTracker.INSTANCE;
    }

    @NonNull
    @Provides
    @CommonScope
    UriWrapper provideUriWrapper(@NonNull Context context) {
        return new UriWrapperImpl(context);
    }

    @NonNull
    @Provides
    @CommonScope
    RxBus provideRxBus() {
        return new RxBus();
    }
    //endregion

    @NonNull
    @Provides
    @CommonScope
    ContentResolver provideContentResolver(@NonNull Context context) {
        return context.getContentResolver();
    }

    @NonNull
    @Provides
    @CommonScope
    AssetManager provideAssetManager(@NonNull Context context) {
        return context.getAssets();
    }

    @NonNull
    @Provides
    @CommonScope
    ScrollHelper provideScrollEvent(@NonNull Context context) {
        return new ScrollHelperImpl(context.getResources().getDimension(R.dimen.bottom_navigation_height));
    }

    @NonNull
    @Provides
    @CommonScope
    DeviceUtils provideDeviceUtils(@NonNull Context context) {
        return new DeviceUtils(context);
    }

    @NonNull
    @Provides
    @CommonScope
    ResourceProvider provideResourceProvider(@NonNull Context context) {
        return new ResourceProvider(context);
    }

    @NonNull
    @Provides
    @CommonScope
    AndroidSystem provideAndroidSystem(@NonNull ContentResolver contentResolver,
                                       @NonNull AssetManager assetManager,
                                       @NonNull SharedPreferences sharedPreferences) {
        return new AndroidSystem(contentResolver, assetManager, sharedPreferences);
    }

}
