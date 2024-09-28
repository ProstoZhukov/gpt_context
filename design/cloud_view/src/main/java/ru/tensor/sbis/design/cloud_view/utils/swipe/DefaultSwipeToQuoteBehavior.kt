package ru.tensor.sbis.design.cloud_view.utils.swipe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
import android.view.HapticFeedbackConstants.KEYBOARD_TAP
import android.view.View
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.custom_view_tools.utils.animation.CubicBezierInterpolator
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.utils.findViewParent
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Асбтрактная реализация компонента для цитирования по свайпу.
 * @see MessageSwipeToQuoteBehavior
 *
 * @author vv.chekurda
 */
abstract class DefaultSwipeToQuoteBehavior(context: Context) : MessageSwipeToQuoteBehavior {

    // region cloud data
    /**
     * Вью которая учавствует в свайпе.
     */
    protected lateinit var view: View

    /**
     * Признак исходящего сообщения.
     */
    protected var isOutcome = false

    /**
     * Список View, которые участвуют в смещении по свайпу.
     */
    protected lateinit var translatableViews: List<View>
    // endregion

    // region checkpoints
    /**
     * Максимальное смещение ячейки по свайпу в px.
     */
    private val maxSwipeDx = -context.resources.dp(MAX_SWIPE_DX_DP).toFloat()

    /**
     * Триггерное смещение по свайпу в px, на котором появляется стрелка и цитата может быть активирована.
     */
    private val quoteTriggerDx = -context.resources.dp(QUOTE_TRIGGER_DX_DP).toFloat()

    /**
     * Смещение в px, по достижению которого время полностью скрывается.
     */
    private val timeHideDx = quoteTriggerDx

    /**
     * Стартовая позиция появления стрелки цитирования относительно правого края ячейки.
     */
    protected val arrowStartPosition = -context.resources.dp(ARROW_START_POSITION_DP)
    // endregion

    /**
     * Интерполятор для анимации прозрачности view у входящих сообщений.
     */
    private val incomingAlphaInterpolator = CubicBezierInterpolator.superSmoothDecelerateInterpolator

    /**
     * Признак необходимости повторной отрисовки.
     */
    private var requireInvalidate: Boolean = false

    // region swipe info
    /**
     * Признак того, что ячейка находится в состоянии свайпа.
     */
    private var isSwipeRunning = false

    /**
     * Признак того, что пользователь свайпает ячейку активным касанием.
     */
    private var isSwipingByTouch = false

    /**
     * Начальное смещение, от которого начнется смещение view представления.
     * Необходимо для избежания резких скачков во время распознавания движений касания пользователя.
     */
    private var swipeStartDx = 0f
    // endregion

    // region arrow info
    /**
     * Drawable стрелки цитирования.
     */
    private val arrowDrawable by lazy { ContextCompat.getDrawable(context, R.drawable.cloud_view_ic_reply)!! }

    /**
     * Половина размера стрелки по высоте.
     */
    protected val arrowHalfWidth: Int
        get() = arrowDrawable.intrinsicWidth / 2

    /**
     * Половина размера стрелки по ширине.
     */
    protected val arrowHalfHeight: Int
        get() = arrowDrawable.intrinsicHeight / 2

    /**
     * Размер нижнего отступа [arrowDrawable] для выравнивания по базовой линии.
     */
    protected val arrowDrawableBottomSpacing = context.resources.dp(ARROW_DRAWABLE_BOTTOM_SPACING_DP)
    // endregion

    // region arrow animation
    /**
     * Признак необходимости отображения стрелки цитирования.
     */
    private var isArrowMustBeVisible = false

    /**
     * Признак того, что анимация стрелки запущена.
     */
    private var isArrowAnimationRunning = false

    /**
     * Прогресс анимации стрелки:
     * 0 - полностью скрыта.
     * от 0 до 1 - появление.
     * 1 - полностью отображается.
     * от 1 до 0 - скрытие.
     */
    private var arrowAnimationProgress = 0f

    /**
     * Последнее время отрисовки стрелки для вычисления dt для анимации.
     */
    private var lastArrowDrawTime = 0L
    // endregion

    // region recover animation
    /**
     * Признак того, что запущена анимация восстановления изначальной позиции после свайпа.
     */
    private var isRecoverAnimationRunning = false

