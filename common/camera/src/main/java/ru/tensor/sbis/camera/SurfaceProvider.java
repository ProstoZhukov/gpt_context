package ru.tensor.sbis.camera;

import android.graphics.SurfaceTexture;
import androidx.annotation.Nullable;

/**
 * Поставщик объекта типа Surface, используемого для обновления изображения из фоновых потоков
 * @author am.boldinov
 */
@SuppressWarnings({"JavaDoc", "unused", "RedundantSuppression"})
public interface SurfaceProvider {

    /** @SelfDocumented */
    @Nullable
    SurfaceTexture getSurfaceTexture();

    /** @SelfDocumented */
    int getSurfaceWidth();

    /** @SelfDocumented */
    int getSurfaceHeight();

    /** @SelfDocumented */
    int getScreenOrientation();
}
