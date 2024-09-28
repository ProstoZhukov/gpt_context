package ru.tensor.sbis.scanner.di.scannedimagelist;

import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import ru.tensor.sbis.base_components.adapter.checkable.ObservableCheckCountHelper;
import ru.tensor.sbis.base_components.adapter.checkable.impl.ObservableCheckCountHelperImpl;
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.common.util.uri.UriWrapper;
import ru.tensor.sbis.mvp.interactor.FileInteractor;
import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem;
import ru.tensor.sbis.scanner.data.ScannerResultSupplier;
import ru.tensor.sbis.scanner.data.interactor.ScannerPageInteractor;
import ru.tensor.sbis.scanner.data.interactor.scannedimagelist.ScannedImageListInteractor;
import ru.tensor.sbis.scanner.data.interactor.scannedimagelist.ScannedImageListInteractorImpl;
import ru.tensor.sbis.scanner.data.mapper.ScannedImageModelMapper;
import ru.tensor.sbis.scanner.data.ScannerEventManager;
import ru.tensor.sbis.scanner.ui.scannedimagelist.ScannedImageListContract;
import ru.tensor.sbis.scanner.ui.scannedimagelist.ScannedImageListPresenterImpl;
import ru.tensor.sbis.scanner.generated.ScannerApi;
import ru.tensor.sbis.storage.external.SbisExternalStorage;

/**
 * @author am.boldinov
 */
@Module
public class ScannedImageListModule {

    @NonNull
    @Provides
    @ScannedImageListScope
    public ScannedImageModelMapper provideScannedImageModelMapper(@NonNull UriWrapper uriWrapper) {
        return new ScannedImageModelMapper(uriWrapper);
    }

    @NonNull
    @Provides
    @ScannedImageListScope
    public ScannedImageListInteractor provideScannedImageListInteractor(@NonNull DependencyProvider<ScannerApi> scannerApi,
                                                                        @NonNull ScannedImageModelMapper scannedImageModelMapper,
                                                                        @NonNull ScannerPageInteractor pageInteractor,
                                                                        @NonNull FileInteractor fileInteractor,
                                                                        @NonNull SbisExternalStorage sbisExternalStorage) {
        return new ScannedImageListInteractorImpl(scannerApi, scannedImageModelMapper, pageInteractor, fileInteractor, sbisExternalStorage);
    }

    @NonNull
    @Provides
    @ScannedImageListScope
    public ObservableCheckCountHelper<ScannedImageListItem> provideObservableCheckHelper() {
        return new ObservableCheckCountHelperImpl<>(UniversalBindingItem::getItemTypeId);
    }

    @NonNull
    @Provides
    @ScannedImageListScope
    public ScannerResultSupplier provideResultSupplier(@NonNull String requestCode,
                                                       @NonNull UriWrapper uriWrapper,
                                                       @NonNull ScannerEventManager scannerEventManager) {
        return new ScannerResultSupplier(requestCode, uriWrapper, scannerEventManager);
    }

    @NonNull
    @Provides
    @ScannedImageListScope
    public ScannedImageListContract.Presenter providePresenter(@NonNull ScannedImageListInteractor interactor,
                                                               @NonNull ObservableCheckCountHelper<ScannedImageListItem> checkHelper,
                                                               @NonNull NetworkUtils networkUtils,
                                                               @NonNull ScannerResultSupplier resultSupplier) {
        return new ScannedImageListPresenterImpl(interactor, checkHelper, networkUtils, resultSupplier);
    }
}
