package ru.tensor.sbis.camera.service;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.Surface;

import java.io.IOException;
import java.util.List;

import ru.tensor.sbis.camera.BuildConfig;
import ru.tensor.sbis.camera.SurfaceProvider;
import timber.log.Timber;

/**
 * Класс взаимодействия с объектом [Camera]
 *
 * @author am.boldinov
 */
@SuppressWarnings({"deprecation", "EmptyMethod", "unused", "RedundantSuppression", "ConstantConditions"})
public abstract class AbstractCameraService implements CameraService {

    @Nullable
    protected volatile Camera mCamera;
    protected volatile int mCameraId = getDefaultCameraId();
    @Nullable
    protected SurfaceProvider mSurfaceProvider;

    protected abstract int getDefaultCameraId();

    @Override
    public synchronized boolean hasFlash() {
        if (mCamera == null) {
            throw new IllegalStateException("start preview before");
        }

        try {
            Camera.Parameters parameters = mCamera.getParameters();

            if (parameters.getFlashMode() == null) {
                return false;
            }

            List<String> supportedFlashModes = parameters.getSupportedFlashModes();
            boolean hasFlash = supportedFlashModes != null && !supportedFlashModes.isEmpty();
            if (hasFlash && supportedFlashModes.size() == 1) {
                hasFlash = !supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF);
            }
            return hasFlash;
        } catch (RuntimeException e) {
            Timber.d(e, "Error while getting the camera parameters.");
            return false;
        }
    }

    @Override
    public synchronized void setSurfaceProvider(@Nullable SurfaceProvider surfaceProvider) {
        mSurfaceProvider = surfaceProvider;
    }

    @Override
    public synchronized void clearSurfaceProvider() {
        mSurfaceProvider = null;
    }

    @Override
    public synchronized void switchCamera() throws RuntimeException {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

        if (Camera.getNumberOfCameras() < 2) {
            throw (new RuntimeException("there is no camera to switch"));
        }

        mCameraId = mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT
                ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;

        startPreview(mCameraId);
    }

    @Override
    public synchronized boolean getFlashState() throws RuntimeException {
        try {
            return getFlashModeParametersOrThrow().getFlashMode()
                    .equals(Camera.Parameters.FLASH_MODE_TORCH);
        } catch (RuntimeException e) {
            Timber.d(e, "Error while getting the camera parameters.");
            return false;
        }
    }

    @Override
    public synchronized void switchFlash() throws RuntimeException {
        final Camera.Parameters params = getFlashModeParametersOrThrow();
        if (params.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        } else {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }
        mCamera.setParameters(params);
    }

    @Override
    public synchronized boolean switchFlash(@NonNull FlashState flashState) throws RuntimeException {
        final Camera.Parameters params = getFlashModeParametersOrThrow();
        if (params.getSupportedFlashModes().contains(flashState.getCameraFlashMode())) {
            params.setFlashMode(flashState.getCameraFlashMode());
            mCamera.setParameters(params);
            return true;
        }
        return false;
    }

    @NonNull
    private Camera.Parameters getFlashModeParametersOrThrow() throws RuntimeException {
        if (mCamera == null) {
            throw (new RuntimeException("start preview before"));
        }
        Camera.Parameters params = mCamera.getParameters();
        final String currentMode = params.getFlashMode();
        if (currentMode == null) {
            mCamera.release();
            throw (new RuntimeException("This camera doesn't support flash mode settings"));
        }
        return params;
    }

    @Override
    public synchronized void startPreview(int cameraId, SurfaceTexture surface, int orientation) throws IOException, RuntimeException {
        if (mCamera != null) {
            mCamera.release();
            throw (new RuntimeException("Camera instance already exists"));
        }

        openCamera(cameraId);

        if (mCamera == null) {
            throw (new RuntimeException("Error while opening camera"));
        }

        mCameraId = cameraId;

        mCamera.setPreviewTexture(surface);
        int cameraOrientation = determineSuitableCameraOrientation(orientation);
        mCamera.setDisplayOrientation(cameraOrientation);
        onPreStartPreview();
        mCamera.startPreview();
    }

    @Override
    public synchronized void startPreview(int cameraId) throws RuntimeException {
        if (mCamera != null) {
            mCamera.release();
            throw (new RuntimeException("Camera instance already exists"));
        }

        openCamera(cameraId);

        if (mCamera == null) {
            throw (new RuntimeException("Error while opening camera"));
        }

        mCameraId = cameraId;
        onPreStartPreview();
        mCamera.startPreview();
    }

    @Override
    public synchronized void startPreview() throws RuntimeException {
        startPreview(mCameraId);
    }

    @Override
    public synchronized void stopPreview() {
        if (mCamera != null) {
            //todo: may be add setPreviewTexture(null) to prevent SurfaceTexture leak
            try {
                onPreStopPreview();
                mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    throw new RuntimeException(e);
                } else {
                    Timber.e(e);
                }
            }
            mCamera = null;
        }
    }

    @Override
    public synchronized void updatePreviewTexture() throws IOException {
        if (mCamera == null) {
            throw new IllegalStateException("Camera not created yet.");
        }

        if (mSurfaceProvider != null) {
            mCamera.setPreviewTexture(mSurfaceProvider.getSurfaceTexture());

            mCamera.setDisplayOrientation(
                    determineSuitableCameraOrientation(mSurfaceProvider.getScreenOrientation()));
        }
    }

    @Override
    public synchronized int getPreviewWidth() {
        if (mCamera != null) {
            try {
                return mCamera.getParameters().getPreviewSize().width;
            } catch (RuntimeException e) {
                Timber.d(e, "Error while getting the camera parameters.");
            }
        }

        return -1;
    }

    @Override
    public synchronized int getPreviewHeight() {
        if (mCamera != null) {
            try {
                return mCamera.getParameters().getPreviewSize().height;
            } catch (RuntimeException e) {
                Timber.d(e, "Error while getting the camera parameters.");
            }
        }

        return -1;
    }

    @Nullable
    @Override
    public synchronized Camera.Size getPreviewSize() {
        if (mCamera != null) {
            try {
                return mCamera.getParameters().getPreviewSize();
            } catch (RuntimeException e) {
                Timber.d(e, "Error while getting the camera parameters.");
            }
        }

        return null;
    }

    @Override
    public int getCameraId() {
        return mCameraId;
    }

    protected void onPreStartPreview() {

    }

    protected void onPreStopPreview() {

    }

    protected int determineSuitableCameraOrientation(int screenOrientation) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);

        int degrees = 0;

        switch (screenOrientation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    private void openCamera(int cameraId) {
        for (int i = 0; i < 30; i++) {
            try {
                mCamera = Camera.open(cameraId);
                break;
            } catch (Exception exception) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
