package ru.tensor.sbis.fresco_view.shapeddrawer;

import static android.os.Build.VERSION.SDK_INT;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import timber.log.Timber;

/**
 * Абстрактный класс для отрисовки изображений с обрезкой по маске.
 *
 * @author am.boldinov
 */
public abstract class AbstractShapedImageDrawer {

    /**
     * Экземпляр {@link ShapedImageView}, для которого будем выполнять отрисовку.
     */
    protected final ShapedImageView imageView;

    /**
     * Кисть для наложения шейдера с изображением.
     */
    private final Paint mShaderPaint = new Paint();

    /**
     * Канва для отрисовки маски.
     */
    private final Canvas mMaskCanvas = new Canvas();

    /**
     * Bitmap для хранения отрисованной маски.
     */
    private Bitmap mMaskBitmap;

    /**
     * Маска, по которой необходимо обрезать изображение.
     */
    @Nullable
    private Drawable mShape;

    /**
     * Флаг инвалидации закешированного изображения.
     */
    private boolean mInvalidated = true;

    @ColorInt
    private int mBackgroundColor;

    @Nullable
    private Function1<Canvas, Unit> mDrawForeground;

    protected AbstractShapedImageDrawer(@NonNull ImageView imageView) {
        this(new DefaultShapedImageView(imageView));
    }

    protected AbstractShapedImageDrawer(@NonNull ShapedImageView imageView) {
        this.imageView = imageView;
        mShaderPaint.setAntiAlias(true);
    }

    /**
     * Задать маску, по которой нужно обрезать изображение.
     */
    public void setShape(@Nullable Drawable shape) {
        if (mShape != shape) {
            mShape = shape;
            if (mMaskBitmap != null) {
                prepareMaskCanvas(imageView.getWidth(), imageView.getHeight(), true, true);
            }
            invalidate();
        }
    }

    /**
     * Задаёт цвет фона. Актуально для изображений с прозрачностью
     */
    public void setBackgroundColor(@ColorInt int backgroundColor) {
        if (mBackgroundColor != backgroundColor) {
            mBackgroundColor = backgroundColor;
            invalidate();
        }
    }

    /**
     * Задаёт код для рисования поверх изображения, с учётом обрезки по заданной форме.
     */
    public void setOnDrawForeground(@NonNull Function1<Canvas, Unit> drawForeground) {
        mDrawForeground = drawForeground;
    }

    // region ImageView binding methods

    /**
     * Метод инвалидации отрисовщика.
     * Должен быть вызван из метода {@link ImageView#invalidate()}.
     */
    @CallSuper
    public void invalidate() {
        mInvalidated = true;
    }

