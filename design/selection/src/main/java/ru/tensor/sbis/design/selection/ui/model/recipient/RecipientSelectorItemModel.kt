package ru.tensor.sbis.design.selection.ui.model.recipient

import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Модель данных для отображения получателя в компоненте выбора
 *
 * @author ma.kolpakov
 */
interface RecipientSelectorItemModel : SelectorItemModel {

    /**
     * ФИО или название группы
     */
    override val title: String

    /**
     * Название группы или ФИО куратора
     */
    override val subtitle: String?
}