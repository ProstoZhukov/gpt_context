package ru.tensor.sbis.camera.service;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import ru.tensor.sbis.camera.SurfaceProvider;

/**
 * Интерфейс описывающий взаимодействие с объектом [Camera]
 * @author am.boldinov
 */
@SuppressWarnings({"JavaDoc", "deprecation", "unused", "RedundantSuppression"})
public interface CameraService {

    /** @SelfDocumented */
    boolean hasFlash();

    /** @SelfDocumented */
    void setSurfaceProvider(@Nullable SurfaceProvider surfaceProvider);

    /** @SelfDocumented */
    void clearSurfaceProvider();

    /** @SelfDocumented */
    void switchCamera() throws RuntimeException;

    /** @SelfDocumented */
    boolean getFlashState() throws RuntimeException;

    /** @SelfDocumented */
    void switchFlash() throws RuntimeException;

    /** @SelfDocumented */
    boolean switchFlash(@NonNull FlashState flashState) throws RuntimeException;

    /** @SelfDocumented */
    void startPreview(int cameraId, SurfaceTexture surface, int orientation) throws IOException, RuntimeException;

    /** @SelfDocumented */
    void startPreview(int cameraId) throws RuntimeException;

    /** @SelfDocumented */
    void startPreview() throws RuntimeException;

    /** @SelfDocumented */
    void stopPreview();

    /** @SelfDocumented */
    void updatePreviewTexture() throws IOException;

    /** @SelfDocumented */
    int getPreviewWidth();

    /** @SelfDocumented */
    int getPreviewHeight();

    /** @SelfDocumented */
    @Nullable
    Camera.Size getPreviewSize();

    /** @SelfDocumented */
    int getCameraId();

    /** @SelfDocumented */
    class Helper {
        /** @SelfDocumented */
        public static int getNumberOfCameras() {
            return Camera.getNumberOfCameras();
        }
    }
}
