package ru.tensor.sbis.design.list_utils.decoration.drawer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import ru.tensor.sbis.design.list_utils.decoration.Decoration;
import android.view.View;

/**
 * Реализация отрисовщика декорации, заливающего отступы указанным цветом.
 *
 * @author sa.nikitin
 */
@SuppressWarnings("unused")
public abstract class SolidDecorationDrawer
        extends AlphaDecorationDrawer
        implements Decoration.Drawer {

    /**
     * Цвет заливки по-умолчанию.
     */
    protected static final int DEFAULT_SOLID_COLOR = Color.TRANSPARENT;

    /**
     * Кисть для выполнения заливки.
     */
    @NonNull
    private final Paint mPaint = new Paint();

    /**
     * Цвет заливки.
     */
    private int mColor;

    public SolidDecorationDrawer() {
        this(DEFAULT_SOLID_COLOR, DEFAULT_ADJUST_ALPHA);
    }

    public SolidDecorationDrawer(int color) {
        this(color, DEFAULT_ADJUST_ALPHA);
    }

    public SolidDecorationDrawer(int color, boolean adjustAlpha) {
        super(adjustAlpha);
        setColor(color);
    }

    /**
     * Получить цвет заливки.
     *
     * @return цвет заливки
     */
    public int getColor() {
        return mColor;
    }

    /**
     * Задать цвет заливки.
     *
     * @param color - цвет заливки
     */
    public void setColor(int color) {
        mColor = color;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull View itemView,
                     int left, int top, int right, int bottom,
                     @NonNull Rect offsets) {
        preparePaint(mPaint, itemView);
        if (offsets.top > 0) {
            canvas.drawRect(left, top, right, top + offsets.top, mPaint);
        }
        if (offsets.bottom > 0) {
            canvas.drawRect(left, bottom - offsets.bottom, right, bottom, mPaint);
        }
        if (offsets.left > 0) {
            canvas.drawRect(left, top + offsets.top, left + offsets.left, bottom - offsets.bottom, mPaint);
        }
        if (offsets.right > 0) {
            canvas.drawRect(right - offsets.right, top + offsets.top, right, bottom - offsets.bottom, mPaint);
        }
    }

    /**
     * Подготавливаем кисть для заливки.
     *
     * @param paint - кисть, которую необходимо подготовить
     * @param view  - view для которого будет выполняться заливка
     */
    private void preparePaint(@NonNull Paint paint, @NonNull View view) {
        paint.setColor(mColor);
        if (getAdjustAlpha()) {
            paint.setAlpha(intAlpha(view, paint));
        }
    }

    /**
     * Реализация {@link SolidDecorationDrawer} для выполнения заливки до отрисовки элемента списка.
     */
    public static class Before extends SolidDecorationDrawer implements Decoration.BeforeDrawer {
    }

    /**
     * Реализация {@link SolidDecorationDrawer} для выполнения заливки после отрисовки элемента списка.
     */
    public static class After extends SolidDecorationDrawer implements Decoration.AfterDrawer {
    }
}
