package ru.tensor.sbis.segmented_control

import android.animation.AnimatorSet
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.animation.addListener
import androidx.core.view.doOnLayout
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.segmented_control.control.api.SbisSegmentedControlApi
import ru.tensor.sbis.segmented_control.control.api.SbisSegmentedControlController
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlDistribution
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlSize
import ru.tensor.sbis.segmented_control.item.SbisSegmentedControlItem
import ru.tensor.sbis.segmented_control.utils.BackgroundOutlineProvider
import ru.tensor.sbis.segmented_control.utils.SavedState
import ru.tensor.sbis.segmented_control.utils.SegmentedControlStyleHolder
import kotlin.math.roundToInt

/**
 * Компонент сегмент-контрол.
 * Предназначен для выбора одного из нескольких взаимоисключающих значений.
 *
 * [Стандарт](https://www.figma.com/file/X89AbolbCz2nG69ZVDF6Xj/%D0%A1%D0%B5%D0%B3%D0%BC%D0%B5%D0%BD%D1%82%D0%BD%D1%8B%D0%B9-%D0%BF%D0%B5%D1%80%D0%B5%D0%BA%D0%BB%D1%8E%D1%87%D0%B0%D1%82%D0%B5%D0%BB%D1%8C-(Web%2C-Mobile)?type=design&node-id=4390-4086&mode=design&t=qggsLePH5rfzawSo-11)
 *
 * @author ps.smirnyh
 */
