package ru.tensor.sbis.onboarding_tour.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.onboarding_tour.R
import ru.tensor.sbis.design.R as RDesign

/**
 * [View] с индикаторами.
 *
 * @author ar.leschev
 *
 * TODO https://online.sbis.ru/opendoc.html?guid=263ca51b-6ee6-4d50-b198-87eca0a3d433&client=3
 */
internal class IndicatorsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val unselectedPaint: Paint
    private val selectedPaint: Paint
    private val indicatorSize: Float
    private val offset: Float
    private val radius get() = indicatorSize / 2
    private var xCoordinates = emptyList<Float>()

    /**
     * Текущий выбранный элемент в пределах [itemCount]
     */
    var currentItem: Int = DEFAULT_CURRENT
        set(value) {
            if (field == value) return
            if (value !in DEFAULT_CURRENT..itemCount) return

            field = value
            invalidate()
        }

    var itemCount: Int = DEFAULT_COUNT
        set(value) {
            if (field == value) return
            field = value
            requestLayout()
        }

    init {
        setWillNotDraw(false)
        with(context.obtainStyledAttributes(attrs, R.styleable.IndicatorsView)) {
            indicatorSize =
                getDimensionPixelSize(R.styleable.IndicatorsView_IndicatorsView_size, DEFAULT_INDICATOR_SIZE).toFloat()
            offset = getDimension(R.styleable.IndicatorsView_IndicatorsView_offset, indicatorSize)
            selectedPaint = getColor(
                R.styleable.IndicatorsView_IndicatorsView_selectedColor,
                context.getThemeColorInt(RDesign.attr.navigationMarkerColor)
            ).let(::createPaint)
            unselectedPaint = getColor(
                R.styleable.IndicatorsView_IndicatorsView_unselectedColor,
                context.getThemeColorInt(RDesign.attr.unaccentedIconColor)
            ).let(::createPaint)
        }
        xCoordinates = List(itemCount) { radius + (it * indicatorSize) + (it * offset) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val onlyIndicators = indicatorSize * itemCount
        val onlyOffsets = offset * itemCount - 1
        val desiredWidth = onlyIndicators + onlyOffsets + paddingLeft + paddingRight
        val desiredHeight = indicatorSize + paddingTop + paddingBottom
        setMeasuredDimension(desiredWidth.toInt(), desiredHeight.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        xCoordinates = List(itemCount) { radius + (it * indicatorSize) + (it * offset) }
        repeat(itemCount) {
            canvas?.drawCircle(xCoordinates[it], radius, radius, selectPaintFor(it))
        }
    }

    private fun createPaint(@ColorInt indicatorColor: Int): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = indicatorColor
            style = Paint.Style.FILL
        }

    private fun selectPaintFor(item: Int): Paint =
        if (item == currentItem) {
            selectedPaint
        } else {
            unselectedPaint
        }

    private companion object {
        const val DEFAULT_COUNT = 1
        const val DEFAULT_CURRENT = 0
        const val DEFAULT_INDICATOR_SIZE = 8
    }
}