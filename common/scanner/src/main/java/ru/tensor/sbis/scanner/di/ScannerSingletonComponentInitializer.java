package ru.tensor.sbis.scanner.di;

import androidx.annotation.NonNull;

import ru.tensor.sbis.common.di.BaseSingletonComponentInitializer;
import ru.tensor.sbis.common.di.CommonSingletonComponent;
import ru.tensor.sbis.storage.contract.ExternalStorageProvider;

/**
 * @author am.boldinov
 */
public class ScannerSingletonComponentInitializer extends BaseSingletonComponentInitializer<ScannerSingletonComponent> {
    private final ExternalStorageProvider externalStorageProvider;

    public ScannerSingletonComponentInitializer(@NonNull ExternalStorageProvider externalStorageProvider) {
        this.externalStorageProvider = externalStorageProvider;
    }

    @NonNull
    @Override
    protected ScannerSingletonComponent createComponent(@NonNull CommonSingletonComponent commonSingletonComponent) {
        return DaggerScannerSingletonComponent.builder()
                .externalStorageProvider(externalStorageProvider)
                .commonSingletonComponent(commonSingletonComponent)
                .scannerSingletonModule(new ScannerSingletonModule())
                .build();
    }

    @Override
    protected void initSingletons(@NonNull ScannerSingletonComponent singletonComponent) {

    }
}
