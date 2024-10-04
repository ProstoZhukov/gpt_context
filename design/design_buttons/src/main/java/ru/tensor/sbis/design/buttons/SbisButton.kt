package ru.tensor.sbis.design.buttons

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.StyleRes
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.buttons.base.AbstractSbisButton
import ru.tensor.sbis.design.theme.HorizontalAlignment.*
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.UnaccentedButtonStyle
import ru.tensor.sbis.design.buttons.button.api.SbisButtonApi
import ru.tensor.sbis.design.buttons.button.api.SbisButtonController
import ru.tensor.sbis.design.buttons.base.utils.drawers.ButtonComponentDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.ButtonTextComponentDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.CounterDrawer
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground
import ru.tensor.sbis.design.buttons.button.models.SbisButtonPlacement
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.buttons.base.utils.style.SbisButtonStyleHolder
import ru.tensor.sbis.design.buttons.base.zentheme.ButtonZenThemeSupport
import ru.tensor.sbis.design.buttons.base.zentheme.ZenThemeAbstractButtonControllerSelector
import ru.tensor.sbis.design.buttons.button.zentheme.ZenThemeButtonControllerSelector
import ru.tensor.sbis.design.buttons.icon_text.AbstractSbisIconAndTextButton
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.errorSafe
import kotlin.math.roundToInt

/**
 * Реализация [AbstractSbisButton], которую можно размещать самостоятельно, в группах [SbisButtonGroup], плавающей
 * панели [SbisFloatingButtonPanel].
 *
 * @author ma.kolpakov
 */
