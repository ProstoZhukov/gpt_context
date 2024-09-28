package ru.tensor.sbis.fresco_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import ru.tensor.sbis.fresco_view.shapeddrawer.AbstractShapedImageDrawer;
import ru.tensor.sbis.fresco_view.shapeddrawer.SimpleShapedImageDrawer;
import ru.tensor.sbis.fresco_view.shapeddrawer.SwapBufferShapedImageDrawer;

/**
 * Реализация {@link SimpleDraweeView} с возможностью наложения маски на изображение.
 *
 * @author am.boldinov
 */
public class ShapedDraweeView extends SimpleDraweeView {

    /**
     * Отрисовщик изображения с маской.
     */
    private final AbstractShapedImageDrawer mDrawer = isBrokenHardwareAcceleration()
            ? new SwapBufferShapedImageDrawer(this)
            : new SimpleShapedImageDrawer(this);

    /** @SelfDocumented */
    public ShapedDraweeView(Context context) {
        super(context);
        setup(context, null, 0);
    }

    /** @SelfDocumented */
    public ShapedDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0);
    }

    /** @SelfDocumented */
    public ShapedDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs, defStyle);
    }

    private void setup(Context context, AttributeSet attrs, int defStyle) {
        if (getScaleType() == ScaleType.FIT_CENTER) {
            setScaleType(ScaleType.CENTER_CROP);
        }

        if (attrs != null) {
            @SuppressLint("CustomViewStyleable")
            TypedArray typedArray =
                    context.obtainStyledAttributes(attrs, R.styleable.FrescoViewShapedDraweeView, defStyle, 0);
            int shapeId = typedArray.getResourceId(R.styleable.FrescoViewShapedDraweeView_maskShape, 0);
            if (shapeId != 0) {
                setShape(ContextCompat.getDrawable(getContext(), shapeId));
            }
            int backgroundColor = typedArray.getColor(R.styleable.FrescoViewShapedDraweeView_shapeBackgroundColor, Color.TRANSPARENT);
            setShapeBackgroundColor(backgroundColor);
            typedArray.recycle();
        }
    }

    /**
     * Есть ли на устройстве проблемы с hardware acceleration.
     * TODO придумать workaround https://online.sbis.ru/opendoc.html?guid=021b3980-1907-476e-a1a3-5d4e2c7a42d4
     */
    private boolean isBrokenHardwareAcceleration() {
        return true;
    }

    /**
     * Задать маску для изображения.
     */
    public void setShape(Drawable drawable) {
        mDrawer.setShape(drawable);
    }

    @Override
    public void invalidate() {
        if (mDrawer != null) { // Метод может быть вызван в конструкторе базового класса
            mDrawer.invalidate();
        }
        super.invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!isInEditMode()) {
            // Уведомляем отрисовщик об изменении размеров вью
            mDrawer.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Делегируем логику отрисовки
        mDrawer.onDraw(canvas);
    }

    /**
     * Задаёт цвет фона под областями изображения с прозрачностью
     */
    public void setShapeBackgroundColor(@ColorInt int backgroundColor) {
        mDrawer.setBackgroundColor(backgroundColor);
    }
}
