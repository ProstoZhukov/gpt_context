package ru.tensor.sbis.design.pie_chart_view

import android.animation.Animator
import android.animation.ValueAnimator
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

/**
 * Класс аниматор изменения графика
 * Подготавливает данные для анимация изменений происходящих на графике и передает их [PieChartView]
 *
 * Пошаговая инструкция использования
 * 1. Создать дочерний класс от [PieChartDiffAnimator], если требуется не стандартный [PieData] переопределить [generatePieData]
 * 2. Создать аниматор с привязкой к [PieChartView]
 * 3. Вызвать [calculateDiff] для отрисовки "пирога"
 *
 * На примере модуля статистики календаря
 * ```
 * class CalendarStatisticsFragment: Fragment() {
 *
 *     private var pieChartDataDisposer = SerialDisposable()
 *     private val animator: PieChartDiffAnimator<CalendarStatisticsItem>? = null
 *
 *     override fun onResume() {
 *         super.onResume()
 *
 *         pieChartDataDisposer.set(viewModel.pieChartData.doOnSubscribe {
 *             animator = PieChartDiffAnimator<CalendarStatisticsItem>(binding.calendarStatisticsPieChart)
 *         }.doOnDispose {
 *             animator = null
 *         }.subscribe {
 *             animator.calculateDiff(it)
 *         })
 *     }
 *
 *     override fun onPause() {
 *         super.onPause()
 *         pieChartDataDisposer.set(null)
 *     }
 * }
 *
 * class StatisticDiffAnimator(pieChartView: PieChartView): PieChartDiffAnimator<CalendarStatisticsItem>(pieChartView) {
 *     override fun getColor(item: CalendarStatisticsItem): Int = item.iconBackgroundColor
 *
 *     override fun getValue(item: CalendarStatisticsItem): Float = item.percentValue.toFloat()
 *
 *     override fun generate(color: Int, value: Float): CalendarStatisticsItem = CalendarStatisticsItem(color, value.toDouble())
 * }
 * ```
 */
abstract class PieChartDiffAnimator<DATA>(pieChart: PieChartView) {

    companion object {
        private const val MAX_PHASE = 1f
        private const val MIN_PHASE = 0f
        private const val DURATION = 1000L
    }

    /**
     * Аниматор. Длительность [DURATION].
     */
    private val animator: Animator = ValueAnimator.ofFloat(MIN_PHASE, MAX_PHASE)
        .apply {
            duration = DURATION
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {
                phase = it.animatedValue as Float
            }
        }

    /**
     * Здесь хранится ключ - цвет, значение - пара старого и нового
     * По мере изменения [phase] вычисляется список с промежуточным значением между старым и новым
     */
    private val map = LinkedHashMap<Int, Pair<Float, Float>>()

    /**
     * Список отрисуемых данных в данным момент. Привязан к текущей [phase]
     */
    private var currentList: List<DATA> = emptyList()

    /**
     * Фаза состояния которой управляет [animator]
     * Используется для формирования промежуточных рисуемых данных
     * [MIN_PHASE] - начальное состояние перед изменением
     * [MAX_PHASE] - финальная фаза измнения
     */
    private var phase = MAX_PHASE
        set(value) {
            field = value
            currentList = listPhase()
            pieChart.data = generatePieData(currentList)
            pieChart.invalidate()
            pieChart.notifyDataSetChanged()
        }

    private val pieChart: PieChart = pieChart.chart

    /**
     * Подготовить данные к анимации перехода
     * [new] новый список данных к которым требуется перейти
     * [currentList] текущее состояние списка
     * Функция подготоваливает [map] перехода от [currentList] к [new]
     *
     * Логика работы
     * В [map] хранится ключи в порядке их появления в списке [new]
     * Первым проходом по ключам [map] формируются данные перехода
     * Вторым проходом по списку [new] добавляются новые ключи и данные перехода к [map]
     * Ключи которых нет в [currentList] и [new] не удаляются из [map], они там остаются со значением Pair(0, 0)
     */
    fun calculateDiff(new: List<DATA>) {
        animator.cancel()
        val old = currentList
        val newMap = new.map { getColor(it) to getValue(it) }.toMap()
        val oldMap = old.map { getColor(it) to getValue(it) }.toMap()
        // проход по имеющимся ключам в [map]
        map.keys.forEach {
            map[it] = Pair(oldMap[it] ?: 0f, newMap[it] ?: 0f)
        }
        // проход по новому списку и добавления в [map] новых ключей
        new.forEach {
            if (map[getColor(it)] == null) {
                map[getColor(it)] = Pair(0f, getValue(it))
            }
        }
        animator.start()
    }

    /**
     * Получить цвет куска пирога из [DATA]
     */
    @ColorInt
    protected abstract fun getColor(item: DATA): Int

    /**
     * Получить значение размера пирога из [DATA]
     */
    @FloatRange(from = 0.0, to = Double.MAX_VALUE)
    protected abstract fun getValue(item: DATA): Float

    /**
     * Формирование модели данных. Используется для формировани промежуточного списка данных [DATA]
     */
    protected abstract fun generate(
        @ColorInt color: Int,
        @FloatRange(from = 0.0, to = Double.MAX_VALUE) value: Float
    ): DATA

    /**
     * Генерация [PieData] по списку [list] для передачи [PieChart]
     * Требуется переопределить если шаблон [PieData] отличается
     */
    protected fun generatePieData(list: List<DATA>): PieData {
        val pieEntries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()

        list.forEach {
            pieEntries.add(PieEntry(getValue(it)))
            colors.add(getColor(it))
        }

        val dataSet = PieDataSet(pieEntries, "")
            .apply {
                sliceSpace = 1f
                setColors(colors)
                setDrawIcons(false)
                setDrawValues(false)
                selectionShift = 0f
            }

        return PieData(dataSet)
    }

    /**
     * Формирует список отрисовки для текущей фазы [phase]
     */
    private fun listPhase(): List<DATA> =
        map.map {
            generate(it.key, it.value.first + (it.value.second - it.value.first) * phase)
        }.filter { getValue(it) > 0f }

}