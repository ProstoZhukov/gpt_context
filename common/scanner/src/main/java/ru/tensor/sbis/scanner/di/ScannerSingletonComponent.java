package ru.tensor.sbis.scanner.di;

import android.content.Context;
import androidx.annotation.NonNull;
import dagger.Component;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.di.CommonSingletonComponent;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.common.util.uri.UriWrapper;
import ru.tensor.sbis.common.util.di.PerApp;
import ru.tensor.sbis.mvp.interactor.FileInteractor;
import ru.tensor.sbis.scanner.data.interactor.ScannerPageInteractor;
import ru.tensor.sbis.scanner.generated.ScannerApi;
import ru.tensor.sbis.scanner.data.ScannerEventManager;
import ru.tensor.sbis.storage.contract.ExternalStorageProvider;
import ru.tensor.sbis.storage.external.SbisExternalStorage;

/**
 * @author am.boldinov
 */
@PerApp
@Component(
        dependencies = {
                CommonSingletonComponent.class,
                ExternalStorageProvider.class
        },
        modules = {
                ScannerSingletonModule.class
        }
)
public interface ScannerSingletonComponent {

    Context getContext();

    NetworkUtils getNetworkUtils();

    UriWrapper getUriWrapper();

    ScannerPageInteractor getScannerPageInteractor();

    ScannerEventManager getScannerEventManager();

    DependencyProvider<ScannerApi> getScannerApi();

    SbisExternalStorage getSbisExternalStorage();

    FileInteractor getFileInteractor();

    static ScannerSingletonComponent fromContext(@NonNull Context context) {
        return ScannerSingletonComponentProvider.get(context);
    }
}
