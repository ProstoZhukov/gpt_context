package ru.tensor.sbis.scanner.di;

import android.content.Context;
import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.util.di.PerApp;
import ru.tensor.sbis.mvp.interactor.FileInteractor;
import ru.tensor.sbis.scanner.data.interactor.ScannerPageInteractor;
import ru.tensor.sbis.scanner.data.interactor.ScannerPageInteractorImpl;
import ru.tensor.sbis.scanner.generated.ScannerApi;
import ru.tensor.sbis.scanner.data.ScannerEventManager;

/**
 * @author am.boldinov
 */
@Module
class ScannerSingletonModule {

    @NonNull
    @Provides
    @PerApp
    FileInteractor provideFileInteractor(@NonNull Context context) {
        return new FileInteractor(context);
    }

    @NonNull
    @Provides
    @PerApp
    ScannerPageInteractor provideScannerPageInteractor(@NonNull DependencyProvider<ScannerApi> scannerApi) {
        return new ScannerPageInteractorImpl(scannerApi);
    }

    @NonNull
    @Provides
    @PerApp
    DependencyProvider<ScannerApi> provideScannerApi(@NonNull Context context) {
        return DependencyProvider.create(() -> {
            ScannerApi.setTempFolder(context.getFilesDir().getAbsolutePath().concat("/scans"));
            return ScannerApi.instance();
        });
    }

    @NonNull
    @Provides
    @PerApp
    ScannerEventManager provideScannerEventManager() {
        return new ScannerEventManager();
    }
}
