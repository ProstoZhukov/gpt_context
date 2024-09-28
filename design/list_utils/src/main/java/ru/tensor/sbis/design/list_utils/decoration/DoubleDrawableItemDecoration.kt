package ru.tensor.sbis.design.list_utils.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View

/**
 * Декоратор для отрисовки двух типов линий между элеменатми.
 */
class DoubleDrawableItemDecoration
/**
 * Здесь необходимо задать два [Drawable], один для расположения между элементами и второй для отображения после последнего элемента.
 * @param context                           - контекст
 * @param decorationDrawableResId           - ресурс Drawable который будет отображен между элементами.
 * @param decorationEndDrawableResId        - ресурс Drawable который будет отображен после последнего элемента списка.
 * @param listener
 */
(context: Context,
 @DrawableRes decorationDrawableResId: Int,
 @DrawableRes decorationEndDrawableResId: Int?,
 private val listener: DoubleDrawableInterface) : DividerItemDecorationWithInsets() {

    private val divider: Drawable? = ContextCompat.getDrawable(context, decorationDrawableResId)
    private val dividerEnd: Drawable? = decorationEndDrawableResId?.let { ContextCompat.getDrawable(context, it) }
    private var tempPosition = RecyclerView.NO_POSITION

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        divider(parent, view)?.also {
            outRect.set(0, 0, 0, it.intrinsicHeight)
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawForEach(c, parent,
                object : DrawDelegate{
                    override fun draw(canvas: Canvas, child: View, left: Int, top: Int, right: Int, bottom: Int, divider: Drawable) {
                        divider.setBounds(left, top, right, bottom)
                        divider.draw(canvas)
                    }
                }
        )
    }

    private fun drawForEach(c: Canvas, parent: RecyclerView, delegate: DrawDelegate) {
        val left = parent.left
        val right = parent.right
        var bottom: Int
        val childCount = parent.childCount
        for (i in startInset until childCount - endInset) {
            val child = parent.getChildAt(i)
            divider(parent, child)?.also {
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                bottom = top + it.intrinsicHeight
                delegate.draw(c, child, left, top, right, bottom, it)
            }
        }
    }

    private fun divider(parent: RecyclerView, child: View): Drawable? {
        tempPosition = parent.getChildAdapterPosition(child)
        if (tempPosition == RecyclerView.NO_POSITION || !listener.isNeedDivider(tempPosition)) {
            return null
        }
        return if (listener.isLast(tempPosition)) dividerEnd else divider
    }

}

/**
 * Интерфейс делегата, ответственного за отрисовку разделителя
 */
interface DrawDelegate {

    /**@SelfDocumented */
    fun draw(canvas: Canvas, child: View, left: Int, top: Int, right: Int, bottom: Int, divider: Drawable)
}

/**
 * Интерфейс получения необходимых данных для работы [DoubleDrawableItemDecoration].
 * Имплементируется адаптером.
 */
interface DoubleDrawableInterface {

    /**
     * Метод будет вызван [DoubleDrawableInterface] для определения того,
     * что указанная позиция является последней.
     * @param itemPosition int.
     * @return true, если по указанной позиции лежит последний элемент.
     */
    fun isLast(itemPosition: Int): Boolean

    /**
     * Метод будет вызван для определения необходимо ли добавлять дивайдер после этого элемента.
     * @param itemPosition int.
     * @return true, если после этого элемента надо добавить дивайдер.
     */
    fun isNeedDivider(itemPosition: Int): Boolean
}
