package ru.tensor.sbis.scanner.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.jetbrains.annotations.Nullable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import ru.tensor.sbis.base_components.AloneFragmentContainerActivity;
import ru.tensor.sbis.design.view_ext.UiUtils;

/**
 * @author am.boldinov
 */
public class DocumentScannerActivity extends AloneFragmentContainerActivity {

    public static final String EXTRA_SCANNED_IMAGE_LIST_RESULT = "EXTRA_SCANNED_IMAGE_LIST_RESULT";

    @NonNull
    public static Intent getActivityIntent(@NonNull Context context, @NonNull String requestCode) {
        Intent intent = new Intent(context, DocumentScannerActivity.class);
        intent.putExtra(DocumentScannerContract.EXTRA_REQUEST_CODE_KEY, requestCode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiUtils.disableActivityRotation(this);
    }

    @Nullable
    @Override
    public Fragment createFragment() {
        return DocumentScannerFragment.newInstance();
    }
}
