package ru.tensor.sbis.scanner.ui.viewimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import ru.tensor.sbis.mvp.presenter.BasePresenter;
import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;

/**
 * @author am.boldinov
 */
public interface ViewImageContract {
    interface View {
        void switchToEditImageScreen(@NonNull ScannerPageInfo scannerPageInfo);
        void switchToImageListScreen();
        void switchToScannerScreen();
        void switchToMainScreen();
        void setEnabledControls(boolean enabled);
        void showWaitProgress();
        void hideWaitProgress();
        void showImageProcessingError();
        void showImageDeletingError();
        void showImageLoadingError();
        void displayCurrentPage(int page, boolean smooth);
        void displayImageList(@NonNull List<ScannedImageListItem> imageList);
    }

    interface Presenter extends BasePresenter<View>, ViewPager.OnPageChangeListener {
        void setScannerPageInfo(@Nullable ScannerPageInfo scannerPageInfo);
        void onCloseButtonClick();
        void onDoneButtonClick();
        void onEditButtonClick();
        void onRotateButtonClick();
        void onDeleteButtonClick();
        void onNextSnapshotButtonClick();
        void onViewStarted();
    }
}
