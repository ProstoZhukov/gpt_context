package ru.tensor.sbis.design.selection.ui.model.recipient

import androidx.annotation.IntRange

/**
 * Модель данных для отображения группы (соц. сети) получателей в компоненте выбора
 *
 * @see DepartmentSelectorItemModel
 *
 * @author ma.kolpakov
 */
interface GroupSelectorItemModel : RecipientSelectorItemModel {

    /**
     * Адрес аватарки группы
     */
    val imageUri: String

    /**
     * Количество участников группы
     */
    @get:IntRange(from = 0L)
    val membersCount: Int
}