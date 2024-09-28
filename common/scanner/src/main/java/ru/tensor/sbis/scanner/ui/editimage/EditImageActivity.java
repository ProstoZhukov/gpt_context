package ru.tensor.sbis.scanner.ui.editimage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import ru.tensor.sbis.base_components.AloneFragmentContainerActivity;
import ru.tensor.sbis.design.view_ext.UiUtils;
import ru.tensor.sbis.scanner.ui.DocumentScannerContract;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;

/**
 * @author am.boldinov
 */
public class EditImageActivity extends AloneFragmentContainerActivity {

    @NonNull
    public static Intent getActivityIntent(@NonNull Activity parent, @NonNull ScannerPageInfo scannerPageInfo) {
        Intent intent = new Intent(parent, EditImageActivity.class);
        intent.putExtra(DocumentScannerContract.EXTRA_SCANNER_PAGE_INFO_KEY, scannerPageInfo);
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
        return EditImageFragment.newInstance();
    }
}
