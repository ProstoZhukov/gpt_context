package ru.tensor.sbis.fresco_view.shapeddrawer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Обычный отрисовщик изображений с наложением маски.
 *
 * @author am.boldinov
 */
public class SimpleShapedImageDrawer extends AbstractShapedImageDrawer {

    private final DrawBuffer mBuffer = new DrawBuffer();

    /** @SelfDocumented */
    public SimpleShapedImageDrawer(@NonNull ImageView imageView) {
        super(imageView);
    }

    @Override
    protected Canvas getImageCanvas() {
        return mBuffer.getCanvas();
    }

    @Override
    protected Shader getImageShader() {
        return mBuffer.getShader();
    }

    @Override
    protected void prepareImageCanvas(int width, int height, @Nullable Matrix matrix) {
        mBuffer.configure(width, height, matrix);
    }

}
