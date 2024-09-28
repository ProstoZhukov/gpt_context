package ru.tensor.sbis.fresco_view.shapeddrawer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Отрисовщик изображений с наложением маски, использующий
 * стратегию чередования буфферов для отрисовки.
 *
 * @author am.boldinov
 */
public class SwapBufferShapedImageDrawer extends AbstractShapedImageDrawer {

    private final DrawBuffer mBuffer0 = new DrawBuffer();
    private final DrawBuffer mBuffer1 = new DrawBuffer();

    private DrawBuffer mCurrentBuffer;

    /** @SelfDocumented */
    public SwapBufferShapedImageDrawer(@NonNull ImageView imageView) {
        super(imageView);
    }

    /** @SelfDocumented */
    public SwapBufferShapedImageDrawer(@NonNull ShapedImageView imageView) {
        super(imageView);
    }

    @Override
    protected void prepareImageCanvas(int width, int height, @Nullable Matrix matrix) {
        mBuffer0.configure(width, height, matrix);
        mBuffer1.configure(width, height, matrix);
    }

    @Override
    protected Canvas getImageCanvas() {
        if (mCurrentBuffer != null) {
            return mCurrentBuffer.getCanvas();
        }
        return null;
    }

    @Override
    protected Shader getImageShader() {
        if (mCurrentBuffer != null) {
            return mCurrentBuffer.getShader();
        }
        return null;
    }

    private void swapBuffer() {
        mCurrentBuffer = mCurrentBuffer == mBuffer0 ? mBuffer1 : mBuffer0;
    }

    @Override
    protected void paintNewImage(@NonNull Drawable drawable) {
        swapBuffer(); // Подменяем буффер перед отрисовкой нового изображения
        super.paintNewImage(drawable);
    }

}