    /**
     * Начальная позиция, с которой начинается анимация восстановления изначальной позиции ячейки после свайпа.
     */
    private var recoverStartDx = 0f

    /**
     * Время старта начана анимации восстановления изначальной позиции ячейки после свайпа.
     */
    private var recoverStartTime = 0L

    /**
     * Интерполятор для анимации восстановления изначальной позиции ячейки после свайпа.
     */
    private var recoverInterpolator = CubicBezierInterpolator.superSmoothDecelerateInterpolator
    // endregion

    private var parentRecycler: RecyclerView? = null
    private val viewDrawingRect = Rect()

    /**
     * Смещение ячейки по оси X в px.
     */
    private var dx: Float = 0f
        set(value) {
            if (value == field) return
            field = value
            translateViews(value)
        }

    fun init(view: View, isOutcome: Boolean) {
        this.view = view
        this.isOutcome = isOutcome
    }

    abstract fun translateViews(dx: Float)
    abstract fun alphaViews(alpha: Float)
    /**
     * Получить позицию стрелки с координатами на осях X и Y.
     */
    abstract fun getArrowPosition(): Pair<Int, Int>

    override var canBeQuoted: Boolean = false
    override var swipeToQuoteListener: CloudSwipeToQuoteListener? = null

    override val movementFlags: Int
        get() = if (canBeQuoted && !isSwipeRunning) {
            makeMovementFlags(0, LEFT)
        } else {
            makeMovementFlags(0, 0)
        }.also {
            if (isSwipeRunning && dx <= quoteTriggerDx) {
                swipeToQuoteListener?.invoke()
            }
        }

    /** @SelfDocumented */
    fun onAttachedToWindow() {
        parentRecycler = findViewParent(view)
    }

    /** @SelfDocumented */
    fun onDetachedFromWindow() {
        parentRecycler = null
        clearState()
    }

    /**
     * Очистить состояние.
     */
    fun clearState() {
        dx = 0f
        isSwipeRunning = false
        isRecoverAnimationRunning = false
        isSwipingByTouch = false
        isArrowMustBeVisible = false
        requireInvalidate = false
        alphaViews(1f)
    }

    override fun draw(canvas: Canvas, dx: Float, isSwiping: Boolean): Boolean {
        updateSwipeState(dx, isSwiping)
        this.dx = getSmoothDx(dx)
        updateLayout(canvas)
        return requireInvalidate
    }

    protected fun getViewTopByRecycler(): Int {
        view.getDrawingRect(viewDrawingRect)
        parentRecycler?.offsetDescendantRectToMyCoords(view, viewDrawingRect)
        return viewDrawingRect.top
    }

    /**
     * Обновить состояние свайпа.
     *
     * @param originDx необработанное смещение по оси X.
     * @param isSwiping true, если пользователь свайпает ячейку.
     */
    private fun updateSwipeState(originDx: Float, isSwiping: Boolean) {
        this.isSwipeRunning = originDx != 0f
        requireInvalidate = false

        if (!isSwiping && isSwipingByTouch) {
            startRecoverAnimation(originDx)
        } else if (isRecoverAnimationRunning && !isSwipeRunning) {
            finishRecoverAnimation()
        }
        isSwipingByTouch = isSwiping
    }

    /**
     * Начать анимацию восстановления к исходной позиции ячейки.
     *
     * @param originDx необработанное смещение по оси X.
     */
    private fun startRecoverAnimation(originDx: Float) {
        val swipeDx = getSwipeDx(originDx)
        // this.dx предотвращает небольшое непроизвольное смещение в сторону свайпа в момент отпускания пальца.
        recoverStartDx = maxOf(swipeDx, this.dx)
        if (recoverStartDx <= 0) {
            isRecoverAnimationRunning = true
            recoverStartTime = System.currentTimeMillis()
        }
    }

    /**
     * Закончить анимацию восстановления к исходной позиции ячейки.
     */
    private fun finishRecoverAnimation() {
        recoverStartDx = 0f
        isRecoverAnimationRunning = false
    }

    /**
     * Получить сглаженное смещение по оси X.
     *
     * @param originDx необработанное смещение по оси X.
     */
    private fun getSmoothDx(originDx: Float): Float =
        if (isRecoverAnimationRunning) {
            getRecoverDx()
        } else {
            getSwipeDx(originDx)
        }

