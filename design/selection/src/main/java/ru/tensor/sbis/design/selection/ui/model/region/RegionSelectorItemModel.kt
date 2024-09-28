package ru.tensor.sbis.design.selection.ui.model.region

import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Модель данных для отображения регионов в компоненте выбора
 *
 * @author ma.kolpakov
 */
interface RegionSelectorItemModel : SelectorItemModel {

    /**
     * Количество вложенных/логически связанных элементов
     */
    val counter: Int
}