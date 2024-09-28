package ru.tensor.sbis.design.message_panel.recorder_common.record_control.children

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.graphics.contains
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.message_panel.decl.record.RecordControlButtonPosition
import ru.tensor.sbis.design.message_panel.decl.record.RecordControlButtonPosition.FIRST_ALIGN_END
import ru.tensor.sbis.design.message_panel.decl.record.RecordControlButtonPosition.SECOND_ALIGN_END
import ru.tensor.sbis.design.message_panel.recorder_common.R
import ru.tensor.sbis.design.utils.extentions.getActivity
import ru.tensor.sbis.design.utils.getDimenPx
import kotlin.math.abs
import ru.tensor.sbis.design.R as RDesign
import kotlin.math.roundToInt

/**
 * Контейнер для управления движениями кнопки записи [RecordControlButton].
 *
 * @author vv.chekurda
 */
internal class RecordButtonControlContainer(context: Context) : FrameLayout(context) {

    private val dimens = RecordButtonControlContainerDimens.create(context)
    private var requireStealTouch = false
    private var touchDownPos = 0f to 0f
    private var lastMovePos = 0f to 0f

    /**
     * Кнопка записи.
     */
    val recordButton = RecordControlButton(context)

    /**
     * Разметка кнопки отмены записи.
     */
    val cancelLayout = TextLayout.createTextLayoutByStyle(
        context,
        R.style.RecorderCancelTextDefaultStyle
    ).apply {
        textPaint.textSize = textPaint.textSize
            .coerceAtMost(dp(MAX_CANCEL_TEXT_SIZE_DP).toFloat())
            .coerceAtLeast(dp(MIN_CANCEL_TEXT_SIZE_DP).toFloat())
        makeClickable(this@RecordButtonControlContainer)
        alpha = 0f
    }
    private val cancelClickableRect = Rect()
    private val buttonRect = Rect()
    private val recordButtonCenterXAlignEnd: Int
        get() = when (recordButtonPosition) {
            FIRST_ALIGN_END -> dimens.firstRecordButtonCenterXAlignEnd
            SECOND_ALIGN_END -> dimens.secondRecordButtonCenterXAlignEnd
        }

    /**
     * Центр кнопки записи по оси X без учета translationX.
     */
    val recordButtonCenterX: Int
        get() = measuredWidth.minus(paddingEnd)
            .minus(recordButtonCenterXAlignEnd)

    /**
     * Слушатель изменений позиции кнопки записи.
     */
    lateinit var buttonTranslationChangedListener: OnButtonPositionChangedListener

    /**
     * Слушатель инициативы отправки записи.
     */
    lateinit var sendListener: OnSendListener

    /**
     * Слушатель инициативы отмены записи.
     */
    lateinit var cancelListener: OnCancelListener

    /**
     * Состояние закрепления управления процессом записи.
     */
    var isLocked: Boolean = false
        set(value) {
            val isChanged = field != value
            field = value
            if (!isChanged) return
            recordButton.isLocked = value
            if (value) recoverButtonPosition()
        }

