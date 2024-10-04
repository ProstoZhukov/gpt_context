package ru.tensor.sbis.design.radio_group.item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import ru.tensor.sbis.design.radio_group.control.RadioGroupStyleHolder
import ru.tensor.sbis.design.radio_group.control.api.SbisRadioGroupTitlePosition
import ru.tensor.sbis.design.radio_group.control.api.SbisRadioGroupViewApi
import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupContent
import kotlin.math.max

/**
 * View радиокнопки.
 *
 * @author ps.smirnyh
 */
@SuppressLint("ViewConstructor")
internal class SbisRadioGroupItemView internal constructor(
    context: Context,
    val itemId: String,
    val content: SbisRadioGroupContent,
    internal val styleHolder: RadioGroupStyleHolder,
    private val circleDrawer: RadioGroupItemCircleDrawer = RadioGroupItemCircleDrawer(styleHolder)
) : ViewGroup(context) {

    /** Родительская view. */
    var parentItem: SbisRadioGroupItemView? = null

    /** Уровень вложенности view. */
    var hierarchyLevel: Int = 0

    /** @see SbisRadioGroupViewApi.titlePosition */
    var titlePosition: SbisRadioGroupTitlePosition = SbisRadioGroupTitlePosition.RIGHT

    init {
        content.prepare(this)
    }

    override fun setEnabled(enabled: Boolean) {
        circleDrawer.isEnable = enabled
        content.setEnabled(enabled)
        super.setEnabled(enabled)
    }

    override fun setSelected(selected: Boolean) {
        circleDrawer.isSelected = selected
        content.setSelected(selected)
        super.setSelected(selected)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val markerWidth = (styleHolder.defaultMarkerPadding + styleHolder.borderCircleRadius) * 2
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec) - markerWidth
        content.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST), heightMeasureSpec)
        setMeasuredDimension(
            markerWidth + content.getMeasuredWidth(),
            max(content.getMeasuredHeight(), styleHolder.minHeight)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        content.layout(titlePosition.getContentPositionX(), 0)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        circleDrawer.draw(canvas, titlePosition.getCirclePositionX())
        content.draw(canvas)
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.packageName = context.getPackageName()
        info.className = SbisRadioGroupItemView::class.java.name
        content.onInitializeAccessibilityNodeInfo(info)
    }

    private fun SbisRadioGroupTitlePosition.getContentPositionX() = when (this) {
        SbisRadioGroupTitlePosition.RIGHT -> (styleHolder.defaultMarkerPadding + styleHolder.borderCircleRadius) * 2
        SbisRadioGroupTitlePosition.LEFT -> 0
    }

    private fun SbisRadioGroupTitlePosition.getCirclePositionX() = when (this) {
        SbisRadioGroupTitlePosition.RIGHT -> 0f
        SbisRadioGroupTitlePosition.LEFT -> content.getMeasuredWidth().toFloat()
    }
}