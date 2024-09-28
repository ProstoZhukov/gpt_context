package ru.tensor.sbis.scanner.data.model;

import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;

/**
 * @author am.boldinov
 */
public class ScannerPage {

    private ScannerPageInfo mScannerPageInfo;
    private int mPosition = -1;

    public ScannerPage() {
        // empty page
    }

    public ScannerPage(ScannerPageInfo scannerPageInfo, int position) {
        mScannerPageInfo = scannerPageInfo;
        mPosition = position;
    }

    public ScannerPageInfo getPageInfo() {
        return mScannerPageInfo;
    }

    public void setScannerPageInfo(ScannerPageInfo scannerPageInfo) {
        mScannerPageInfo = scannerPageInfo;
    }

    public int getPosition() {
        return mPosition;
    }

    public boolean isEmpty() {
        return mScannerPageInfo == null || CommonUtils.isEmpty(mScannerPageInfo.getImageCroppedPath()) || mPosition == -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScannerPage that = (ScannerPage) o;

        if (mPosition != that.mPosition) return false;
        return mScannerPageInfo != null ? mScannerPageInfo.equals(that.mScannerPageInfo) : that.mScannerPageInfo == null;
    }

    @Override
    public int hashCode() {
        int result = mScannerPageInfo != null ? mScannerPageInfo.hashCode() : 0;
        result = 31 * result + mPosition;
        return result;
    }
}
