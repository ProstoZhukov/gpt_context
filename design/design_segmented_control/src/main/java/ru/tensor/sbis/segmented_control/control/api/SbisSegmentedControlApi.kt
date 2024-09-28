package ru.tensor.sbis.segmented_control.control.api

import ru.tensor.sbis.segmented_control.SbisSegmentedControl
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlDistribution
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlSize
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlStyle
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlCounter
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlItemModel
import ru.tensor.sbis.segmented_control.utils.SegmentChangedListener

/**
 * Api компонента [SbisSegmentedControl].
 *
 * @author ps.smirnyh
 */
interface SbisSegmentedControlApi {

    /**
     * Стиль сегмент-контрола.
     */
    var style: SbisSegmentedControlStyle

    /**
     * Включен режим контрастности.
     */
    var contrast: Boolean

    /**
     * Режим компоновки сегментов в сегмент-контроле.
     */
    var distribution: SbisSegmentedControlDistribution

    /**
     * Размер компонента.
     */
    var size: SbisSegmentedControlSize

    /**
     * Наличие тени.
     * Поддерживается только при [contrast] true.
     */
    var hasShadow: Boolean

    /**
     * Индекс выбранного сегмента.
     */
    val selectedSegmentIndex: Int

    /**
     * Слушатель изменения активного сегмента.
     */
    var onSelectedSegmentChangedListener: SegmentChangedListener?

    /**
     * Установка сегментов в контрол.
     */
    fun setSegments(segments: List<SbisSegmentedControlItemModel>)

    /**
     * Установка активного сегмента с возможностью анимации изменения позиции.
     *
     * @throws IndexOutOfBoundsException при передаче несуществующего индекса
     */
    fun setSelectedSegmentIndex(segmentIndex: Int, animated: Boolean)

    /**
     * Обновление счетчика отдельного сегмента по индексу.
     *
     * @throws IndexOutOfBoundsException при передаче несуществующего индекса
     */
    fun updateCounter(segmentIndex: Int, counterModel: SbisSegmentedControlCounter?)
}