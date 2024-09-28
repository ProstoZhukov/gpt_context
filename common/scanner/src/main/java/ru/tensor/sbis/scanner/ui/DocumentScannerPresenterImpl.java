package ru.tensor.sbis.scanner.ui;

import android.Manifest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import ru.tensor.sbis.camera.BaseCameraPresenterImpl;
import ru.tensor.sbis.camera.service.FlashState;
import ru.tensor.sbis.common.util.uri.UriWrapper;
import ru.tensor.sbis.scanner.data.DocumentScannerService;
import ru.tensor.sbis.scanner.data.ScannerResultSupplier;
import ru.tensor.sbis.scanner.data.interactor.ScannerPageInteractor;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;
import timber.log.Timber;

/**
 * @author am.boldinov
 */
public class DocumentScannerPresenterImpl extends BaseCameraPresenterImpl<DocumentScannerContract.View, DocumentScannerService>
        implements DocumentScannerContract.Presenter {

    @NonNull
    private final FlashState[] mFlashSwitchSequence = {
            FlashState.AUTO, FlashState.ON, FlashState.OFF
    };
    @NonNull
    private FlashState mCurrentFlashState = mFlashSwitchSequence[0];
    @NonNull
    private SnapshotLayoutState mSnapshotLayoutState = SnapshotLayoutState.UNKNOWN;
    @NonNull
    private final CompositeDisposable mDisposables = new CompositeDisposable();
    @NonNull
    private final ScannerPageInteractor mPageInteractor;
    @NonNull
    private final UriWrapper mUriWrapper;
    @NonNull
    private final ScannerResultSupplier mResultSupplier;
    @Nullable
    private ScannerPageInfo mLastSnapshot;

    public DocumentScannerPresenterImpl(@NonNull DocumentScannerService mediaService, @NonNull ScannerPageInteractor pageInteractor,
                                        @NonNull UriWrapper uriWrapper, @NonNull ScannerResultSupplier resultSupplier) {
        super(mediaService);
        mPageInteractor = pageInteractor;
        mUriWrapper = uriWrapper;
        mResultSupplier = resultSupplier;
    }

    @Override
    public void attachView(@NonNull DocumentScannerContract.View view) {
        super.attachView(view);
        view.setEnabledControls(false);
        view.hideProgress();
        resetLastPage();
        subscribeOnPreviewCoordinates();
    }

    @Override
    public void detachView() {
        super.detachView();
        mDisposables.clear();
    }

    @Override
    public void onViewStarted() {
        super.onViewStarted();
        clearDisplayedRectangle();
        displayNextPage();
    }

    @Override
    protected void updateUiAfterCameraPreparing() throws IOException {
        super.updateUiAfterCameraPreparing();
        if (mView != null) {
            mView.setEnabledControls(true);
            if (mMediaService.hasFlash()) {
                mView.setFlashViewVisibility(true);
                mView.switchFlashView(mCurrentFlashState);
                mMediaService.switchFlash(mCurrentFlashState);
            } else {
                mView.setFlashViewVisibility(false);
            }
        }
    }

    @Override
    public void onSwitchFlashClick() {
        mCurrentFlashState = getNextFlashState(mCurrentFlashState);
        if (mView != null) {
            mView.switchFlashView(mCurrentFlashState);
        }
        mMediaService.switchFlash(mCurrentFlashState);
    }

    @Override
    public void onSnapshotClick() {
        takePicture(scannerPageInfo -> {
            if (mView != null) {
                mView.switchToScannerResultScreen(scannerPageInfo);
            }
        });
    }

    @Override
    public void onFastSnapshotClick() {
        takePicture(scannerPageInfo -> {
            if (mView != null) {
                List<String> uris = mResultSupplier.getUrisFromPageInfo(scannerPageInfo);
                mResultSupplier.dispatchResult(uris);
                mView.dispatchResult(uris);
                mView.finish();
            }
        });
    }

    @Override
    public void onScannerFinishing() {
        mPageInteractor.deleteAllPagesAsync();
    }

    @Override
    public void onLastSnapshotPreviewClick() {
        if (mView != null) {
            if (mLastSnapshot != null) {
                mView.switchToScannerResultScreen(mLastSnapshot);
            } else {
                Timber.e("Last snapshot preview was clicked without page info, click missed");
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mLastSnapshot != null) {
            onLastSnapshotPreviewClick();
            return true;
        }
        return false;
    }

    @Override
    public void onPermissionsRequestResult(@Nullable Set<String> grantedPermissions) {
        super.onPermissionsRequestResult(grantedPermissions);
        if (!hasCameraPermissions() && mView != null) {
            mView.displayPermissionsDialog();
        }
    }

    private void subscribeOnPreviewCoordinates() {
        mDisposables.add(
                mMediaService.getCoordinatesObservable().subscribe(coordinates -> {
                    if (mView != null) {
                        mView.displayRectangle(coordinates);
                    }
                })
        );
    }

    private void takePicture(@NonNull Consumer<ScannerPageInfo> successConsumer) {
        if (mView != null) {
            mView.setEnabledControls(false);
        }
        mDisposables.add(
                mMediaService.takePicture(() -> {
                    if (mView != null) {
                        mView.showProgress();
                    }
                }).doAfterTerminate(() -> {
                    if (mView != null) {
                        mView.hideProgress();
                    }
                }).subscribe(successConsumer, throwable -> {
                    if (mView != null) {
                        try {
                            mMediaService.stopPreview();
                            startPreview();
                        } catch (Exception ignored) {
                        }
                        mView.setEnabledControls(true);
                        mView.showTakeSnapshotError();
                    }
                })
        );
    }

    private void resetLastPage() {
        mLastSnapshot = null;
        if (mView != null) {
            mView.displayNextPage(0);
            displaySnapshotLayout(SnapshotLayoutState.UNKNOWN);
        }
    }

    private void displayNextPage() {
        resetLastPage();
        mDisposables.add(
                mPageInteractor.getLastPageAsync().subscribe(scannerPage -> {
                    if (mView != null) {
                        if (!scannerPage.isEmpty()) {
                            displaySnapshotLayout(SnapshotLayoutState.SINGLE);
                            final ScannerPageInfo scannerPageInfo = scannerPage.getPageInfo();
                            mLastSnapshot = scannerPageInfo;
                            mView.displayLastSnapshot(mUriWrapper.getStringUriForFilePath(scannerPageInfo.getImageCroppedPath()));
                            mView.displayNextPage(scannerPage.getPosition() + 1);
                        } else {
                            displaySnapshotLayout(SnapshotLayoutState.MULTI);
                        }
                    }
                })
        );
    }

    private void displaySnapshotLayout(@NonNull SnapshotLayoutState state) {
        if (mView != null && mSnapshotLayoutState != state) {
            switch (state) {
                case UNKNOWN:
                    mView.removeSnapshotLayout();
                    break;
                case SINGLE:
                    mView.inflateSingleSnapshotLayout();
                    break;
                case MULTI:
                    mView.inflateMultiSnapshotLayout();
                    break;
            }
            mSnapshotLayoutState = state;
        }
    }

    private void clearDisplayedRectangle() {
        if (mView != null) {
            mView.displayRectangle(Collections.emptyList());
        }
    }

    @NonNull
    private FlashState getNextFlashState(@NonNull FlashState currentFlashState) {
        for (int i = 0; i < mFlashSwitchSequence.length; i++) {
            if (mFlashSwitchSequence[i] == currentFlashState) {
                if (i < mFlashSwitchSequence.length - 1) {
                    return mFlashSwitchSequence[i + 1];
                } else {
                    return mFlashSwitchSequence[0];
                }
            }
        }
        return currentFlashState;
    }

    @Override
    public void onPermissionDialogSettingsClick() {
        if (mView != null) {
            mView.openAppSettings();
        }
    }

    @Override
    public void onPermissionDialogCancel() {
        if (mView != null) {
            mView.finish();
        }
    }

    @Override
    public void onAppSettingsClosed() {
        if (mView != null) {
            mGrantedPermissions = mView.getGrantedPermissions();
            if (!hasCameraPermissions()) {
                mView.finish();
            }
        }
    }

    private boolean hasCameraPermissions() {
        return mGrantedPermissions != null && mGrantedPermissions.contains(Manifest.permission.CAMERA);
    }

    private enum SnapshotLayoutState {
        UNKNOWN,
        SINGLE,
        MULTI
    }
}
