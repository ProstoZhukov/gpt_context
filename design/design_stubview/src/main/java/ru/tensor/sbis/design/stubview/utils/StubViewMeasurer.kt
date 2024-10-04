package ru.tensor.sbis.design.stubview.utils

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.Px
import ru.tensor.sbis.design.stubview.layout_strategies.StubViewComposer

/**
 * Инструмент для измерения ширины и высоты заглушки
 *
 * @param layoutParams параметры для получения информации о размерах
 * @param minStubViewHeight минимальная высота заглушки в пикселях
 *
 * @author ma.kolpakov
 */
internal class StubViewMeasurer(
    private val layoutParams: ViewGroup.LayoutParams,
    @Px private val minStubViewHeight: Int,
) {

    private var widthMeasureSpec: Int = 0
    private var heightMeasureSpec: Int = 0
    private var paddingTop: Int = 0
    private var paddingBottom: Int = 0

    /**
     * Установка размеров в момент измерения. Вызывать в onMeasure()
     *
     * @param widthMeasureSpec спек ширины
     * @param heightMeasureSpec спек высоты
     * @param paddingTop верхний отступ в пикселях
     * @param paddingBottom нижний отступ в пикселях
     *
     */
    fun setSizes(widthMeasureSpec: Int, heightMeasureSpec: Int, @Px paddingTop: Int, @Px paddingBottom: Int) {
        this.widthMeasureSpec = widthMeasureSpec
        this.heightMeasureSpec = heightMeasureSpec
        this.paddingTop = paddingTop
        this.paddingBottom = paddingBottom
    }

    /**
     * Измерение ширины и высоты View заглушки
     *
     * Пояснение для [isMinHeight]:
     * * Если высота не задана и [isMinHeight] == true, сжимаем высоту до минимальной (отображаем только тексты)
     * * Если высота не задана и [isMinHeight] == false, показываем весь контент и сжимаем размер до его высоты
     * * Иначе измеряем высоту через [View.MeasureSpec]
     *
     * @param composer стратегия позиционирования для измерения и корректировки размеров
     * @param isMinHeight нужно ли использовать минимальную высоту при не заданной высоте заглушки
     *
     * @return пара с width и height
     */
    fun measure(composer: StubViewComposer?, isMinHeight: Boolean): Pair<Int, Int> {
        val containerWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val isNotMeasurable = heightMeasureSpec == View.MeasureSpec.UNSPECIFIED || layoutParams.height == WRAP_CONTENT

        var containerHeight: Int =
            if (isNotMeasurable && isMinHeight) {
                minStubViewHeight
            } else {
                View.MeasureSpec.getSize(heightMeasureSpec).coerceAtLeast(minStubViewHeight)
            }

        composer?.run {
            var containerHeightWithoutPaddings = containerHeight - paddingTop - paddingBottom
            measure(containerWidth, containerHeightWithoutPaddings)

            val remeasure = !isMinHeight && isNotMeasurable
            if (remeasure) {
                containerHeightWithoutPaddings = maxHeight().coerceAtLeast(minStubViewHeight)
                containerHeight = containerHeightWithoutPaddings
                measure(containerWidth, containerHeightWithoutPaddings)
            }
        }

        return containerWidth to containerHeight
    }
}
