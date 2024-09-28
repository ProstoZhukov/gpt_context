package ru.tensor.sbis.scanner.di.scannedimagelist;

import androidx.annotation.NonNull;

import dagger.BindsInstance;
import dagger.Component;
import ru.tensor.sbis.scanner.di.ScannerSingletonComponent;
import ru.tensor.sbis.scanner.ui.scannedimagelist.ScannedImageListContract;

/**
 * @author am.boldinov
 */
@ScannedImageListScope
@Component(
        dependencies = {
                ScannerSingletonComponent.class
        },
        modules = {
                ScannedImageListModule.class
        }
)
public interface ScannedImageListComponent {

    ScannedImageListContract.Presenter getScannedImageListPresenter();

    @Component.Builder
    interface Builder {

        @NonNull
        @BindsInstance
        Builder requestCode(@NonNull String requestCode);

        @NonNull
        Builder scannerSingletonComponent(@NonNull ScannerSingletonComponent scannerSingletonComponent);

        @NonNull
        ScannedImageListComponent build();
    }
}