    /**
     * Исходная позиция кнопки записи, которой управляет пользователь.
     */
    var recordButtonPosition: RecordControlButtonPosition = FIRST_ALIGN_END
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) safeRequestLayout()
        }

    init {
        addView(recordButton)
        isClickable = true
    }

    /**
     * Украсть пользовательское касание и поместить его на кнопку.
     */
    fun stealTouch() {
        recordButton.getGlobalVisibleRect(buttonRect)
        if (buttonRect.width() == 0 || isLayoutRequested) {
            requireStealTouch = true
        } else {
            dispatchButtonTouch()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        recordButton.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        )
    }

    override fun getSuggestedMinimumWidth(): Int =
        paddingStart.plus(paddingEnd)
            .plus(recordButtonCenterXAlignEnd)
            .plus((recordButton.measuredWidth / 2f).roundToInt())
            .plus(dimens.recordButtonAvailableDx.toInt())

    override fun getSuggestedMinimumHeight(): Int =
        paddingTop + paddingBottom + recordButton.measuredHeight

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        recordButton.layout(
            measuredWidth - paddingEnd - recordButtonCenterXAlignEnd - (recordButton.measuredWidth / 2f).roundToInt(),
            measuredHeight - paddingBottom - dimens.recordButtonCenterYAlignBottom - (recordButton.measuredHeight / 2f).roundToInt()
        )
        updateCancelClickableRect()
        if (requireStealTouch) {
            recordButton.getGlobalVisibleRect(buttonRect)
            dispatchButtonTouch()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        when {
            !isEnabled -> false
            isLocked -> cancelLayout.onTouch(this, event)
            else -> handleTouchEvent(event)
        }

    private fun handleTouchEvent(event: MotionEvent): Boolean =
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                moveButton(event.x, event.y)
                true
            }
            MotionEvent.ACTION_DOWN -> {
                recordButton.getHitRect(buttonRect)
                val isRecordTouch = buttonRect.contains(Point(event.x.roundToInt(), event.y.roundToInt()))
                if (isRecordTouch) {
                    requestDisallowInterceptTouchEvent(true)
                    touchDownPos = event.x to event.y
                    lastMovePos = touchDownPos
                }
                isRecordTouch
            }
            MotionEvent.ACTION_UP -> {
                requestDisallowInterceptTouchEvent(false)
                handleTouchUp()
                recoverButtonPosition()
                true
            }
            else -> false
        }

    private fun handleTouchUp() {
        if (isLocked) return
        if (recordButton.isCancelModeActivated) {
            cancelListener()
        } else {
            sendListener()
        }
    }

    private fun moveButton(x: Float, y: Float) {
        val dx = x - touchDownPos.first
        val dy = y - touchDownPos.second
        val lastDx = x - lastMovePos.first
        val lastDy = y - lastMovePos.second
        when {
            // Если было начато движение по вертикали -> продолжаем двигать вертикально.
            recordButton.translationY != 0f -> translateButton(dy = dy)
            // Если было начато движение по горизонтали и текущее смещение больше допустимого запаса
            // на начало вертикального движения -> продолжаем двигать горизонтально.
            recordButton.translationX < -dimens.ignoreHorizontalMovementDistance -> translateButton(dx = dx)
            // Вектор текущего смещения пальца направлен вверх по вертикали по соотношению 60% к 30% от прямого угла.
            lastDy < 0 && abs(lastDy * 2) >= abs(lastDx) -> translateButton(dy = dy)
            // Иначе двигаем по горизонтали.
            else -> translateButton(dx = dx)
        }
        lastMovePos = x to y
    }

    private fun translateButton(
        dx: Float = recordButton.translationX,
        dy: Float = recordButton.translationY,
        withNotify: Boolean = true
    ) {
        with(recordButton) {
            val oldTranslationX = translationX
            val oldTranslationY = translationY
            translationX = dx.coerceAtLeast(-dimens.recordButtonAvailableDx).coerceAtMost(0f)
            translationY = dy.coerceAtLeast(-dimens.recordButtonAvailableDy).coerceAtMost(0f)
            if (withNotify && (translationX != oldTranslationX || translationY != oldTranslationY)) {
                buttonTranslationChangedListener.invoke(translationX, translationY)
            }
        }
    }

    private fun recoverButtonPosition() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = RECOVER_BUTTON_POSITION_DURATION_MS
            val startTranslationX = recordButton.translationX
            val startTranslationY = recordButton.translationY
            addUpdateListener {
                translateButton(
                    startTranslationX * (1f - it.animatedFraction),
                    startTranslationY * (1f - it.animatedFraction),
                    withNotify = false
                )
            }
        }.start()
    }

    private fun updateCancelClickableRect() {
        val cancelHalfWidth = cancelLayout.width / 2
        val start = (measuredWidth - cancelLayout.width) / 2 - cancelHalfWidth
        val bottom = measuredHeight - dimens.recordFieldVerticalSpacing
        cancelClickableRect.set(
            start,
            bottom - dimens.recordFieldHeight,
            start + cancelLayout.width + cancelHalfWidth,
            bottom
        )
        cancelLayout.setStaticTouchRect(cancelClickableRect)
    }

    private fun dispatchButtonTouch() {
        requireStealTouch = false
        getActivity(context).dispatchTouchEvent(
            MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_DOWN,
                buttonRect.centerX().toFloat(),
                buttonRect.centerY().toFloat(),
                0
            )
        )
    }

    /**
     * Размеры внутренней разметки [RecordButtonControlContainer].
     */
    private data class RecordButtonControlContainerDimens(
        val firstRecordButtonCenterXAlignEnd: Int,
        val secondRecordButtonCenterXAlignEnd: Int,
        val recordButtonCenterYAlignBottom: Int,
        val recordButtonAvailableDx: Float,
        val recordButtonAvailableDy: Float,
        val recordFieldHeight: Int,
        val recordFieldVerticalSpacing: Int,
        val ignoreHorizontalMovementDistance: Float
    ) {
        companion object {
            fun create(context: Context) = with(context) {
                val recordButtonAvailableDx = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_button_horizontal_movement_distance).toFloat()
                RecordButtonControlContainerDimens(
                    firstRecordButtonCenterXAlignEnd = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_button_first_center_x_align_end),
                    secondRecordButtonCenterXAlignEnd = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_button_second_center_x_align_end),
                    recordButtonCenterYAlignBottom = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_button_center_y_align_bottom),
                    recordButtonAvailableDx = recordButtonAvailableDx,
                    recordButtonAvailableDy = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_button_vertical_movement_distance).toFloat(),
                    recordFieldHeight = context.getDimenPx(RDesign.attr.inlineHeight_2xs),
                    recordFieldVerticalSpacing = context.getDimenPx(RDesign.attr.offset_xs),
                    ignoreHorizontalMovementDistance = recordButtonAvailableDx * IGNORE_HORIZONTAL_MOVEMENT_DISTANCE_PERCENT
                )
            }
        }
    }
}

/**
 * Продолжительность анимации возвращения кнопки в исходное положение в мс.
 */
private const val RECOVER_BUTTON_POSITION_DURATION_MS = 180L

private const val MAX_CANCEL_TEXT_SIZE_DP = 18
private const val MIN_CANCEL_TEXT_SIZE_DP = 14

/**
 * Процент игнорируемого смещения по оси X, для возможности начала движения по оси Y.
 */
internal const val IGNORE_HORIZONTAL_MOVEMENT_DISTANCE_PERCENT = 0.05f

/**
 * Слушатель изменений позиции кнопки.
 */
internal typealias OnButtonPositionChangedListener = (Float, Float) -> Unit

/**
 * Слушатель инициативы отправки записи.
 */
internal typealias OnSendListener = () -> Unit

/**
 * Слушатель инициативы отмены записи.
 */
internal typealias OnCancelListener = () -> Unit