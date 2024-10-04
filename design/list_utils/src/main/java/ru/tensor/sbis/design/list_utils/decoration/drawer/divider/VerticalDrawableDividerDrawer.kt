package ru.tensor.sbis.design.list_utils.decoration.drawer.divider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import ru.tensor.sbis.design.list_utils.decoration.Decoration
import ru.tensor.sbis.design.list_utils.decoration.drawer.AlphaDecorationDrawer

/**
 * Отрисовщик разделителя для вертикального списка. В качестве разделителя выступает [Drawable]
 *
 * @property divider    Drawable разделителя
 * @property onBottom   Если true, то разделитель будет нарисован снизу элемента, иначе - сверху
 *
 * @author sa.nikitin
 */
sealed class VerticalDrawableDividerDrawer(
    private val divider: Drawable,
    private val onBottom: Boolean
) : AlphaDecorationDrawer(),
    Decoration.Drawer,
    Decoration.ItemOffsetProvider {

    override fun getItemOffsets(outRect: Rect, itemView: View, parent: RecyclerView, state: RecyclerView.State) {
        if (onBottom) {
            outRect.set(0, 0, 0, divider.intrinsicHeight)
        } else {
            outRect.set(0, divider.intrinsicHeight, 0, 0)
        }
    }

    override fun draw(canvas: Canvas, itemView: View, left: Int, top: Int, right: Int, bottom: Int, offsets: Rect) {
        if (adjustAlpha) {
            divider.alpha = intAlpha(itemView)
        }
        if (onBottom) {
            divider.setBounds(left, bottom - offsets.bottom, right, bottom)
        } else {
            divider.setBounds(left, top, right, top + offsets.top)
        }
        divider.draw(canvas)
    }

    /**
     * Отрисовщик декорации до отрисовки элемента списка.
     */
    class Before(divider: Drawable, onBottom: Boolean = true) :
        VerticalDrawableDividerDrawer(divider, onBottom),
        Decoration.BeforeDrawer {

        constructor(
            context: Context,
            @DrawableRes dividerResId: Int,
            onBottom: Boolean = true
        ) : this(context.getDrawable(dividerResId)!!, onBottom)
    }

    /**
     * Отрисовщик декорации после отрисовки элемента списка.
     */
    class After(divider: Drawable, onBottom: Boolean = true) :
        VerticalDrawableDividerDrawer(divider, onBottom),
        Decoration.AfterDrawer {

        constructor(
            context: Context,
            @DrawableRes dividerResId: Int,
            onBottom: Boolean = true
        ) : this(context.getDrawable(dividerResId)!!, onBottom)
    }
}