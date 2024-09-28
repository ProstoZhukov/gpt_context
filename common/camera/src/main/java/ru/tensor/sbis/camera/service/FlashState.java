package ru.tensor.sbis.camera.service;

import android.hardware.Camera;

/**
 * Состояние вспышки
 * @author am.boldinov
 */
@SuppressWarnings({"JavaDoc", "deprecation", "unused", "RedundantSuppression"})
public enum FlashState {
    AUTO(Camera.Parameters.FLASH_MODE_AUTO),
    OFF(Camera.Parameters.FLASH_MODE_OFF),
    ON(Camera.Parameters.FLASH_MODE_TORCH);

    private final String cameraFlashMode;

    FlashState(String cameraFlashMode) {
        this.cameraFlashMode = cameraFlashMode;
    }

    /** @SelfDocumented */
    public String getCameraFlashMode() {
        return cameraFlashMode;
    }
}
