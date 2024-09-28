package ru.tensor.sbis.design.topNavigation.internal_view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.animation.addListener
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.theme.zen.ZenThemeModel
import ru.tensor.sbis.design.theme.zen.ZenThemeSupport
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.topNavigation.util.FlatIndicatorViewStyleHolder
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import kotlin.math.roundToInt

/**
 * Плоский индикатор загрузки.
 *
 * @author da.zolotarev
 */
internal class FlatIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes
    defStyleAttr: Int = R.attr.sbisTopNavigationTheme,
    @StyleRes
    defStyleRes: Int = R.style.SbisTopNavigationDefaultStyle
) : View(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    ZenThemeSupport {
    private val styleHolder = FlatIndicatorViewStyleHolder()

    private val rect: RectF

    private var animationState: ColorAnimationState

    private val firstRectPaint: Paint
    private val secondRectPaint: Paint
    private val thirdRectPaint: Paint

    private var firstOnDraw = true

    private var rectColorAnimator: ValueAnimator

    init {
        styleHolder.initStyle(context)

        firstRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = styleHolder.rectColor
        }
        secondRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = styleHolder.rectColor
        }
        thirdRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = styleHolder.rectColor
        }

        rect = RectF(0f, 0f, styleHolder.rectWidth, styleHolder.rectHeight)

        animationState = ColorAnimationState(styleHolder.rectColor, styleHolder.rectActiveColor, firstRectPaint to null)
        rectColorAnimator = ValueAnimator.ofArgb(animationState.startColor, animationState.endColor)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            MeasureSpecUtils.makeExactlySpec((rect.width() * 3 + styleHolder.rectPadding * 2).roundToInt()),
            MeasureSpecUtils.makeExactlySpec(rect.height().roundToInt())
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (firstOnDraw) {
            rectColorAnimator.start()
            firstOnDraw = false
        }
        canvas.drawRoundRect(
            rect,
            styleHolder.rectCornerRadius.toFloat(),
            styleHolder.rectCornerRadius.toFloat(),
            firstRectPaint
        )
        canvas.withTranslation(rect.width() + styleHolder.rectPadding, 0f) {
            canvas.drawRoundRect(
                rect,
                styleHolder.rectCornerRadius.toFloat(),
                styleHolder.rectCornerRadius.toFloat(),
                secondRectPaint
            )
        }
        canvas.withTranslation(rect.width() * 2 + styleHolder.rectPadding * 2, 0f) {
            canvas.drawRoundRect(
                rect,
                styleHolder.rectCornerRadius.toFloat(),
                styleHolder.rectCornerRadius.toFloat(),
                thirdRectPaint
            )
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        rectColorAnimator.setupAnimator()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    override fun setZenTheme(themeModel: ZenThemeModel) {
        stopAnimation()
        styleHolder.rectColor = themeModel.elementsColors.flatIndicatorColor.getColor(context)
        styleHolder.rectActiveColor = themeModel.elementsColors.flatIndicatorActiveColor.getColor(context)

        firstRectPaint.color = styleHolder.rectColor
        secondRectPaint.color = styleHolder.rectColor
        thirdRectPaint.color = styleHolder.rectColor

        animationState = ColorAnimationState(styleHolder.rectColor, styleHolder.rectActiveColor, firstRectPaint to null)
        rectColorAnimator = ValueAnimator.ofArgb(animationState.startColor, animationState.endColor).apply {
            setupAnimator()
            start()
        }
    }

    private fun stopAnimation() {
        if (rectColorAnimator.isRunning) {
            rectColorAnimator.removeAllListeners()
            rectColorAnimator.cancel()
        }
    }

    private fun ValueAnimator.setupAnimator() {
        duration = ANIMATION_SPEED
        addUpdateListener {
            animationState.paints.first?.color = it.animatedValue as Int
            animationState.paints.second?.color =
                ArgbEvaluator().evaluate(it.animatedFraction, animationState.endColor, animationState.startColor) as Int
            invalidate()
        }

        addListener(onEnd = {
            animationState.paints = when (animationState.paints.first) {
                firstRectPaint -> secondRectPaint to firstRectPaint
                secondRectPaint -> thirdRectPaint to secondRectPaint
                thirdRectPaint -> null to thirdRectPaint
                else -> firstRectPaint to null
            }
            // OnRepeat не подходит, т.к. анимация воспроизводится не корректно
            start()
        })
    }

    /** @SelfDocumented */
    internal data class ColorAnimationState(val startColor: Int, val endColor: Int, var paints: Pair<Paint?, Paint?>)

    private companion object {
        const val ANIMATION_SPEED = 300L
    }
}