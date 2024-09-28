package ru.tensor.sbis.design.tab_panel

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.Px
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout

/**
 * Представление для отображения вкладки
 *
 * @author ai.abramenko
 */
internal class TabPanelItemView(context: Context) : View(context) {

    private val styleHolder: TabPanelStyleHolder = TabPanelStyleHolder(context)
    private val controller: TabPanelItemController = TabPanelItemController(view = this, styleHolder = styleHolder)

    init {
        minimumWidth = styleHolder.sizePx
        minimumHeight = styleHolder.heightPx
        isClickable = true
    }

    fun applyItem(item: TabPanelItem) {
        controller.applyItem(item)
    }

    fun setTitleMaxWidth(@Px maxWidth: Int) {
        val isChanged = styleHolder.textLayout.configure { this.maxWidth = maxWidth }
        if (isChanged) safeRequestLayout()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        styleHolder.textLayout.layout(
            left = (width - styleHolder.textLayout.width) / 2,
            top = (bottom - styleHolder.textBottomMargin - styleHolder.textLayout.height).toInt()
        )
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.text = controller.accessibilityText
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        val drawable: Drawable? = styleHolder.iconBackgroundDrawable
        if (drawable != null && drawable.isStateful && drawable.setState(drawableState)) {
            invalidate()
        }
        if (controller.drawableStateChanged()) {
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpecUtils.makeAtMostSpec(minimumHeight)
        )
    }

    override fun onDraw(canvas: Canvas) {
        styleHolder.iconPaint.color = controller.iconColor
        controller.drawIconWithText(canvas)
    }

    private fun TabPanelItemController.drawIconWithText(canvas: Canvas) {
        styleHolder.iconBackgroundDrawable?.setBounds(0, 0, styleHolder.sizePx, styleHolder.sizePx)

        val height = styleHolder.size
        val topIconMargin = bottom - styleHolder.textBottomMargin -
            styleHolder.textLayout.height - styleHolder.iconBottomMargin - height

        canvas.withTranslation((width - height) / 2, topIconMargin) {
            styleHolder.iconBackgroundDrawable?.draw(canvas)
        }

        canvas.drawText(
            icon,
            (width - styleHolder.iconPaint.measureText(icon)) / 2,
            (topIconMargin + (height / 2) - ((styleHolder.iconPaint.descent() + styleHolder.iconPaint.ascent()) / 2)),
            styleHolder.iconPaint
        )

        styleHolder.textLayout.draw(canvas)
    }
}