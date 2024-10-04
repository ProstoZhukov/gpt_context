package ru.tensor.sbis.design.selection.ui.model.recipient

import androidx.annotation.IntRange

/**
 * Модель данных для отображения рабочей группы получателей в компоненте выбора
 *
 * @see GroupSelectorItemModel
 *
 * @author ma.kolpakov
 */
interface DepartmentSelectorItemModel : RecipientSelectorItemModel {

    /**
     * Количество сотрудников в рабочей группе
     */
    @get:IntRange(from = 0L)
    val membersCount: Int
}