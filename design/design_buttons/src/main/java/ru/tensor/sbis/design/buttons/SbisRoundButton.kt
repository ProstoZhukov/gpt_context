package ru.tensor.sbis.design.buttons

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.buttons.base.AbstractSbisButton
import ru.tensor.sbis.design.buttons.base.api.ButtonIconApiAdapter
import ru.tensor.sbis.design.buttons.base.api.SbisButtonIconApi
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.utils.drawers.CounterDrawer
import ru.tensor.sbis.design.buttons.base.utils.style.SbisButtonStyleHolder
import ru.tensor.sbis.design.buttons.base.zentheme.ButtonZenThemeSupport
import ru.tensor.sbis.design.buttons.base.zentheme.ZenThemeAbstractButtonControllerSelector
import ru.tensor.sbis.design.buttons.round.api.SbisRoundButtonAnimation
import ru.tensor.sbis.design.buttons.round.api.SbisRoundButtonApi
import ru.tensor.sbis.design.buttons.round.api.SbisRoundButtonController
import ru.tensor.sbis.design.buttons.round.model.CounterOffsetsModel
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.buttons.round.animation.AnimationController
import ru.tensor.sbis.design.buttons.round.zentheme.ZenThemeRoundButtonControllerSelector
import ru.tensor.sbis.design.theme.HorizontalPosition.*
import ru.tensor.sbis.design.util.dpToPx
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Реализация [AbstractSbisButton] в виде круглой кнопки действия для размещения в плавающей панели
 * [SbisFloatingButtonPanel].
 *
 * @author ma.kolpakov
 */
