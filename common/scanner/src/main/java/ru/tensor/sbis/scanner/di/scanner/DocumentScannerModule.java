package ru.tensor.sbis.scanner.di.scanner;

import androidx.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.util.uri.UriWrapper;
import ru.tensor.sbis.scanner.data.ScannerResultSupplier;
import ru.tensor.sbis.scanner.data.interactor.ScannerPageInteractor;
import ru.tensor.sbis.scanner.data.mapper.CornerPointListMapper;
import ru.tensor.sbis.scanner.data.DocumentScannerService;
import ru.tensor.sbis.scanner.data.DocumentScannerServiceImpl;
import ru.tensor.sbis.scanner.ui.DocumentScannerContract;
import ru.tensor.sbis.scanner.ui.DocumentScannerPresenterImpl;
import ru.tensor.sbis.scanner.generated.ScannerApi;
import ru.tensor.sbis.scanner.data.ScannerEventManager;

/**
 * @author am.boldinov
 */
@Module
public class DocumentScannerModule {

    @NonNull
    @Provides
    @DocumentScannerScope
    public ScannerResultSupplier provideResultSupplier(@NonNull String requestCode, @NonNull UriWrapper uriWrapper,
                                                       @NonNull ScannerEventManager eventManager) {
        return new ScannerResultSupplier(requestCode, uriWrapper, eventManager);
    }

    @NonNull
    @Provides
    @DocumentScannerScope
    public DocumentScannerContract.Presenter providePresenter(@NonNull DocumentScannerService scannerService, @NonNull ScannerPageInteractor pageInteractor,
                                                              @NonNull UriWrapper uriWrapper, @NonNull ScannerResultSupplier resultSupplier) {
        return new DocumentScannerPresenterImpl(scannerService, pageInteractor, uriWrapper, resultSupplier);
    }

    @NonNull
    @Provides
    @DocumentScannerScope
    public DocumentScannerService provideScannerService(@NonNull DependencyProvider<ScannerApi> scannerApi, @NonNull CornerPointListMapper pointListMapper) {
        return new DocumentScannerServiceImpl(scannerApi, pointListMapper);
    }

    @NonNull
    @Provides
    @DocumentScannerScope
    public CornerPointListMapper provideCornerPointMapper() {
        return new CornerPointListMapper();
    }
}
