package ru.tensor.sbis.design.selection.ui.model

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.region.RegionSelectorItemModel

/**
 * Базовая модель с минимальным набором данных для отображения в компоненте выбора
 *
 * @see RegionSelectorItemModel
 * @see PersonSelectorItemModel
 * @see DepartmentSelectorItemModel
 *
 * @author ma.kolpakov
 */
interface SelectorItemModel : SelectorItem {

    /**
     * Основной заголовок
     */
    val title: String

    /**
     * Подзаголовок. Вспомогательная информация
     */
    val subtitle: String?
}