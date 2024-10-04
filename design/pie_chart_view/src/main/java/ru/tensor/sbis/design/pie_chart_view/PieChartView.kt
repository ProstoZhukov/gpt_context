package ru.tensor.sbis.design.pie_chart_view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import ru.tensor.sbis.design.pie_chart_view.databinding.PieChartViewBinding

/**
 * PieChart для статистики покупок
 */
class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private val binding: PieChartViewBinding
    private val chartAnimDuration = 500

    internal var chart: PieChart

    private var data: PieData? = null
    private var animatorX: ObjectAnimator? = null
    private var animatorY: ObjectAnimator? = null

    /**
     * Целая часть суммы
     */
    var centerSum: TextView

    /**
     * Дробная часть суммы
     */
    var centerSumFraction: TextView

    @ColorInt
    private val holeColor: Int
    private val holeRadiusPercent: Float

    init {
        binding = PieChartViewBinding.inflate(LayoutInflater.from(context), this, true)

        chart = binding.chart
        centerSum = binding.pieChartCenterContent.pieChartCenterSum
        centerSumFraction = binding.pieChartCenterContent.pieCharCenterSumFraction

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.PieChartView, 0, 0)
        try {
            val centerTextVisibility = typedArray.getBoolean(R.styleable.PieChartView_centerTextViewVisibility, true)
            holeColor = typedArray.getColor(R.styleable.PieChartView_holeColor, Color.WHITE)
            holeRadiusPercent = typedArray.getFloat(R.styleable.PieChartView_holeRadiusPercent, 70f)
            centerSum.isVisible = centerTextVisibility
            centerSumFraction.isVisible = centerTextVisibility
        } finally {
            typedArray.recycle()
        }

        initChart()
    }

    private fun initChart() {
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false

        chart.legend.isEnabled = false

        chart.isDrawHoleEnabled = true
        chart.setHoleColor(holeColor)

        chart.holeRadius = holeRadiusPercent

        chart.setDrawCenterText(false)
        chart.setDrawEntryLabels(false)
        chart.setDrawMarkers(false)
        chart.setTouchEnabled(false)
        chart.setNoDataText(null)
    }

    /**@SelfDocumented */
    fun setChartData(data: PieData, useAnim: Boolean) {
        chart.data = data
        chart.highlightValues(null)
        if (useAnim) chart.animateXY(chartAnimDuration, chartAnimDuration, Easing.EaseInOutQuad) else chart.invalidate()
    }

    /**@SelfDocumented */
    fun setChartDataAnimateFilling(data: PieData?) {
        this.data = data
        chart.highlightValues(null)
        // Запуск анимации сворачивания
        if (chart.data != null) {
            startAnimation(0f)
        } else {
            // Если данных не было, то развернуть
            chart.data = data
            chart.animator.phaseX = 0f
            chart.animator.phaseY = 0f
            startAnimation(1f)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode != MeasureSpec.EXACTLY) {
            binding.root.layoutParams.height = resources.getDimensionPixelOffset(R.dimen.pie_chart_height)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onAnimationStart(animation: Animator) = Unit

    override fun onAnimationEnd(animation: Animator) {
        if (!isFillInAnimation()) {
            chart.data = data
            // Запуск анимации разворачивания
            startAnimation(1f)
        }
    }

    override fun onAnimationCancel(animation: Animator) = Unit

    override fun onAnimationRepeat(animation: Animator) = Unit

    override fun onAnimationUpdate(animation: ValueAnimator) {
        chart.postInvalidateOnAnimation()
    }

    private fun isFillInAnimation() = chart.data == data && chart.animator.phaseX == 1f && chart.animator.phaseY == 1f

    @Synchronized
    private fun startAnimation(toPhase: Float) {
        cancelAnimation()
        animatorX = ObjectAnimator.ofFloat(chart.animator, "phaseX", chart.animator.phaseX, toPhase).apply {
            interpolator = Easing.EaseInOutQuad
            duration = chartAnimDuration.toLong()
        }

        animatorY = ObjectAnimator.ofFloat(chart.animator, "phaseY", chart.animator.phaseY, toPhase).apply {
            interpolator = Easing.EaseInOutQuad
            duration = chartAnimDuration.toLong()
            addListener(this@PieChartView)
            addUpdateListener(this@PieChartView)
        }

        animatorX?.start()
        animatorY?.start()
    }

    private fun cancelAnimation() {
        animatorX?.cancel()
        animatorY?.cancel()
        animatorX = null
        animatorY = null
    }
}