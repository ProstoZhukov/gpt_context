package ru.tensor.sbis.design.message_panel.recorder_common.record_control.children

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.view.isVisible
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.custom_view_tools.utils.SimplePaint
import ru.tensor.sbis.design.custom_view_tools.utils.animation.ColorAnimationUtils
import ru.tensor.sbis.design.message_panel.recorder_common.R
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import kotlin.math.roundToInt

/**
 * Замочек для закрепления процесса записи.
 *
 * @author vv.chekurda
 */
internal class RecordLockView(context: Context) : View(context) {

    private val size =
        resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_lock_view_size)

    @ColorInt
    private val lockIconColor: Int = IconColor.CONTRAST.getValue(context)
    @ColorInt
    private val unlockIconColor: Int = StyleColor.INFO.getIconColor(context)
    @ColorInt
    private val lockBackgroundColor: Int = StyleColor.INFO.getContrastBackgroundColor(context)
    @ColorInt
    private val unlockBackgroundColor: Int = BackgroundColor.CONTRAST.getValue(context)

    private val unlockIcon = SbisMobileIcon.Icon.smi_Unlock.character.toString()
    private val lockIcon = SbisMobileIcon.Icon.smi_lock.character.toString()
    private val stopIcon = SbisMobileIcon.Icon.smi_stopAudio.character.toString()

    private val backgroundPaint = SimplePaint {
        style = Paint.Style.FILL
        color = unlockBackgroundColor
    }
    private val backgroundRadius = size / 2f
    private var backgroundCenter = 0f to 0f

    private val primaryIconLayout = TextLayout {
        paint.apply {
            typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            textSize = IconSize.XL.getDimenPx(context).toFloat()
            color = unlockIconColor
        }
        text = unlockIcon
        alignment = Layout.Alignment.ALIGN_CENTER
        includeFontPad = false
        layoutWidth = size
    }
    private val secondaryIconLayout = primaryIconLayout.copy {
        paint.color = lockIconColor
        text = lockIcon
    }.apply { alpha = 0f }

    /**
     * Состояние закрепления процесса записи.
     */
    private var isLocked: Boolean = false

    /**
     * Доля анимации активации закрепления процесса записи.
     */
    @get:FloatRange(from = 0.0, to = 1.0)
    var activateFraction: Float = 0f
        set(value) {
            val isChanged = field != value
            field = value
            if (!isChanged || translationY != 0f || isLocked) return
            backgroundPaint.color =
                ColorAnimationUtils.getAnimatedColor(unlockBackgroundColor, lockBackgroundColor, value)
            primaryIconLayout.alpha = 1f - value
            secondaryIconLayout.alpha = value
            invalidate()
        }

    init {
        isClickable = false
        elevation =
            resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_lock_view_elevation)
                .toFloat()
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(
                    Rect(0, 0, view.measuredWidth, view.measuredHeight),
                    backgroundRadius
                )
            }
        }
    }

    /**
     * Установить состояние блокировки.
     */
    fun setLockState(isLocked: Boolean, animate: Boolean = true) {
        if (this.isLocked == isLocked) return
        this.isLocked = isLocked
        isClickable = isLocked
        if (isLocked) onLockRecord(animate) else onUnlockRecord()
        invalidate()
    }

    /**
     * Скрыть замочек для закрепления процесса записи.
     */
    fun hide(durationMs: Long) {
        animate()
            .alpha(0f)
            .setDuration(durationMs)
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
                    isVisible = false
                }
            })
            .start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        )
    }

    override fun getSuggestedMinimumWidth(): Int =
        size + paddingStart + paddingEnd

    override fun getSuggestedMinimumHeight(): Int =
        size + paddingTop + paddingBottom

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val availableWidth = w - paddingStart - paddingEnd
        val availableHeight = h - paddingTop - paddingBottom
        val centerX = availableWidth / 2f
        val centerY = availableHeight / 2f
        backgroundCenter = centerX to centerY
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val iconTop = paddingTop + ((size - primaryIconLayout.height) / 2f).roundToInt()
        primaryIconLayout.layout(paddingStart, iconTop)
        secondaryIconLayout.layout(paddingStart, iconTop)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(backgroundCenter.first, backgroundCenter.second, backgroundRadius, backgroundPaint)
        primaryIconLayout.draw(canvas)
        secondaryIconLayout.draw(canvas)
    }

    private fun onLockRecord(animate: Boolean) {
        primaryIconLayout.buildLayout {
            text = lockIcon
            paint.color = lockIconColor
        }
        secondaryIconLayout.apply {
            buildLayout { text = stopIcon }
            alpha = 0f
        }
        backgroundPaint.color = lockBackgroundColor
        if (animate) {
            animateIconChange()
        } else {
            animateLockFraction(1f)
        }
    }

    private fun onUnlockRecord() {
        primaryIconLayout.buildLayout {
            text = unlockIcon
            paint.color = unlockIconColor
        }
        primaryIconLayout.alpha = 1f
        secondaryIconLayout.apply {
            buildLayout { text = lockIcon }
            alpha = 0f
        }
        backgroundPaint.color = unlockBackgroundColor
    }

    private fun animateIconChange() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = ICON_CHANGE_DURATION_MS
            addUpdateListener { animateLockFraction(it.animatedFraction) }
        }.start()
    }

    private fun animateLockFraction(fraction: Float) {
        primaryIconLayout.alpha = 1f - fraction
        secondaryIconLayout.alpha = fraction
        invalidate()
    }
}

/**
 * Продолжительность анимации изменения иконки в мс.
 */
private const val ICON_CHANGE_DURATION_MS = 100L