open class SbisButton private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    controller: SbisButtonController,
    zenThemeController: ZenThemeAbstractButtonControllerSelector<SbisButton> = ZenThemeButtonControllerSelector(),
    globalStyleHolder: SbisButtonStyleHolder = SbisButtonStyleHolder()
) : AbstractSbisIconAndTextButton<SbisButtonSize, SbisButtonController>(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
),
    SbisButtonApi by controller,
    ButtonZenThemeSupport by zenThemeController {

    /**
     * Отступ, на основе которого регулируется выравнивание контента внутри кнопки.
     *
     * @see contentWidth
     */
    @get:Dimension
    private val alignPadding: Float
        get() {
            return if (contentWidth < measuredWidth) {
                when (controller.align) {
                    LEFT -> 0F
                    CENTER -> (measuredWidth - contentWidth) / 2F
                    RIGHT -> measuredWidth - contentWidth
                }
            } else {
                0F
            }
        }

    /**
     * Стандартный отступ внутри контентной области без учёта отступа для выравнивания [alignPadding].
     */
    @Dimension
    private val counterX = resources.getDimension(R.dimen.design_buttons_button_counter_x_position)

    @Dimension
    private val largeCounterX =
        resources.getDimension(R.dimen.design_buttons_button_large_counter_x_position)

    @Dimension
    private val largeCounterWidth =
        resources.getDimension(R.dimen.design_buttons_button_large_counter_width)

    private var measuredTitle: ButtonTextComponentDrawer? = null
    private var measuredIcon: ButtonComponentDrawer? = null

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes, globalStyleHolder)
        zenThemeController.attach(this)
        if (isInEditMode) this.context.theme.applyStyle(R.style.SbisButtonPreviewTheme, true)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = SbisButtonStyle.DEFAULT.buttonStyle,
        @StyleRes defStyleRes: Int = SbisButtonStyle.DEFAULT.defaultButtonStyle
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisButtonController())

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = with(controller) {
        // Стиль кнопки не отрисован, если размер кнопки должен был быть вычислен.
        if (!isMeasured) {
            isMeasured = true
            controller.updateStyle()
        }
        measuredTitle = titleDrawer
        measuredIcon = iconDrawer

        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val constrainedWidth = MeasureSpec.getSize(widthMeasureSpec)

        innerSpacing = globalStyleHolder.getInnerSpacing(button, size)
        val counterDrawer = getCounterDrawerUnlessEmpty()
        sidePadding = when {
            controller.hasHorizontalPadding -> globalStyleHolder.getSidePadding(button, placement)
            counterDrawer != null -> -counterDrawer.getTranslationX()
            else -> 0F
        }

        var width = sidePadding * 2F
        width += iconDrawer?.width ?: 0F

        if (iconDrawer != null && titleDrawer != null) {
            width += innerSpacing
        }

        getCounterDrawerUnlessEmpty()?.let {
            if (it.width != 0F && isCounterInlined) {
                width += if (iconDrawer == null && titleDrawer == null) {
                    it.width
                } else {
                    it.width + innerSpacing
                }
            }
        }

        // Нужно, чтобы рассчитать размер кнопки без обрезания текста.
        val titleWidth = titleDrawer?.let { title ->
            title.maxWidth = Float.MAX_VALUE
            title.width
        } ?: 0F
        val widthWithUncutTitle = width + titleWidth

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

        val contentWidth = width.roundToInt().coerceAtLeast(suggestedMinimumWidth).let {
            // Если ширина измеренного контента больше чем пространство вью,
            // установить минимальный размер в состоянии MEASURED_STATE_TOO_SMALL
            if (constrainedWidth < widthWithUncutTitle) {
                it or MEASURED_STATE_TOO_SMALL
            } else {
                it
            }
        }

        // Проверка если высота иконки больше высоты кнопки
        // возникает если включить применение системного размера шрифта
        val height =
            if (iconDrawer != null &&
                (minimumHeight - Offset.X3S.getDimenPx(context) * 2 < iconDrawer!!.height.toInt())
            ) {
                iconDrawer!!.height.toInt() + Offset.X3S.getDimenPx(context) * 2
            } else {
                minimumHeight
            }

        // Поддержка скругления при изменении высоты работает только если она задана по умолчанию
        if (cornerRadiusValue.isNaN()) {
            controller.updateCornerRadius(height.toFloat() / 2)
        }

        setMeasuredDimension(contentWidth, height)
    }

    override fun onDraw(canvas: Canvas) = with(controller) {
        super.onDraw(canvas)

        canvas.translate(alignPadding, 0F)

        // если кнопка требует вычислений размеров, а родительское View вызывает onDraw вне очереди
        val title = if (!isMeasured) measuredTitle else titleDrawer
        val icon = if (!isMeasured) measuredIcon else iconDrawer
        val titlePosition = model.title?.position ?: HorizontalPosition.RIGHT
        when {
            icon != null && title != null ->
                drawIconWithTitle(canvas, icon, title, titlePosition)

            icon != null ->
                drawIcon(canvas, icon)

            title != null ->
                drawTitle(canvas, title, titlePosition)

            else ->
                // для полноты картины поддерживается отображение только счётчиков
                canvas.withTranslation(sidePadding, (measuredHeight - (counterDrawer?.height ?: 0F)) / 2F) {
                    counterDrawer?.draw(canvas)
                }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        controller.counterDrawer?.isEnabled = enabled
        super.setEnabled(enabled)
    }

    /**
     * Установить размер [nestedSize] и тип [isMainButton]кнопки от внешнего контейнера и вложенный режим работы.
     */
    internal fun setNested(
        nestedSize: SbisButtonSize,
        isMainButton: Boolean,
        @ColorInt secondaryButtonProgressColor: Int
    ) {
        controller.apply {
            size = nestedSize
            // акцентная кнопка сохраняет настройки стиля. В целом рисуется как вне контейнера
            if (!isMainButton) {
                placement = SbisButtonPlacement.NESTED
                style = UnaccentedButtonStyle
                backgroundType = SbisButtonBackground.InGroup
                styleHolder.progressColor = secondaryButtonProgressColor
                styleHolder.progressContrastColor = secondaryButtonProgressColor
                updateStyle()
            }
        }
    }

    private fun drawIcon(
        canvas: Canvas,
        icon: ButtonComponentDrawer,
        leftPadding: Float = sidePadding,
        titlePosition: HorizontalPosition = HorizontalPosition.RIGHT
    ) {
        canvas.withTranslation(leftPadding, (measuredHeight - icon.height) / 2F) {
            icon.draw(canvas)
            if (controller.isCounterInlined) {
                drawInlinedCounter(canvas, icon, titlePosition)
            } else {
                drawStickyCounter(canvas)
            }
        }
    }

    private fun drawTitle(
        canvas: Canvas,
        title: ButtonComponentDrawer,
        titlePosition: HorizontalPosition
    ) {
        val x: Float
        val counterPosition: HorizontalPosition
        if (titlePosition == HorizontalPosition.RIGHT) {
            // смещаемся вправо, рисуем в обратном порядке
            x = contentWidth - sidePadding - title.width
            counterPosition = HorizontalPosition.LEFT
        } else {
            x = sidePadding
            counterPosition = HorizontalPosition.RIGHT
        }
        canvas.withTranslation(x, (measuredHeight - title.height) / 2F) {
            title.draw(canvas)
            drawInlinedCounter(canvas, title, counterPosition)
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
            drawIcon(canvas, icon, titlePosition = position)
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
            // после метода отрисовки ничего не вызывается. Можно сместить без восстановления
            canvas.translate(contentWidth - sidePadding - icon.width, 0F)
            drawIcon(canvas, icon, 0F, position)
        }
    }

    /**
     * Нарисовать счётчик рядом с мконкой (место должно быть зарезервировано).
     */
    private fun drawInlinedCounter(
        canvas: Canvas,
        anchor: ButtonComponentDrawer,
        titlePosition: HorizontalPosition
    ) {
        getCounterDrawerUnlessEmpty()?.let {
            val x = if (titlePosition == HorizontalPosition.RIGHT) {
                anchor.width + innerSpacing
            } else {
                -innerSpacing - it.width
            }
            canvas.withTranslation(x, (anchor.height - it.height) / 2F) {
                it.draw(canvas)
            }
        }
    }

    /**
     * Нарисовать счётчик поверх иконки.
     */
    private fun drawStickyCounter(canvas: Canvas) {
        // сейчас не поддерживается рисование на иконках разного размера поэтому смещение статично
        getCounterDrawerUnlessEmpty()?.let {
            canvas.withTranslation(it.getTranslationX()) {
                it.draw(canvas)
            }
        }
    }

    private fun getCounterDrawerUnlessEmpty() = controller.counterDrawer?.takeUnless { it.isEmpty }

    private fun CounterDrawer.getTranslationX(): Float {
        val centerX = if (this.width > largeCounterWidth) {
            largeCounterX + controller.styleHolder.borderWidth
        } else {
            counterX
        }
        return centerX - this.width / 2F
    }
}