    /**
     * Получить смещение по оси Х для анимации восстановления исходной позиции ячейки после свайпа.
     */
    private fun getRecoverDx(): Float {
        val dt = System.currentTimeMillis() - recoverStartTime
        val progress = minOf(dt / SWIPE_RECOVER_DURATION_MS.toFloat(), 1f)
        val interpolation = 1 - recoverInterpolator.getInterpolation(progress)
        return interpolation * recoverStartDx
    }

    /**
     * Получить смещение по оси Х для свайпа ячейки.
     *
     * @param originDx необработанное смещение по оси X.
     */
    private fun getSwipeDx(originDx: Float): Float {
        val isFirstSwipeDx = swipeStartDx == 0f && this.dx == 0f && originDx != 0f
        val isNewSwipeStartDx = swipeStartDx <= originDx
        return if (isFirstSwipeDx || isNewSwipeStartDx) {
            /* Сохраняем смещение, как точку старта для начала свайпа, чтобы избежать резкого смещения
            из-за необходимой дистанции для распознавания движения пальца на маленькой скорости. */
            swipeStartDx = originDx
            0f
        } else {
            maxOf(originDx - swipeStartDx, maxSwipeDx)
        }
    }

    /**
     * Обновить разметку.
     */
    private fun updateLayout(canvas: Canvas) {
        changeIncomingTimeAlpha()
        updateArrowState()
        drawArrow(canvas)
    }

    /**
     * Изменить прозрачность времени и статусов у ячейки входящего сообщения.
     */
    private fun changeIncomingTimeAlpha() {
        if (isOutcome) return
        val timeAlpha = when {
            dx <= timeHideDx -> 0f
            dx >= 0 -> 1f
            else -> 1 - incomingAlphaInterpolator.getInterpolation(dx / timeHideDx)
        }
        alphaViews(timeAlpha)
    }

    /**
     * Обновить состояние стрелки цитирования.
     */
    private fun updateArrowState() {
        if (dx <= quoteTriggerDx && !isArrowMustBeVisible) {
            view.performHapticFeedback(KEYBOARD_TAP, FLAG_IGNORE_GLOBAL_SETTING)
            isArrowMustBeVisible = true
        } else if (dx > quoteTriggerDx && isArrowMustBeVisible) {
            isArrowMustBeVisible = false
        }
    }

    /**
     * Нарисовать стрелку цитирования.
     *
     * @param canvas холст для рисования.
     */
    private fun drawArrow(canvas: Canvas) {
        val dt = getArrowDrawTimeDt()
        if (isArrowMustBeVisible) {
            drawShowingArrow(canvas, dt)
        } else if (arrowAnimationProgress > 0f) {
            drawHidingArrow(canvas, dt)
        }
    }

    /**
     * Получить дельту времени отрисовки относительно последнего кадра.
     * Также метод сохраняет текущее время отрисовки.
     */
    private fun getArrowDrawTimeDt(): Long {
        val currentTime = System.currentTimeMillis()
        return minOf(currentTime - lastArrowDrawTime, FRAME_RATE_MS).also {
            lastArrowDrawTime = currentTime
        }
    }

    /**
     * Получить смещение стрелки во время свайпа.
     * Скорость стрелки в 2 раза меньше скорости ячейки.
     */
    protected fun getArrowSwipeDx(): Int =
        ((dx - quoteTriggerDx) / 2).roundToInt()

    /**
     * Нарисовать отображение стрелки.
     *
     * @param canvas холст для рисования.
     * @param dt дельта времени с последней отрисовки.
     */
    private fun drawShowingArrow(canvas: Canvas, dt: Long) {
        val (x, y) = getArrowPosition()
        val needStartArrowShowingAnimation = !isArrowAnimationRunning && arrowAnimationProgress == 0f
        when {
            needStartArrowShowingAnimation -> {
                isArrowAnimationRunning = true
            }
            arrowAnimationProgress < 1f -> {
                arrowAnimationProgress = minOf(arrowAnimationProgress + dt / ARROW_ANIMATION_DURATION_MS, 1f)
                isArrowAnimationRunning = arrowAnimationProgress != 1f

                val scale = if (arrowAnimationProgress <= ARROW_DOWNSCALE_PROGRESS_POINT) {
                    val scaleProgress = arrowAnimationProgress / ARROW_DOWNSCALE_PROGRESS_POINT
                    ARROW_MAX_SCALE * scaleProgress
                } else {
                    val downScaleProgress = (arrowAnimationProgress - ARROW_DOWNSCALE_PROGRESS_POINT) /
                        (1f - ARROW_DOWNSCALE_PROGRESS_POINT)
                    val downScaleDiff = abs(ARROW_MAX_SCALE - 1f) * downScaleProgress
                    ARROW_MAX_SCALE - downScaleDiff
                }
                val alpha = minOf(arrowAnimationProgress / ARROW_DOWNSCALE_PROGRESS_POINT, 1f)

                drawArrowDrawable(canvas, x, y, scale, alpha)
                requireInvalidate = true
            }
            else -> {
                drawArrowDrawable(canvas, x, y, scale = 1f, alpha = 1f)
            }
        }
    }

