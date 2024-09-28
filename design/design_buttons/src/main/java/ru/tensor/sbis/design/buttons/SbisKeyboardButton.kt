package ru.tensor.sbis.design.buttons

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.buttons.base.utils.drawers.ButtonComponentDrawer
import ru.tensor.sbis.design.buttons.base.utils.style.SbisButtonStyleHolder
import ru.tensor.sbis.design.buttons.icon_text.AbstractSbisIconAndTextButton
import ru.tensor.sbis.design.buttons.keyboard.api.SbisKeyboardButtonApi
import ru.tensor.sbis.design.buttons.keyboard.api.SbisKeyboardButtonController
import ru.tensor.sbis.design.buttons.keyboard.model.SbisKeyboardButtonSize
import ru.tensor.sbis.design.utils.errorSafe
import kotlin.math.roundToInt

/**
 * Реализация [AbstractSbisIconAndTextButton] в виде кнопки виртуальной клавиатуры, на базе кнопки с заголовком и
 * иконкой.
 *
 * @author ra.geraskin
 */
class SbisKeyboardButton private constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    controller: SbisKeyboardButtonController,
    globalStyleHolder: SbisButtonStyleHolder = SbisButtonStyleHolder()
) : AbstractSbisIconAndTextButton<SbisKeyboardButtonSize, SbisKeyboardButtonController>(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
),
    SbisKeyboardButtonApi by controller {

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes, globalStyleHolder)
    }

    @JvmOverloads
    @Suppress("unused")
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
        @StyleRes defStyleRes: Int = 0
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisKeyboardButtonController())

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = with(controller) {
        if (!isMeasured) isMeasured = true
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val constrainedWidth = MeasureSpec.getSize(widthMeasureSpec)

        var width = sidePadding * 2F
        width += iconDrawer?.width ?: 0F

        val remain = (constrainedWidth - width).coerceAtLeast(0F)
        // Размер содержимого в кнопке. Может быть меньше размера, который требуется снаружи.
        contentWidth = width
        width += titleDrawer?.let { title ->
            when (mode) {
                MeasureSpec.UNSPECIFIED -> {
                    title.maxWidth = Float.MAX_VALUE
                    title.width
                }

                MeasureSpec.AT_MOST -> {
                    title.maxWidth = remain
                    title.width
                }

                MeasureSpec.EXACTLY -> {
                    title.maxWidth = remain
                    remain
                }

                else -> errorSafe("Unexpected mode $mode") ?: 0F
            }.also {
                contentWidth += title.width
            }
        } ?: if (mode == MeasureSpec.EXACTLY) remain else 0F

        setMeasuredDimension(width.roundToInt().coerceAtLeast(suggestedMinimumWidth), minimumHeight)
    }

    override fun onDraw(canvas: Canvas) = with(controller) {
        super.onDraw(canvas)

        canvas.translate(paddingStart.toFloat(), paddingTop.toFloat())
        activeBackgroundDrawer.draw(canvas)

        val title = titleDrawer
        val icon = iconDrawer
        when {
            title != null ->
                drawTitle(canvas, title)

            icon != null ->
                drawIcon(canvas, icon)
        }
    }

    private fun drawIcon(
        canvas: Canvas,
        icon: ButtonComponentDrawer
    ) {
        canvas.withTranslation(
            (controller.activeBackgroundDrawer.width - icon.width) / 2F,
            (controller.activeBackgroundDrawer.height - icon.height) / 2F
        ) {
            icon.draw(canvas)
        }
    }

    private fun drawTitle(
        canvas: Canvas,
        title: ButtonComponentDrawer
    ) {
        canvas.withTranslation(
            (controller.activeBackgroundDrawer.width - title.width) / 2F,
            (controller.activeBackgroundDrawer.height - title.height) / 2F
        ) {
            title.draw(canvas)
        }
    }

    override fun getBaseline(): Int = with(controller) {
        val iconHeight = iconDrawer?.height ?: 0F
        ((measuredHeight - iconHeight) / 2F + iconHeight).roundToInt()
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.text = controller.accessibilityText
    }

}