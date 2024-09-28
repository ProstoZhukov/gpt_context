package ru.tensor.sbis.marks.item.api

import ru.tensor.sbis.marks.model.SbisMarksCheckboxStatus
import ru.tensor.sbis.marks.model.item.SbisMarksElement

/**
 * Api view-компонента одного элемента списка пометок
 *
 * @author ra.geraskin
 */

interface SbisMarksElementViewApi {

    /**
     * Модель пометки
     */
    val item: SbisMarksElement

    /**
     * Флаг стилизации текста
     */
    val isDefaultTitleStyle: Boolean

    /**
     * Флаг отображения чекбоксов
     */
    val isCheckboxVisible: Boolean

    /**
     * Слушатель изменения состояния чекбокса
     */
    var selectionChangeListener: ((SbisMarksElement) -> Unit)?

    /**
     * Слушатель клика по элементу
     */
    var elementClickListener: ((SbisMarksElement) -> Unit)?

    /**
     * Очистка чекбокса от выделения
     */
    fun clearSelection()

    /**
     * Получение значения чекбокса
     */
    fun getSelectionStatus(): SbisMarksCheckboxStatus

}