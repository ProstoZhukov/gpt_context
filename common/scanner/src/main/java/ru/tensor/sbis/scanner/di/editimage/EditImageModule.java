package ru.tensor.sbis.scanner.di.editimage;

import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.util.uri.UriWrapper;
import ru.tensor.sbis.mvp.interactor.FileInteractor;
import ru.tensor.sbis.scanner.data.interactor.ScannerPageInteractor;
import ru.tensor.sbis.scanner.data.interactor.editimage.EditImageInteractor;
import ru.tensor.sbis.scanner.data.interactor.editimage.EditImageInteractorImpl;
import ru.tensor.sbis.scanner.data.interactor.scannedimagelist.ScannedImageListInteractor;
import ru.tensor.sbis.scanner.data.interactor.scannedimagelist.ScannedImageListInteractorImpl;
import ru.tensor.sbis.scanner.data.mapper.ScannedImageModelMapper;
import ru.tensor.sbis.scanner.data.mapper.CornerCoordinatesListMapper;
import ru.tensor.sbis.scanner.data.mapper.CornerPointListMapper;
import ru.tensor.sbis.scanner.ui.editimage.EditImageContract;
import ru.tensor.sbis.scanner.ui.editimage.EditImagePresenterImpl;
import ru.tensor.sbis.scanner.ui.viewimage.ViewImageContract;
import ru.tensor.sbis.scanner.ui.viewimage.ViewImagePresenterImpl;
import ru.tensor.sbis.scanner.generated.ScannerApi;
import ru.tensor.sbis.storage.external.SbisExternalStorage;

/**
 * @author am.boldinov
 */

@Module
public class EditImageModule {

    @NonNull
    @Provides
    @EditImageScope
    public EditImageContract.Presenter providePresenter(@NonNull EditImageInteractor editImageInteractor, @NonNull UriWrapper uriWrapper,
                                                        @NonNull CornerPointListMapper cornerPointListMapper) {
        return new EditImagePresenterImpl(editImageInteractor, uriWrapper, cornerPointListMapper);
    }

    @NonNull
    @Provides
    @EditImageScope
    public EditImageInteractor provideEditImageInteractor(@NonNull CornerCoordinatesListMapper coordinatesListMapper,
                                                          @NonNull UriWrapper uriWrapper, @NonNull DependencyProvider<ScannerApi> scannerApi) {
        return new EditImageInteractorImpl(coordinatesListMapper, uriWrapper, scannerApi);
    }

    @NonNull
    @Provides
    @EditImageScope
    public ScannedImageModelMapper provideScannedImageModelMapper(@NonNull UriWrapper uriWrapper) {
        return new ScannedImageModelMapper(uriWrapper);
    }

    @NonNull
    @Provides
    @EditImageScope
    public ScannedImageListInteractor provideScannedImageListInteractor(@NonNull DependencyProvider<ScannerApi> scannerApi,
                                                                        @NonNull ScannedImageModelMapper scannedImageModelMapper,
                                                                        @NonNull ScannerPageInteractor pageInteractor,
                                                                        @NonNull FileInteractor fileInteractor,
                                                                        @NonNull SbisExternalStorage sbisExternalStorage) {
        return new ScannedImageListInteractorImpl(scannerApi, scannedImageModelMapper, pageInteractor, fileInteractor, sbisExternalStorage);
    }

    @NonNull
    @Provides
    @EditImageScope
    public ViewImageContract.Presenter provideViewImagePresenter(@NonNull EditImageInteractor editImageInteractor, @NonNull ScannedImageListInteractor imageListInteractor) {
        return new ViewImagePresenterImpl(editImageInteractor, imageListInteractor);
    }

    @NonNull
    @Provides
    @EditImageScope
    public CornerPointListMapper provideCornerPointMapper() {
        return new CornerPointListMapper();
    }

    @NonNull
    @Provides
    @EditImageScope
    public CornerCoordinatesListMapper providerCornerCoordinatesMapper() {
        return new CornerCoordinatesListMapper();
    }
}
