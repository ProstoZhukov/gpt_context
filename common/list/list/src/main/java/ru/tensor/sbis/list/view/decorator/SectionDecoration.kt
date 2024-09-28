package ru.tensor.sbis.list.view.decorator

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.background.ColorProvider
import ru.tensor.sbis.list.view.utils.layout_manager.SbisGridLayoutManager
import kotlin.math.roundToInt

/**
 * Реализует оформление блоков по стандарту - отступ между секциями и цветная линия-индикатор.
 * <a href="http://axure.tensor.ru/MobileStandart8/%D0%B1%D0%BB%D0%BE%D0%BA%D0%B8.html"
 * @property sectionsHolder SectionsHolder @SelDocumented.
 * @property spaceBetweenSectionsPx Int @SelDocumented.
 * @constructor
 */
internal class SectionDecoration(
    private val gridLayoutManager: SbisGridLayoutManager,
    private val sectionsHolder: ListDataHolder,
    private val spaceBetweenSectionsPx: Int,
    private val colorProvider: ColorProvider
) : RecyclerView.ItemDecoration() {
    /**
     * Ресурс для отрисовки цветной линии-индикатора. Создаем и храним свойством, для оптимизации.
     */
    private val line: ColorDrawable = ColorDrawable()

    /**
     * Толщина цветной линии-индикатора по стандарту.
     */
    private val lineSizeByStandard = 4f

    /**
     * Нарисовать цветную линию-индикатор для каждого блока, для которой требуется.
     * @param c Canvas
     * @param parent RecyclerView
     * @param state State
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            sectionsHolder.runIfIsFirstItemInSectionAndHasLine(
                parent.getChildAdapterPosition(view)
            ) { color ->
                val top = view.top + view.translationY.roundToInt()
                val bottom = top + getLineSize(view.resources.displayMetrics).toInt()
                line.color = color
                line.setBounds(parent.left, top, parent.right, bottom)
                line.draw(c)
            }
        }
    }

    /**
     * Нарисовать продолжение последней сеции до конца списка
     * @param c Canvas
     * @param parent RecyclerView
     * @param state State
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        if (!sectionsHolder.isNeedExpandLastSection()) return

        val childCount = parent.childCount

        if (childCount > 0) {
            val lastItemView = parent.getChildAt(childCount - 1)
            val itemDrawable = GradientDrawable()
            itemDrawable.color = colorProvider.itemColorStateList
            itemDrawable.setBounds(
                parent.left,
                lastItemView.bottom,
                parent.right,
                parent.measuredHeight
            )
            itemDrawable.draw(c)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        val marginDp = sectionsHolder.getMarginDp(
            view.context,
            position,
            gridLayoutManager.hasNoItemAtLeft(position),
            gridLayoutManager.hasNoSpanSpaceAtRight(position),
            gridLayoutManager.isFirstGroupInSection(position),
            gridLayoutManager.isInLastGroup(position)
        )
        outRect.left = marginDp.left
        outRect.right = marginDp.right
        outRect.top = marginDp.top
        outRect.bottom = marginDp.bottom
    }

    private fun getLineSize(metrics: DisplayMetrics): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            lineSizeByStandard,
            metrics
        )
    }
}