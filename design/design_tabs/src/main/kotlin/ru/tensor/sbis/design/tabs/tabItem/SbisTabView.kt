package ru.tensor.sbis.design.tabs.tabItem

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import ru.tensor.sbis.design.tabs.util.SbisTabItemContentViewInflater

/**
 * Вкладка.
 *
 * @author da.zolotarev
 */
internal class SbisTabView @JvmOverloads constructor(
    context: Context,
    val styleHolder: SbisTabItemStyleHolder = SbisTabItemStyleHolder.create(context)
) : View(context) {

    /** @see ContentHolder.tabId */
    val tabId get() = contentHolder?.tabId

    /** @see ContentHolder.navxId */
    val navxId get() = contentHolder?.navxId

    private val inflater = SbisTabItemContentViewInflater(context, styleHolder, this) { isCounterWidthChanged ->
        if (isCounterWidthChanged) {
            requestLayout()
        }
        invalidate()
    }
    private var contentHolder: ContentHolder? = null

    var data: SbisTabsViewItem? = null
        set(value) {
            if (field == value || value == null) return
            field = value
            contentHolder = ContentHolder(value, inflater, styleHolder)
            invalidate()
        }

    init {
        contentDescription = "design_tab"
    }

    /**
     * @see [ContentHolder.updateStyleHolder].
     */
    fun updateStyleHolder() {
        contentHolder?.updateStyleHolder()
        requestLayout()
    }

    /**
     * Получить расстояние от baseline текста до конца нижней границы view, для правильного расчёта расположения табов.
     */
    fun getBaselineOffset(): Int {
        return measuredHeight - (contentHolder?.getContentItemMaxBaseline() ?: 0)
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.text = contentHolder?.getContentDescription()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        contentHolder?.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        contentHolder?.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Ищем самый большой элемент
        val height = contentHolder?.getContentItemMaxHeight() ?: 0

        val widthOffset = contentHolder?.measureContentAndReturnWidth() ?: 0

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(widthOffset + styleHolder.horizontalPadding, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        contentHolder?.drawContent(canvas)
    }

    override fun dispatchSetSelected(isSelected: Boolean) {
        super.dispatchSetSelected(isSelected)
        contentHolder?.setSelected(isSelected)
    }
}
