package ru.tensor.sbis.design.list_utils.decoration.drawer.divider;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import ru.tensor.sbis.design.list_utils.decoration.Decoration;
import ru.tensor.sbis.design.list_utils.decoration.drawer.SolidDecorationDrawer;

/**
 * Отрисовщик разделителя элемента списка. В качестве разделителя используется заливка указанным цветом.
 *
 * @author sa.nikitin
 */
@SuppressWarnings("unused")
public class SolidDividerDrawer
        extends SolidDecorationDrawer.After
        implements Decoration.ItemOffsetProvider {

    /**
     * Размер разделителя.
     */
    private final int mWidth;

    public SolidDividerDrawer(int color, int width) {
        this(color, DEFAULT_ADJUST_ALPHA, width);
    }

    public SolidDividerDrawer(int color, boolean adjustAlpha, int width) {
        setColor(color);
        setAdjustAlpha(adjustAlpha);
        mWidth = width;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View itemView,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        // Задаем отступ снизу для отрисовки разделителя
        //noinspection SuspiciousNameCombination
        outRect.set(0, 0, 0, mWidth);
    }

}