    /**
     * Нарисовать скрытие стрелки.
     *
     * @param canvas холст для рисования.
     * @param dt дельта времени с последней отрисовки.
     */
    private fun drawHidingArrow(canvas: Canvas, dt: Long) {
        val (x, y) = getArrowPosition()
        val needStartArrowHidingAnimation = !isArrowAnimationRunning && arrowAnimationProgress == 1f
        when {
            needStartArrowHidingAnimation -> {
                isArrowAnimationRunning = true
                drawArrowDrawable(canvas, x, y, scale = 1f, alpha = 1f)
            }
            dx < 0 -> {
                arrowAnimationProgress = maxOf(arrowAnimationProgress - dt / ARROW_ANIMATION_DURATION_MS, 0f)
                isArrowAnimationRunning = arrowAnimationProgress != 0f
                drawArrowDrawable(canvas, x, y, scale = arrowAnimationProgress, alpha = arrowAnimationProgress)
            }
            else -> {
                arrowAnimationProgress = 0f
                isArrowAnimationRunning = false
            }
        }
        requireInvalidate = true
    }

    /**
     * Нарисовать Drawable стрелки [arrowDrawable] по заданным координатам [x] и [y].
     *
     * @param canvas холст для рисования.
     * @param x координата на оси X.
     * @param y координата на оси Y.
     * @param scale масштаб стрелки.
     * @param alpha прозрачность стрелки.
     */
    private fun drawArrowDrawable(
        canvas: Canvas,
        x: Int,
        y: Int,
        @FloatRange(from = 0.0, to = 1.0) scale: Float,
        @FloatRange(from = 0.0, to = 1.0) alpha: Float
    ) {
        arrowDrawable.also {
            it.setBounds(
                (x - scale * arrowHalfWidth).toInt(),
                (y - scale * arrowHalfHeight).toInt(),
                (x + scale * arrowHalfWidth).toInt(),
                (y + scale * arrowHalfHeight).toInt()
            )
            it.alpha = minOf(alpha * MAX_PAINT_ALPHA, MAX_PAINT_ALPHA).toInt()
            it.draw(canvas)
        }
    }
}

/**
 * Стандартное время продолжительности анимации восстановления ячейки-облака к исходной позиции после свайпа.
 */
internal const val SWIPE_RECOVER_DURATION_MS = 200L

/**
 * Максимальное смещение ячейки в dp.
 */
private const val MAX_SWIPE_DX_DP = 80

/**
 * Триггерное смещение для активации свайпа в dp.
 */
private const val QUOTE_TRIGGER_DX_DP = 40

/**
 * Продолжительность анимации появления и скрытия стрелки цитирования в мс.
 */
private const val ARROW_ANIMATION_DURATION_MS = 200f

/**
 * Максимальный масштаб стрелки во время анимации.
 */
private const val ARROW_MAX_SCALE = 1.25f

/**
 * Точка прогресса анимации показа стрелки цитирования, по достижению которой начинается обратное масштабирование.
 */
private const val ARROW_DOWNSCALE_PROGRESS_POINT = 0.75f

/**
 * Начальная позиция появления стрелки цитирования в dp.
 */
private const val ARROW_START_POSITION_DP = 10

/**
 * Размер нижнего отступа в dp от нижнего края drawable стрелки до кончика стрелки. (Для выравнивания по базовой линии)
 */
private const val ARROW_DRAWABLE_BOTTOM_SPACING_DP = 1

/**
 * Стандартный период отрисовки кадров в мс.
 */
private const val FRAME_RATE_MS = 17L

/**
 * Максимальное значение прозрачности у краски.
 */
private const val MAX_PAINT_ALPHA = 255f