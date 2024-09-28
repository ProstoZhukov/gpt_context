package ru.tensor.sbis.scanner.di.scanner;

import androidx.annotation.NonNull;

import dagger.BindsInstance;
import dagger.Component;
import ru.tensor.sbis.scanner.di.ScannerSingletonComponent;
import ru.tensor.sbis.scanner.ui.DocumentScannerContract;

/**
 * @author am.boldinov
 */
@DocumentScannerScope
@Component(
        dependencies = {
                ScannerSingletonComponent.class
        },
        modules = {
                DocumentScannerModule.class
        }
)
public interface DocumentScannerComponent {

    DocumentScannerContract.Presenter getPresenter();

    @Component.Builder
    interface Builder {

        @NonNull
        @BindsInstance
        Builder requestCode(@NonNull String requestCode);

        @NonNull
        Builder scannerSingletonComponent(@NonNull ScannerSingletonComponent scannerSingletonComponent);

        @NonNull
        DocumentScannerComponent build();
    }
}
