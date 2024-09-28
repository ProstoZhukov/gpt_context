package ru.tensor.sbis.edo_decl.scanner;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

/**
 * @author am.boldinov
 */
public interface ScannerIntentProvider extends ScannerEventProvider {

    @NonNull
    Intent getScannerActivityIntent(@NonNull Context context, @NonNull String requestCode);
}
