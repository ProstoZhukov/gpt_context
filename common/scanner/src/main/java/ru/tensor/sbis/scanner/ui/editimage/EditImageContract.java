package ru.tensor.sbis.scanner.ui.editimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.tensor.sbis.mvp.presenter.BasePresenter;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.data.model.Rotation;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;

/**
 * @author am.boldinov
 */
public interface EditImageContract {
    interface View {
        void displayImage(@NonNull String imageUri, @Nullable List<CornerPoint> cornerPointList, int imageWidth, int imageHeight);

        void rotateImage();

        void finishScreen();

        void finishScreenWithResult(@NonNull ScannerPageInfo scannerPageInfo);

        void showImageProcessingError();

        void showWaitProgress();

        void hideWaitProgress();
    }

    interface Presenter extends BasePresenter<View> {
        void onCancelButtonClick();

        void onDoneButtonClick(@NonNull Rotation rotation, @Nullable List<CornerPoint> cornerPoints);

        void onRotateButtonClick();

        void setScannerPageInfo(@Nullable ScannerPageInfo scannerPageInfo);
    }
}
