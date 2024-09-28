package ru.tensor.sbis.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment;

/**
 * Базовый фрагмент работы с камерой
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression", "ConstantConditions"})
public abstract class BaseCameraFragment<V extends BaseCameraContract.View, T extends BaseCameraContract.Presenter<V>> extends BasePresenterFragment<V, T>
        implements TextureView.SurfaceTextureListener, SurfaceProvider, BaseCameraContract.View {

    private static final int PERMISSIONS_REQUEST_CODE = 0x0a0b;

    private final BehaviorSubject<Boolean> mSurfacePreparingSubject = BehaviorSubject.create();

    private final Set<String> mCameraPermissions = new HashSet<>();
    @Nullable
    protected TextureView mPreviewView;

    @LayoutRes
    protected abstract int getFragmentLayout();
    @NonNull
    protected abstract TextureView createPreviewView(@NonNull View root);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermissions();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(getFragmentLayout(), container, false);
        initViews(root);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPreviewView = null;
    }

    protected void initViews(@NonNull View root) {
        mPreviewView = createPreviewView(root);
        mPreviewView.setSurfaceTextureListener(this);
    }

    protected void initPermissions() {
        getRequiredPermissions().add(Manifest.permission.CAMERA);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onViewStarted();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onViewStopped();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurfacePreparingSubject.onNext(true);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @NonNull
    @Override
    public Observable<Boolean> getSurfacePreparingObservable() {
        return mSurfacePreparingSubject;
    }

    @Nullable
    @Override
    public SurfaceProvider getSurfaceProvider() {
        return this;
    }

    @Override
    public void configurePreviewTransformation(int previewWidth, int previewHeight) {
        if(mPreviewView == null) return;
        if (previewWidth > 0 && previewHeight > 0) {
            int orientation = getScreenOrientation();

            float scaleVideo;

            switch (orientation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    scaleVideo = (float) previewHeight / previewWidth;
                    break;

                default:
                    scaleVideo = (float) previewWidth / previewHeight;
            }

            int viewWidth = mPreviewView.getWidth();
            int viewHeight = mPreviewView.getHeight();

            float scaleView = (float)  viewWidth / viewHeight;

            float newScale = scaleVideo / scaleView;

            float scaleX = 1.f,
                    scaleY = 1.f;

            if (newScale < 1) {
                scaleY = 1 / newScale;
            } else {
                scaleX = newScale;
            }

            Matrix transformationMatrix = new Matrix();
            mPreviewView.getTransform(transformationMatrix);

            transformationMatrix.setScale(scaleX, scaleY);

            transformationMatrix.postTranslate(((1 - scaleX) * viewWidth) / 2, ((1 - scaleY) * viewHeight) / 2);
            mPreviewView.setTransform(transformationMatrix);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            Set<String> grantedPermissions = null;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (grantedPermissions == null) {
                        grantedPermissions = new HashSet<>();
                    }
                    grantedPermissions.add(permissions[i]);
                }
            }
            mPresenter.onPermissionsRequestResult(grantedPermissions);
        }
    }

    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    @Override
    public void requestPermissions(@NonNull Set<String> permissions) {
        if (permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISSIONS_REQUEST_CODE);
        }
    }

    @NonNull
    @Override
    public Set<String> getRequiredPermissions() {
        return mCameraPermissions;
    }

    @Override
    public Set<String> getGrantedPermissions() {
        Set<String> grantedPermissions = null;
        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                if (grantedPermissions == null) {
                    grantedPermissions = new HashSet<>();
                }
                grantedPermissions.add(permission);
            }
        }
        return grantedPermissions;
    }

    //region SurfaceProvider interface
    @Nullable
    @Override
    public SurfaceTexture getSurfaceTexture() {
        return mPreviewView.getSurfaceTexture();
    }

    @Override
    public int getScreenOrientation() {
        return getActivity().getWindowManager().getDefaultDisplay().getRotation();
    }

    @Override
    public int getSurfaceWidth() {
        return mPreviewView.getWidth();
    }

    @Override
    public int getSurfaceHeight() {
        return mPreviewView.getHeight();
    }

    //endregion
}
