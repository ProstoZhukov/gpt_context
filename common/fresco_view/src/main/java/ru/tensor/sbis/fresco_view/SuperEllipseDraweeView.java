package ru.tensor.sbis.fresco_view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

/**
 * Реализация {@link ShapedDraweeView} с возможностью отобразить изображение
 * со скруглениями типа 'SuperEllipse'.
 *
 * @author am.boldinov
 */
public class SuperEllipseDraweeView extends ShapedDraweeView {

    /** @SelfDocumented */
    public SuperEllipseDraweeView(Context context) {
        this(context, null);
    }

    /** @SelfDocumented */
    public SuperEllipseDraweeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /** @SelfDocumented */
    public SuperEllipseDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        overrideMask();
    }

    private void overrideMask() {
        setShape(ContextCompat.getDrawable(getContext(), R.drawable.fresco_view_super_ellipse_vector_mask));
    }
}