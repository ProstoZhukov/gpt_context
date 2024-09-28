package ru.tensor.sbis.scanner.adapter.scannedimagelist.item;

import androidx.databinding.ObservableField;
import androidx.annotation.NonNull;
import android.util.SparseArray;

import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem;
import ru.tensor.sbis.base_components.adapter.universal.pager.Rebindable;
import ru.tensor.sbis.scanner.BR;
import ru.tensor.sbis.scanner.data.model.ScannerPage;
import ru.tensor.sbis.scanner.data.model.Rotation;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;

/**
 * @author am.boldinov
 */
public class ScannedImageListItem extends UniversalBindingItem implements Rebindable<ScannedImageListItem> {

    @NonNull
    private final String mAbsUri;
    @NonNull
    private final ScannerPage mScannerPage;
    @NonNull
    private Rotation mRotation = Rotation.DEFAULT;
    private ObservableField<Rotation> mRotationObservable;

    public ScannedImageListItem(@NonNull String uuid, @NonNull String absUri, @NonNull ScannerPage scannerPage) {
        super(uuid);
        mAbsUri = absUri;
        mScannerPage = scannerPage;
    }

    @NonNull
    public String getAbsoluteUri() {
        return mAbsUri;
    }

    @NonNull
    public ScannerPageInfo getPageInfo() {
        return mScannerPage.getPageInfo();
    }

    public int getPagePosition() {
        return mScannerPage.getPosition();
    }

    public void rotateCcw() {
        mRotation = mRotation.nextRotation(false);
        mRotationObservable.set(mRotation);
    }

    public void setPageInfo(@NonNull ScannerPageInfo scannerPageInfo) {
        if (mScannerPage.getPageInfo().getTotalRotateAngle() != scannerPageInfo.getTotalRotateAngle()) {
            resetRotation();
        }
        mScannerPage.setScannerPageInfo(scannerPageInfo);
    }

    @NonNull
    public Rotation getRotation() {
        return mRotation;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @NonNull
    @Override
    protected SparseArray<Object> createBindingVariables() {
        mRotationObservable = new ObservableField<>();
        SparseArray<Object> variables = new SparseArray<>(2);
        variables.put(BR.ScannedImageListItem, this);
        variables.put(BR.Rotation, mRotationObservable);
        return variables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScannedImageListItem that = (ScannedImageListItem) o;

        if (!mAbsUri.equals(that.mAbsUri)) return false;
        if (!mScannerPage.equals(that.mScannerPage)) return false;
        return mRotation == that.mRotation;
    }

    @Override
    public int hashCode() {
        int result = mAbsUri.hashCode();
        result = 31 * result + mScannerPage.hashCode();
        result = 31 * result + mRotation.hashCode();
        return result;
    }

    @Override
    public void rebind(@NonNull ScannedImageListItem scannedImageListItem) {
        if (scannedImageListItem.getPageInfo().getTotalRotateAngle() == getPageInfo().getTotalRotateAngle()) {
            mRotation = scannedImageListItem.getRotation();
            mRotationObservable.set(mRotation);
        } else { // Физическое состояние изображения изменилось - нужно сбросить состояние canvas
            resetRotation();
        }
    }

    private void resetRotation() {
        mRotation = Rotation.DEFAULT;
        mRotationObservable.set(null);
    }
}
