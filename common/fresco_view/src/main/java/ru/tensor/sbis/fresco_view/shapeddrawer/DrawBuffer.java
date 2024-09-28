package ru.tensor.sbis.fresco_view.shapeddrawer;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Shader;

import androidx.annotation.Nullable;

/**
 * Класс-обертка для хранения canvas и связанного с ним shader.
 *
 * @author am.boldinov
 */
class DrawBuffer {

    private final Canvas mPictureCanvas = new Canvas();
    private BitmapShader mBitmapShader;

    /**
     * Сконфигурировать canvas и shader по указанным параметрам.
     * @param width     - ширина изображения
     * @param height    - высота изображения
     * @param matrix    - матрица трансформации изображения
     */
    public void configure(int width, int height, @Nullable Matrix matrix) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mPictureCanvas.setBitmap(bitmap);
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        if (matrix != null) {
            mBitmapShader.setLocalMatrix(matrix);
        }
    }

    /**
     * Получить шейдер с изображением.
     */
    public Shader getShader() {
        return mBitmapShader;
    }

    /**
     * Получить канву для отрисовки изображения на шейдере.
     */
    public Canvas getCanvas() {
        return mPictureCanvas;
    }

}
