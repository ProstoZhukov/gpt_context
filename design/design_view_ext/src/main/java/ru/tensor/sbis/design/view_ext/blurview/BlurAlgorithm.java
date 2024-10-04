package ru.tensor.sbis.design.view_ext.blurview;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

/**
 * Интерфейс для реализации блюра
 */
@SuppressWarnings("SameReturnValue")
public interface BlurAlgorithm {
    /**
     * @param bitmap     bitmap to be blurred
     * @param blurRadius blur radius
     * @return blurred bitmap
     */
    Bitmap blur(Bitmap bitmap, float blurRadius);

    /**
     * Frees allocated resources
     */
    void destroy();

    /**
     * @return true if sent bitmap can be modified, false otherwise
     */
    @SuppressWarnings("unused")
    boolean canModifyBitmap();

    /**
     * Retrieve the {@link Bitmap.Config} on which the {@link BlurAlgorithm}
     * can actually work.
     *
     * @return bitmap config supported by the given blur algorithm.
     */
    @NonNull
    Bitmap.Config getSupportedBitmapConfig();
}