class SbisSegmentedControl internal constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: SbisSegmentedControlController
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes),
    SbisSegmentedControlApi by controller {

    @Suppress("UNUSED")
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.sbisSegmentedControlDefaultTheme,
        @StyleRes defStyleRes: Int = R.style.SbisSegmentedControlDefaultTheme
    ) : this(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        SbisSegmentedControlController()
    )

    private var leftSideSelector = 0f
    private var rightSideSelector = 0f

    private val selectorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val selector = RectF()

    init {
        setWillNotDraw(false)
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
        outlineProvider = BackgroundOutlineProvider()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec = MeasureSpec.makeMeasureSpec(minimumHeight, MeasureSpec.EXACTLY)
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val margins = (layoutParams as? MarginLayoutParams)?.run {
            marginStart + marginEnd
        } ?: 0
        val freeSpace = parentWidth - margins
        val width = if (distribution == SbisSegmentedControlDistribution.EQUAL) {
            measureStrictSizeItems(controller.listSegments, widthMeasureSpec, heightSpec, freeSpace)
        } else {
            measureFlexibleSizeItems(controller.listSegments, freeSpace, heightSpec)
        }
        setMeasuredDimension(width, minimumHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var dX = 0
        controller.listSegments.forEachIndexed { index, child ->
            child.layout(dX, 0)
            dX += child.measuredWidth

            if (controller.selectedSegmentIndex == index) {
                leftSideSelector = child.left.toFloat()
                rightSideSelector = child.right.toFloat()
            }
        }
    }

    override fun onDraw(canvas: Canvas) = with(controller) {
        super.onDraw(canvas)
        val halfHeight = height / 2f
        val cornerRadius = if (styleHolder.cornerRadius < 0) {
            halfHeight
        } else {
            styleHolder.cornerRadius.toFloat()
        }
        canvas.drawRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            cornerRadius,
            cornerRadius,
            backgroundPaint
        )
        selector.set(
            leftSideSelector + styleHolder.borderWidth,
            styleHolder.borderWidth.toFloat(),
            rightSideSelector - styleHolder.borderWidth,
            height.toFloat() - styleHolder.borderWidth
        )
        val halfHeightSelector = selector.height() / 2
        canvas.drawRoundRect(
            selector,
            halfHeightSelector,
            halfHeightSelector,
            selectorPaint
        )
    }

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()).apply {
            selectedSegmentIndex = controller.selectedSegmentIndex
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            controller.selectedSegmentIndex = state.selectedSegmentIndex
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams = distribution.getLayoutParams()

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        super.addView(child, index, params)
        (child as? SbisSegmentedControlItem)?.run(controller.listSegments::add)
    }

    override fun removeView(view: View?) {
        super.removeView(view)
        (view as? SbisSegmentedControlItem)?.run(controller.listSegments::remove)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        onStateUpdateChildren()
    }

    /** Callback на изменение состояния элементов. */
    internal fun onStateUpdateChildren() = with(controller) {
        listSegments.forEach {
            it.onStateChanged(
                isEnabled,
                styleHolder.getItemTextColorByEnabled(contrast),
                styleHolder.getItemIconColorByEnabled(contrast)
            )
        }
        if (isEnabled && listSegments.isNotEmpty()) {
            listSegments[selectedSegmentIndex].onSelectedChanged(
                true,
                styleHolder.getItemTextColorBySelected(contrast),
                styleHolder.getItemIconColorBySelected(contrast)
            )
        }
    }

    /** Изменить выбранный элемент. */
    internal fun changeSelectedSegment(
        segment: View,
        animated: Boolean
    ) {
        doOnLayout {
            val oldLeftPos = leftSideSelector
            val newLeftPos = segment.left.toFloat()
            val oldRightPos = rightSideSelector
            val newRightPos = segment.right.toFloat()

            if (animated) {
                changeSelectedSegmentWithAnimation(oldLeftPos, oldRightPos, newLeftPos, newRightPos)
            } else {
                changeSelectedSegmentWithoutAnimation(newLeftPos, newRightPos)
            }
        }
    }

    /** Callback на изменение стиля. */
    internal fun onStyleChanged(styleHolder: SegmentedControlStyleHolder, isContrast: Boolean) {
        selectorPaint.color =
            if (isContrast) styleHolder.itemBackgroundColorContrast else styleHolder.itemBackgroundColor
        backgroundPaint.color = if (isContrast) styleHolder.backgroundColorContrast else styleHolder.backgroundColor
        onStateUpdateChildren()
        invalidate()
    }

    /** Callback на изменение размера. */
    internal fun onSizeChanged(size: SbisSegmentedControlSize) {
        minimumHeight = size.globalVar.getDimenPx(context)
        controller.listSegments.forEach { it.size = size }
        safeRequestLayout()
    }

    private fun measureStrictSizeItems(
        children: List<View>,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        @Px freeSpace: Int
    ): Int {
        if (children.isEmpty()) return 0
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val childWidthSpec = widthMeasureSpec.takeIf { widthMode == MeasureSpec.UNSPECIFIED }
            ?: MeasureSpec.makeMeasureSpec(freeSpace / children.size, MeasureSpec.EXACTLY)
        children.forEach {
            measureChild(it, childWidthSpec, heightMeasureSpec)
        }
        return freeSpace
    }

    private fun measureFlexibleSizeItems(
        children: List<View>,
        @Px freeSpace: Int,
        heightSpec: Int
    ): Int {
        var flexibleWidth = 0
        var measuredContentWidth = 0
        val widthSpec = MeasureSpec.makeMeasureSpec(freeSpace, MeasureSpec.AT_MOST)
        children.forEach { child ->
            // сначала измерим так, чтобы элемент занял минимальное пространство
            measureChild(child, widthSpec, heightSpec)
            flexibleWidth += child.measuredWidth
        }
        // Если элементы входят по ширине, тогда возвращаем измеренную ширину
        if (freeSpace > flexibleWidth) return flexibleWidth

        // Если элементы не входят, то распределяем ширину пропорционально между элементами
        children.forEach { child ->
            val weight = child.measuredWidth / flexibleWidth.toFloat()
            val childWidth = (freeSpace * weight).roundToInt()
            measuredContentWidth += childWidth
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), heightSpec)
        }
        return measuredContentWidth
    }

    private fun changeSelectedSegmentWithAnimation(
        oldLeftPos: Float,
        oldRightPos: Float,
        newLeftPos: Float,
        newRightPos: Float
    ) {
        val animatorStartX = createValueAnimator(oldLeftPos, newLeftPos) { animation ->
            leftSideSelector = animation.animatedValue as Float
            invalidate()
        }

        val animatorEndX = createValueAnimator(oldRightPos, newRightPos) { animation ->
            rightSideSelector = animation.animatedValue as Float
        }

        val set = AnimatorSet()
        set.addListener(onEnd = { controller.onChangedSelectedSegment() })
        set.playTogether(animatorStartX, animatorEndX)
        set.start()
    }

    private fun changeSelectedSegmentWithoutAnimation(newStartPos: Float, newEndPos: Float) {
        leftSideSelector = newStartPos
        rightSideSelector = newEndPos
        invalidate()
        controller.onChangedSelectedSegment()
    }

    private fun createValueAnimator(
        startValue: Float,
        endValue: Float,
        interpolatorAnimator: TimeInterpolator = LinearInterpolator(),
        durationAnimator: Long = 200,
        listenerAnimator: ValueAnimator.AnimatorUpdateListener
    ) = ValueAnimator.ofFloat(startValue, endValue).apply {
        interpolator = interpolatorAnimator
        duration = durationAnimator
        addUpdateListener(listenerAnimator)
    }
}