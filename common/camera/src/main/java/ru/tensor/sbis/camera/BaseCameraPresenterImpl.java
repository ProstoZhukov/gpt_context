package ru.tensor.sbis.camera;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.tensor.sbis.camera.service.CameraService;

/**
 * Legacy-код
 * @author am.boldinov
 */
@SuppressWarnings({"EmptyMethod", "unused", "RedundantSuppression"})
public abstract class BaseCameraPresenterImpl<V extends BaseCameraContract.View, M extends CameraService> implements BaseCameraContract.Presenter<V> {

    @Nullable
    protected V mView;

    @Nullable
    protected Disposable mCameraPreparingSubscription;

    @Nullable
    protected Set<String> mGrantedPermissions;

    @NonNull
    protected final M mMediaService;

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public BaseCameraPresenterImpl(@NonNull M mediaService) {
        mMediaService = mediaService;
    }

    @Override
    public void attachView(@NonNull V view) {
        mView = view;
        mGrantedPermissions = mView.getGrantedPermissions();
        updateViewControlsByPermissions();
        if (mGrantedPermissions == null || mGrantedPermissions.size() < mView.getRequiredPermissions().size()) {
            mView.requestPermissions(mGrantedPermissions != null ? complementOfSet(mView.getRequiredPermissions(), mGrantedPermissions)
                    : mView.getRequiredPermissions());
        }
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onViewStarted() {
        if (mGrantedPermissions != null && mGrantedPermissions.contains(Manifest.permission.CAMERA)) {
            startPreview();
        }
        updateViewControlsByPermissions();
    }

    @Override
    public void onViewStopped() {
        if (mCameraPreparingSubscription != null) {
            if (!mCameraPreparingSubscription.isDisposed()) {
                mCameraPreparingSubscription.dispose();
            }
            mCameraPreparingSubscription = null;
        }

        mMediaService.stopPreview();
        mMediaService.clearSurfaceProvider();
    }

    @Override
    public void onPermissionsRequestResult(@Nullable Set<String> grantedPermissions) {
        if (mGrantedPermissions != null && grantedPermissions != null) {
            mGrantedPermissions.addAll(grantedPermissions);
        } else if (mGrantedPermissions == null && grantedPermissions != null) {
            mGrantedPermissions = grantedPermissions;
        }

        if (grantedPermissions != null && grantedPermissions.contains(Manifest.permission.CAMERA)) {
            startPreview();
        }

        updateViewControlsByPermissions();
    }

    protected void updateViewControlsByPermissions() {

    }

    protected void startPreview() {
        if (mView == null) {
            return;
        }
        mMediaService.setSurfaceProvider(mView.getSurfaceProvider());

        Observable<Object> cameraPrepareObservable = Observable.fromCallable(() -> {
            mMediaService.startPreview();
            return new Object();
        }).subscribeOn(Schedulers.io());

        Observable<Boolean> combinedObservable = Observable.combineLatest(cameraPrepareObservable,
                mView.getSurfacePreparingObservable(), (b1, b2) -> true)
                .onErrorReturnItem(Boolean.FALSE);

        mCameraPreparingSubscription = combinedObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(
                result -> {
                    if (result) {
                        updateUiAfterCameraPreparing();
                    } else {
                        handleErrorAfterCameraPreparing();
                    }
                },
                err -> handleErrorAfterCameraPreparing());
    }

    protected void updateUiAfterCameraPreparing() throws IOException {
        if (mView == null) {
            return;
        }
        mMediaService.updatePreviewTexture();
        mView.configurePreviewTransformation(mMediaService.getPreviewWidth(), mMediaService.getPreviewHeight());
    }

    private void handleErrorAfterCameraPreparing() {
        mMediaService.stopPreview();
        if (mView != null) {
            mView.displayStartingPreviewError();
        }
    }

    @NonNull
    private static Set<String> complementOfSet(@NonNull Set<String> originalSet, @NonNull Set<String> currentSet) {
        Set<String> copy = new HashSet<>(originalSet);
        copy.removeAll(currentSet);
        return copy;
    }
}
