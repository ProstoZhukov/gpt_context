package ru.tensor.sbis.design.list_utils.decoration.drawer;

import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

/**
 * Базовая реализация отрисовщика декорации с изменением прозрачности.
 *
 * @author sa.nikitin
 */
@SuppressWarnings("JavaDoc")
public abstract class AlphaDecorationDrawer {

    /**
     * Нужно ли применять прозрачность по-умолчанию.
     */
    protected static final boolean DEFAULT_ADJUST_ALPHA = true;

    /**
     * Значение для абсолютной непрозрачности.
     */
    protected static final int ALPHA_OPAQUE = 255;

    /**
     * Нужно ли применять прозрачность.
     */
    private boolean mAdjustAlpha;

    public AlphaDecorationDrawer() {
        this(DEFAULT_ADJUST_ALPHA);
    }

    public AlphaDecorationDrawer(boolean adjustAlpha) {
        mAdjustAlpha = adjustAlpha;
    }

    /** @SelfDocumented */
    public boolean getAdjustAlpha() {
        return mAdjustAlpha;
    }

    /** @SelfDocumented */
    public void setAdjustAlpha(boolean adjustAlpha) {
        mAdjustAlpha = adjustAlpha;
    }

    /**
     * Целочисленное значение для прозрачности на основе прозрачности view.
     *
     * @param view - view, на основе которого получаем целочисленную прозрачность
     * @return целочисленное [0-255] значение прозрачности
     */
    protected static int intAlpha(@Nullable View view) {
        if (view == null) {
            return 0;
        }
        return (int) (view.getAlpha() * ALPHA_OPAQUE);
    }

    /**
     * Целочисленное значение для прозрачности на основе прозрачности view и прозрачности paint.
     *
     * @param view      - view, на основе которого получаем целочисленную прозрачность
     * @param paint     - paint, на основе которого получаем целочисленную прозрачность
     * @return целочисленное [0-255] значение прозрачности
     */
    protected static int intAlpha(@NonNull View view, @NonNull Paint paint) {
        int alpha = (int) view.getAlpha() * paint.getAlpha();
        return Math.min(alpha, ALPHA_OPAQUE);
    }

}