open class SbisRoundButton private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    controller: SbisRoundButtonController,
    globalStyleHolder: SbisButtonStyleHolder = SbisButtonStyleHolder(),
    animationController: AnimationController = AnimationController(),
    zenThemeController: ZenThemeAbstractButtonControllerSelector<SbisRoundButton> =
        ZenThemeRoundButtonControllerSelector()
) : AbstractSbisButton<SbisRoundButtonSize, SbisRoundButtonController>(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
),
    SbisRoundButtonApi by controller,
    SbisButtonIconApi by ButtonIconApiAdapter(controller::icon, { controller.icon = it }),
    SbisRoundButtonAnimation by animationController,
    ButtonZenThemeSupport by zenThemeController {

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes, globalStyleHolder)
        animationController.attach(this)
        zenThemeController.attach(this)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = SbisButtonStyle.DEFAULT.roundButtonStyle,
        @StyleRes defStyleRes: Int = SbisButtonStyle.DEFAULT.defaultRoundButtonStyle
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisRoundButtonController())

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = paddingStart + minimumWidth + paddingEnd
        val h = paddingTop + minimumHeight + paddingBottom
        val constrainedWidth = MeasureSpec.getSize(widthMeasureSpec)

        // Если ширина измеренного контента больше чем пространство вью,
        // установить минимальный размер в состоянии MEASURED_STATE_TOO_SMALL.
        val contentWidth = if (constrainedWidth < w) w or MEASURED_STATE_TOO_SMALL else w

        setMeasuredDimension(contentWidth, h)
    }

    override fun onDraw(canvas: Canvas) = with(controller) {
        super.onDraw(canvas)

        canvas.translate(paddingStart.toFloat(), paddingTop.toFloat())
        activeBackgroundDrawer.draw(canvas)

        canvas.withTranslation(
            (activeBackgroundDrawer.width - progressDrawer.width) / 2F,
            (activeBackgroundDrawer.height - progressDrawer.height) / 2F
        ) {
            progressDrawer.draw(canvas)
        }

        canvas.translate(
            (activeBackgroundDrawer.width - activeIconDrawer.width) / 2F,
            (activeBackgroundDrawer.height - activeIconDrawer.height) / 2F
        )
        activeIconDrawer.draw(canvas)
        if (counterDrawer.counter != null) drawCounter(canvas, counterDrawer)
    }

    private fun drawCounter(canvas: Canvas, counterDrawer: CounterDrawer) {
        // Для размера XS счётчики не должны отображаться.
        if (size == SbisRoundButtonSize.XS) return

        val offsetModel = CounterOffsetsModel.getOffsetBySize(size)
        val (counterTranslationX, counterTranslationY) = when (counterPosition) {
            LEFT -> measureLeftCounterTranslations(counterDrawer, offsetModel)
            RIGHT -> measureRightCounterTranslations(counterDrawer, offsetModel)
        }
        canvas.withTranslation(counterTranslationX, counterTranslationY) {
            counterDrawer.draw(canvas)
        }
    }

    /**
     * Ширина activeIconDrawer больше высоты. Высота всегда равна размеру иконки. Ширина всегда пытается растянуться до
     * размера вписанного в окружность кнопки квадрата. Поэтому, за ширину иконки берём её высоту, центрируем размер и
     * вычисляем координату правого края счётчика. От этой координаты и отталкиваемся для расчёта позиции счётчика.
     */
    private fun measureLeftCounterTranslations(
        counterDrawer: CounterDrawer,
        offsetsModel: CounterOffsetsModel
    ): Pair<Float, Float> = with(controller) {
        val iconLeft = (activeIconDrawer.width - activeIconDrawer.height) / 2
        var counterTranslationX = iconLeft - counterDrawer.width / 2 + offsetsModel.horizontalCenterOffset.toPx()
        var counterTranslationY = -counterDrawer.height + offsetsModel.counterIconDepth.toPx()

        // Корректировка горизонтальной координаты счётчика, чтобы он не вылезал за внешний отступ от границы.
        val counterLeft =
            paddingStart + (activeBackgroundDrawer.width - activeIconDrawer.width) / 2F + counterTranslationX
        if (counterLeft < -offsetsModel.outerOffset.toPx()) {
            val correctionX = abs(counterLeft) - offsetsModel.outerOffset.toPx()
            counterTranslationX += correctionX
        }

        // Корректировка вертикальной координаты счётчика, чтобы он не вылезал за внешний отступ от границы.
        val counterTop =
            paddingTop + (activeBackgroundDrawer.height - activeIconDrawer.height) / 2F + counterTranslationY
        if (counterTop < -offsetsModel.outerOffset.toPx()) {
            val correctionY = abs(counterTop) - offsetsModel.outerOffset.toPx()
            counterTranslationY += correctionY
        }
        return Pair(counterTranslationX, counterTranslationY)
    }

    private fun measureRightCounterTranslations(
        counterDrawer: CounterDrawer,
        offsetsModel: CounterOffsetsModel
    ): Pair<Float, Float> = with(controller) {
        val iconRight = activeIconDrawer.height + (activeIconDrawer.width - activeIconDrawer.height) / 2
        var counterTranslationX = iconRight - counterDrawer.width / 2 - offsetsModel.horizontalCenterOffset.toPx()
        var counterTranslationY = -counterDrawer.height + offsetsModel.counterIconDepth.toPx()

        // Корректировка горизонтальной координаты счётчика, чтобы он не вылезал за внешний отступ от границы.
        val counterLeft =
            paddingStart + (activeBackgroundDrawer.width - activeIconDrawer.width) / 2F +
                counterTranslationX + counterDrawer.width
        val maxOuterX = width + offsetsModel.outerOffset.toPx()
        if (counterLeft > maxOuterX) {
            val correctionX = counterLeft - maxOuterX
            counterTranslationX -= correctionX
        }

        // Корректировка вертикальной координаты счётчика, чтобы он не вылезал за внешний отступ от границы.
        val counterTop =
            paddingTop + (activeBackgroundDrawer.height - activeIconDrawer.height) / 2F + counterTranslationY
        if (counterTop < -offsetsModel.outerOffset.toPx()) {
            val correctionY = abs(counterTop) - offsetsModel.outerOffset.toPx()
            counterTranslationY += correctionY
        }
        return Pair(counterTranslationX, counterTranslationY)
    }

    override fun getBaseline(): Int = with(controller) {
        val iconHeight = iconDrawer?.height ?: 0F
        ((measuredHeight - iconHeight) / 2F + iconHeight).roundToInt()
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || controller.progressDrawer.verify(who)
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.text = controller.accessibilityText
    }

    private fun Int.toPx(): Float = this@SbisRoundButton.context.dpToPx(this).toFloat()
}