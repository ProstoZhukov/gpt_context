package ru.tensor.sbis.scanner.ui;

import java.util.List;

import androidx.annotation.NonNull;
import ru.tensor.sbis.camera.BaseCameraContract;
import ru.tensor.sbis.camera.service.FlashState;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;

/**
 * @author am.boldinov
 */
public interface DocumentScannerContract {

    String EXTRA_REQUEST_CODE_KEY = "EXTRA_REQUEST_CODE";
    String EXTRA_SCANNER_PAGE_INFO_KEY = "EXTRA_SCANNER_PAGE_KEY";

    interface View extends BaseCameraContract.View {
        void displayRectangle(@NonNull List<CornerPoint> coordinates);

        void setFlashViewVisibility(boolean visibility);

        void switchFlashView(@NonNull FlashState flashState);

        void switchToScannerResultScreen(@NonNull ScannerPageInfo scannerPageInfo);

        void showTakeSnapshotError();

        void setEnabledControls(boolean enabled);

        void showProgress();

        void hideProgress();

        void displayNextPage(int page);

        void displayLastSnapshot(@NonNull String imageUri);

        void inflateSingleSnapshotLayout();

        void inflateMultiSnapshotLayout();

        void removeSnapshotLayout();

        void dispatchResult(@NonNull List<String> uriList);

        /**
         * Отобразить диалог отсутствия доступа к камере.
         */
        void displayPermissionsDialog();

        /**
         * Открыть настройки приложения.
         */
        void openAppSettings();

        void finish();
    }

    interface Presenter extends BaseCameraContract.Presenter<View> {
        void onSwitchFlashClick();

        void onSnapshotClick();

        void onFastSnapshotClick();

        void onScannerFinishing();

        void onLastSnapshotPreviewClick();

        /**
         * Обработать нажатие на кнопку настроек в диалоге отсутствия доступа к камере.
         */
        void onPermissionDialogSettingsClick();

        /**
         * Обработать нажатие на кнопку отмены в диалоге отсутствия доступа к камере.
         */
        void onPermissionDialogCancel();

        /**
         * Обработать закрытие экрана настроек приложения.
         */
        void onAppSettingsClosed();

        boolean onBackPressed();
    }
}