    /**
     * Метод обновления размеров изображения.
     * Должен быть вызван из метода {@link ImageView#onSizeChanged(int, int, int, int)}.
     */
    @CallSuper
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        boolean isSizeChanged = w != oldw || w != oldh;
        boolean isMaskSizeChanged = mMaskBitmap == null ||
                getMaskWidth(w) != mMaskBitmap.getWidth() ||
                getMaskHeight(h) != mMaskBitmap.getHeight();
        prepareMaskCanvas(w, h, isSizeChanged, isMaskSizeChanged);
    }

    /**
     * Метод отрисовки обрезанного изображения на канве ImageView.
     * Должен быть вызван из метода {@link ImageView#onDraw(Canvas)}.
     */
    @CallSuper
    public void onDraw(Canvas canvas) {
        final int left = imageView.getPaddingLeft();
        final int top = imageView.getPaddingTop();
        final int right = imageView.getWidth() - imageView.getPaddingRight();
        final int bottom = imageView.getHeight() - imageView.getPaddingBottom();
        canvas.saveLayer(left, top, right, bottom, null, Canvas.ALL_SAVE_FLAG);
        try {
            if (mInvalidated) {
                Drawable drawable = imageView.getDrawable();
                if (drawable != null) {
                    mInvalidated = false;
                    paintNewImage(drawable);
                }
            }

            final Shader shader = getImageShader();
            if (shader != mShaderPaint.getShader()) {
                // Обновляем шейдер в кисти, если нужно
                mShaderPaint.setShader(shader);
            }

            // Отрисовываем маску с исходным изображением в качестве шейдера
            canvas.drawBitmap(mMaskBitmap, left, top, mShaderPaint);
        } catch (Exception e) {
            Timber.w(e, "Exception occurred while drawing %s", imageView.getName());
        } finally {
            canvas.restore();
        }
    }

    // endregion

    /**
     * Подготовить канву с маской по новым размерам.
     *
     * @param width     - новое значение ширины
     * @param height    - новое значение высоты
     */
    protected void prepareMaskCanvas(int width,
                                     int height,
                                     boolean isSizeChanged,
                                     boolean isMaskSizeChanged) {
        width = getMaskWidth(width);
        height = getMaskHeight(height);
        boolean isValidSize = width > 0 && height > 0;
        if (isValidSize && (mMaskBitmap == null || isSizeChanged || isMaskSizeChanged)) {
            if (isMaskSizeChanged) {
                mMaskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
            }
            mMaskCanvas.setBitmap(mMaskBitmap);
            paintMaskToCanvas(mMaskCanvas, width, height);

            final Matrix matrix = calcShaderScaleMatrix(width, height);
            prepareImageCanvas(imageView.getWidth(), imageView.getHeight(), matrix);

            invalidate();
        }
    }

    private int getMaskWidth(int imageWidth) {
        return imageWidth - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getMaskHeight(int imageHeight) {
        return imageHeight - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    /**
     * Отрисовать маску на канве с указанными размерами.
     *
     * @param maskCanvas - канва для отрисовки маски
     * @param maskWidth  - ширина маски
     * @param maskHeight - высота маски
     */
    private void paintMaskToCanvas(Canvas maskCanvas, int maskWidth, int maskHeight) {
        if (mShape != null) {
            mShape.setBounds(0, 0, maskWidth, maskHeight);
            mShape.draw(maskCanvas);
        }
    }

    /**
     * Вычислить матрицу для масштабирования шейдера.
     *
     * @param pictureWidth  - ширина изображения
     * @param pictureHeight - высота изображения
     * @return матрица для масштабирования шейдера
     */
    private Matrix calcShaderScaleMatrix(int pictureWidth, int pictureHeight) {
        if (mMaskBitmap == null) {
            return null;
        }

        int maskW = mMaskBitmap.getWidth();
        int maskH = mMaskBitmap.getHeight();

        float wScale = maskW / (float) pictureWidth;
        float hScale = maskH / (float) pictureHeight;

        float scale = Math.max(wScale, hScale);

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate((maskW - pictureWidth * scale) / 2f, (maskH - pictureHeight * scale) / 2f);
        return matrix;
    }

    /**
     * Отрисовать новое исходное изображение.
     *
     * @param drawable - исходное изображение
     */
    @CallSuper
    protected void paintNewImage(@NonNull Drawable drawable) {
        final Canvas canvas = getImageCanvas();
        if (canvas != null) {
            if (mBackgroundColor == Color.TRANSPARENT) {
                canvas.drawColor(mBackgroundColor, PorterDuff.Mode.CLEAR);
            } else {
                canvas.drawColor(mBackgroundColor);
            }

            Matrix imageMatrix = imageView.getImageMatrix();
            int paddingLeft = imageView.getPaddingLeft();
            int paddingTop = imageView.getPaddingTop();
            // Draw image
            if (imageMatrix == null && paddingLeft == 0 && paddingTop == 0) {
                drawable.draw(canvas);
            } else {
                canvas.save();
                // Множество изменений механик работы ImageView в Android 10,
                // поэтому начиная с 29 sdk для паддингов необходим ручной сдвиг изображения.
                if (SDK_INT >= Build.VERSION_CODES.Q) {
                    canvas.translate(paddingLeft, paddingTop);
                }
                if (imageMatrix != null) {
                    canvas.concat(imageMatrix);
                }
                drawable.draw(canvas);
                canvas.restore();
            }

            if (mDrawForeground != null) {
                mDrawForeground.invoke(canvas);
            }
        }
    }

    /**
     * Получить канву для отрисовки исходного изображения.
     */
    protected abstract Canvas getImageCanvas();

    /**
     * Получить шейдер с исходным изображением.
     */
    protected abstract Shader getImageShader();

    /**
     * Подготовить канву к отрисовке изображений с указанными параметрами.
     *
     * @param width     - ширина изображения
     * @param height    - высота изображения
     * @param matrix    - матрица трансформации изображения
     */
    protected abstract void prepareImageCanvas(int width, int height, @Nullable Matrix matrix);

}
