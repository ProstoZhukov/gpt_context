package ru.tensor.sbis.design.buttons

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.StyleRes
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.utils.drawers.ButtonComponentDrawer
import ru.tensor.sbis.design.buttons.base.utils.style.SbisButtonStyleHolder
import ru.tensor.sbis.design.buttons.base.zentheme.ButtonZenThemeSupport
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.buttons.icon_text.AbstractSbisIconAndTextButton
import ru.tensor.sbis.design.buttons.link.zentheme.ZenThemeLinkButtonController
import ru.tensor.sbis.design.buttons.link.api.SbisLinkButtonController
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.utils.errorSafe
import kotlin.math.roundToInt

/**
 * Реализация [AbstractSbisIconAndTextButton] в виде кнопки-ссылки.
 *
 * @author ma.kolpakov
 */

open class SbisLinkButton private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    controller: SbisLinkButtonController,
    globalStyleHolder: SbisButtonStyleHolder = SbisButtonStyleHolder(),
    zenThemeController: ZenThemeLinkButtonController = ZenThemeLinkButtonController()
) : AbstractSbisIconAndTextButton<SbisButtonSize, SbisLinkButtonController>(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
),
    ButtonZenThemeSupport by zenThemeController {

    @get:Dimension
    private val alignPadding: Float
        get() {
            return if (contentWidth < measuredWidth) {
                when (controller.align) {
                    HorizontalAlignment.LEFT -> 0F
                    HorizontalAlignment.CENTER -> (measuredWidth - contentWidth) / 2F
                    HorizontalAlignment.RIGHT -> measuredWidth - contentWidth
                }
            } else {
                0F
            }
        }

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes, globalStyleHolder)
        zenThemeController.attach(this)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = SbisButtonStyle.DEFAULT.linkButtonStyle,
        @StyleRes defStyleRes: Int = SbisButtonStyle.DEFAULT.defaultLinkButtonStyle
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisLinkButtonController())

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = with(controller) {
        if (!isMeasured) isMeasured = true
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val constrainedWidth = MeasureSpec.getSize(widthMeasureSpec)

        innerSpacing = globalStyleHolder.getInnerSpacing(button, size)

        var width = sidePadding * 2F
        width += iconDrawer?.width ?: 0F

        if (iconDrawer != null && titleDrawer != null) {
            width += innerSpacing
        }

        val remain = (constrainedWidth - width).coerceAtLeast(0F)
        // размер содержимого в кнопке. Может быть меньше размера, который требуется снаружи
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

        canvas.translate(alignPadding, 0F)

        val title = titleDrawer
        val icon = iconDrawer
        when {
            icon != null && title != null ->
                drawIconWithTitle(canvas, icon, title, model.title!!.position)

            title != null ->
                drawTitle(canvas, title, model.title!!.position)

            icon != null ->
                drawIcon(canvas, icon)

            else -> Unit
        }
    }

    private fun drawIcon(
        canvas: Canvas,
        icon: ButtonComponentDrawer,
        leftPadding: Float = sidePadding
    ) {
        canvas.withTranslation(leftPadding, (measuredHeight - icon.height) / 2F) {
            icon.draw(canvas)
        }
    }

    private fun drawTitle(
        canvas: Canvas,
        title: ButtonComponentDrawer,
        titlePosition: HorizontalPosition
    ) {
        val x: Float = if (titlePosition == HorizontalPosition.RIGHT) {
            // смещаемся вправо, рисуем в обратном порядке
            contentWidth - sidePadding - title.width
        } else {
            sidePadding
        }
        canvas.withTranslation(x, (measuredHeight - title.height) / 2F) {
            title.draw(canvas)
        }
    }

    private fun drawIconWithTitle(
        canvas: Canvas,
        icon: ButtonComponentDrawer,
        title: ButtonComponentDrawer,
        position: HorizontalPosition
    ) {
        val heightCenter = measuredHeight / 2F

        if (position == HorizontalPosition.RIGHT) {
            drawIcon(canvas, icon)
            canvas.withTranslation(
                contentWidth - sidePadding - title.width,
                (measuredHeight - title.height) / 2F
            ) {
                title.draw(canvas)
            }
        } else {
            canvas.withTranslation(sidePadding, heightCenter - title.height / 2F) {
                title.draw(canvas)
            }
            canvas.translate(contentWidth - sidePadding - icon.width, 0F)
            drawIcon(canvas, icon, 0F)
        }
    }

    /** @SelfDocumented */
    override fun setZenButtonStyle(style: SbisButtonStyle) {
        super.setZenButtonStyle(style)
        invalidate()
    }
}