package ru.tensor.sbis.design.view_ext.indicator

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.view.GestureDetectorCompat
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.view_ext.R
import kotlin.math.roundToInt

/**
 * Вью индикатора с точками
 *
 * @author aa.prischep
 */
class SbisIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.sbisIndicatorTheme,
    @StyleRes defStyleRes: Int = R.style.SbisIndicatorStyle,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val MAX_VISIBLE_DOTS_COUNT_DEFAULT = 6
        private const val DOT_SIZE_DEFAULT = 8
        private const val SMALL_DOT_SCALE = 0.65f
        private const val MAX_ALPHA = 255
        private const val DOT_SPACE_DEFAULT = 4
        private const val BORDER_WIDTH = 2f
    }

    @ColorInt
    private val selectedDotColor: Int

    @ColorInt
    private val unselectedDotColor: Int

    @ColorInt
    private val selectedBorderColor: Int

    @ColorInt
    private val unselectedBorderColor: Int
    private val withBorder: Boolean

    private val dotSize: Int
    private val dotSpace: Int
    private val maxVisibleDotsCount: Int
    private val maxDotsCount: Int
    private val centerDotsCount: Int

    private val dotPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val dotsList: ArrayList<SbisIndicatorDot> = arrayListOf()

    private var dotOffset = 0

    private var lastPositionWithoutPositionOffset = 0
    private var dotOffsetWithoutPositionOffset = 0

    private var lastCenterSelectedDot = 0
    private var lastPosition = 0
    private var lastDotPosition = 0

    private val argbEvaluator = ArgbEvaluator()

    private var isChangedCount: Boolean = false

    private var onDotAreaClick: (item: Int) -> Unit = {}

    /**
     * Количество позиций
     */
    var count: Int = 0
        set(value) {
            if (field != value) {
                field = value
                setDots()
                if (value > 0) setStartPosition()
                requestLayout()
            }
        }

    /**
     * Выбранная позиция
     */
    var selectedPosition: Pair<Int, Float> = 0 to 0f
        set(value) {
            if ((field != value || isChangedCount) && value.first < count) {
                val position = value.first
                val lastDotPosition = getSelectedDotIndex()
                val lastPosition = field.first

                if (position < lastPosition) setLastPassedDot(lastPosition, dotOffset)

                field = value

                if (lastPosition != position || isChangedCount) {
                    this.lastDotPosition = lastDotPosition
                    this.lastPosition = lastPosition
                    calculateDotsOffset()
                }

                if (selectedPosition.second == 0f) setLastPassedDot(position, dotOffset)

                changeDotDataForDraw()
                invalidate()
            }
        }

    init {
        setWillNotDraw(false)
        with(
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.SbisIndicator,
                defStyleAttr,
                defStyleRes
            )
        ) {
            selectedDotColor = getColor(R.styleable.SbisIndicator_SbisIndicator_selectedDotColor, 0)
            unselectedDotColor =
                getColor(R.styleable.SbisIndicator_SbisIndicator_unselectedDotColor, 0)
            withBorder = getBoolean(R.styleable.SbisIndicator_SbisIndicator_withBorder, false)
            selectedBorderColor =
                getColor(R.styleable.SbisIndicator_SbisIndicator_selectedDotBorderColor, selectedDotColor)
            unselectedBorderColor =
                getColor(R.styleable.SbisIndicator_SbisIndicator_unselectedDotBorderColor, unselectedDotColor)
            maxVisibleDotsCount =
                getInt(
                    R.styleable.SbisIndicator_SbisIndicator_maxVisibleDotsCount,
                    MAX_VISIBLE_DOTS_COUNT_DEFAULT
                )
            dotSize = getDimensionPixelSize(R.styleable.SbisIndicator_SbisIndicator_dotSize, dp(DOT_SIZE_DEFAULT))
            dotSpace = getDimensionPixelSize(R.styleable.SbisIndicator_SbisIndicator_dotSpace, dp(DOT_SPACE_DEFAULT))

            maxDotsCount = maxVisibleDotsCount + 1
            centerDotsCount = maxVisibleDotsCount - 2

            recycle()
        }
    }

    private val gestureDetector: GestureDetectorCompat by lazy {
        GestureDetectorCompat(context, gestureListener)
    }

    private val gestureListener: GestureDetector.OnGestureListener =
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean =
                getIndicatorViewRect().contains(event.x.roundToInt(), event.y.roundToInt())

            override fun onSingleTapUp(event: MotionEvent): Boolean =
                getIndicatorViewRect().contains(event.x.roundToInt(), event.y.roundToInt()).also { inRect ->
                    if (inRect) {
                        if (getViewRectToTheLeft().contains(event.x.roundToInt(), event.y.roundToInt())) {
                            val newPosition = (selectedPosition.first - 1).takeIf { selectedPosition.first != 0 } ?: 0
                            onDotAreaClick(newPosition)
                        }

                        if (getViewRectToTheRight().contains(event.x.roundToInt(), event.y.roundToInt())) {
                            val newPosition =
                                (selectedPosition.first + 1).takeIf { selectedPosition.first != count - 1 }
                                    ?: (count - 1)
                            onDotAreaClick(newPosition)
                        }
                    }
                }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        event.let { gestureDetector.onTouchEvent(it) } || super.onTouchEvent(event)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dotsCount = minOf(count, maxVisibleDotsCount)
        val width = dotsCount * dotSize + (dotsCount - 1) * dotSpace + paddingStart + paddingEnd

        setMeasuredDimension(width, dotSize)
    }

    override fun onDraw(canvas: Canvas) {
        val dotRadius = dotSize.toFloat() / 2

        dotsList.forEach {
            canvas?.drawCircle(
                it.left + dotRadius,
                dotRadius,
                it.size / 2,
                dotPaint.apply {
                    style = Paint.Style.FILL
                    alpha = it.alpha
                    color = it.dotColor
                }
            )

            if (withBorder) {
                canvas?.drawCircle(
                    it.left + dotRadius,
                    dotRadius,
                    it.size / 2,
                    dotPaint.apply {
                        style = Paint.Style.STROKE
                        strokeWidth = BORDER_WIDTH
                        alpha = it.alpha
                        color = it.borderColor
                    })
            }
        }
    }

    private fun setDots() {
        dotsList.clear()
        repeat(minOf(count, maxDotsCount)) { dotsList.add(SbisIndicatorDot()) }
    }

    private fun calculateDotsOffset() {
        val position = selectedPosition.first

        if (lastPositionWithoutPositionOffset == position) dotOffset = dotOffsetWithoutPositionOffset

        when {
            position == 0 || isCountMoreThenMax().not() -> {
                dotOffset = 0
                lastCenterSelectedDot = 0
            }

            isDotNotNearby() || isChangedCount -> recalculateOffset()
            isCentralDot() -> lastCenterSelectedDot = getSelectedDotIndex()
            isEndDot().not() -> dotOffset = position - lastCenterSelectedDot
        }
    }

    /**
     * Расчитать значения без учета предыдущих позиций
     */
    private fun recalculateOffset() {
        val position = selectedPosition.first
        dotOffset = (position - (maxVisibleDotsCount - 2)).coerceAtLeast(0) -
            if (isEndDot()) 1 else 0
        lastCenterSelectedDot = getSelectedDotIndex().coerceIn(1..centerDotsCount)
    }

    private fun isCentralDot() = (1..centerDotsCount).contains(getSelectedDotIndex())

    private fun isDotNotNearby() =
        selectedPosition.first != lastPosition + 1 && selectedPosition.first != lastPosition - 1

    private fun isEndDot() = selectedPosition.first == count - 1

    private fun changeDotDataForDraw() {
        val dotSize = dotSize.toFloat() - if (withBorder) BORDER_WIDTH else 0f

        dotsList.forEachIndexed { index, dot ->
            val scale = index.getDotScale()

            dot.left = index.getDotLeft()
            dot.size = dotSize * scale
            dot.dotColor = index.getDotColor(selectedDotColor, unselectedDotColor)
            dot.borderColor = if (withBorder) index.getDotColor(selectedBorderColor, unselectedBorderColor) else 0
            dot.alpha = (MAX_ALPHA * scale).toInt()
        }
    }

    private fun Int.getDotLeft(): Float {
        val offset = if (isMove()) selectedPosition.second else 0f
        val interval = dotSize + dotSpace
        val initialDotLeft = this * interval
        return paddingStart + initialDotLeft - interval * offset
    }

    /**
     * Коэффициенты масштабирования выведены из уравнений, где dotSizeDefault = [dotSize],
     * smallDotSize = dotSizeDefault * [SMALL_DOT_SCALE]
     * 2: dotSize = dotSizeDefault - offset * dotSizeDefault
     * 3: dotSize = smallDotSize - offset * smallDotSize
     * 4: dotSize = offset * dotSizeDefault
     * 5: dotSize = offset * smallDotSize
     * 6: dotSize = dotSizeDefault - offset * (dotSizeDefault - smallDotSize)
     * 7: dotSize = smallDotSize + offset * (dotSizeDefault - smallDotSize)
     */
    private fun Int.getDotScale(): Float {
        val offset = if (isMove()) selectedPosition.second else 0f
        return when {
            isCountMoreThenMax().not() -> 1f
            this == 0 && dotOffset == 0 -> 1 - offset
            this == 0 -> SMALL_DOT_SCALE - offset * SMALL_DOT_SCALE
            this == maxDotsCount - 1 && dotOffset == (count - 3) - (maxVisibleDotsCount - 2) -> offset
            this == maxDotsCount - 1 -> offset * SMALL_DOT_SCALE
            this == 1 -> 1f - offset + offset * SMALL_DOT_SCALE
            this == maxDotsCount - 2 && dotOffset < (count - 2) - (maxVisibleDotsCount - 2) ->
                SMALL_DOT_SCALE + offset - offset * SMALL_DOT_SCALE

            else -> 1f
        }
    }

    private fun isMove(): Boolean {
        val moreThenMax = isCountMoreThenMax()
        val selectedDot = getSelectedDotIndex()
        val position = selectedPosition.first

        val isMoveLeft =
            moreThenMax && (selectedDot == 1 && position < lastPositionWithoutPositionOffset && lastDotPosition == 1)
        val isMoveRight =
            moreThenMax && (selectedDot == maxVisibleDotsCount - 2 && position != count - 2)
        return isMoveLeft || isMoveRight
    }

    private fun Int.getDotColor(selectedColor: Int, unselectedColor: Int): Int {
        val selectedDot = getSelectedDotIndex()
        val offset = selectedPosition.second

        return when (this) {
            selectedDot -> argbEvaluator.evaluate(offset, selectedColor, unselectedColor) as Int
            selectedDot + 1 -> argbEvaluator.evaluate(offset, unselectedColor, selectedColor) as Int
            else -> unselectedColor
        }
    }

    private fun getSelectedDotIndex() = selectedPosition.first - dotOffset

    private fun isCountMoreThenMax() = count > maxVisibleDotsCount

    /**
     * Нужно для установки предыдущего dotOffset, если при скроле влево не дошли до offset=0 и вернулись обратно
     */
    private fun setLastPassedDot(position: Int, dotOffset: Int) {
        dotOffsetWithoutPositionOffset = dotOffset
        lastPositionWithoutPositionOffset = position
    }

    private fun setStartPosition() {
        isChangedCount = true
        selectedPosition = selectedPosition.takeIf { count > selectedPosition.first } ?: (count - 1 to 0f)
        isChangedCount = false
    }

    // Область всей вью
    private fun getIndicatorViewRect(): Rect =
        Rect(0, 0, measuredWidth, measuredHeight)

    // Область левее выбранного индикатора
    private fun getViewRectToTheLeft(): Rect =
        Rect(0, 0, dotsList[selectedPosition.first].left.toInt(), measuredHeight)

    // Область правее выбранного индикатора
    private fun getViewRectToTheRight(): Rect =
        Rect(
            dotsList[selectedPosition.first].left.toInt() + dotsList[selectedPosition.first].size.toInt(),
            0,
            measuredWidth,
            measuredHeight
        )

    /**
     * Установка слушателя по области индикатора
     */
    fun setOnAreaClickListener(listener: (item: Int) -> Unit) {
        onDotAreaClick = listener
    }
}

