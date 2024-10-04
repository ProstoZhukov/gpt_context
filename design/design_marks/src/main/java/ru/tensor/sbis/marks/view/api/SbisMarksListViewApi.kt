package ru.tensor.sbis.marks.view.api

import ru.tensor.sbis.marks.model.SbisMarksComponentType
import ru.tensor.sbis.marks.model.item.SbisMarksElement

/**
 * Api view-компонента списка пометок.
 *
 * @author ra.geraskin
 */
internal interface SbisMarksListViewApi {

    /**
     * Список моделей пометок.
     */
    var items: List<SbisMarksElement>

    /**
     * Значение типа компонента списка пометок.
     */
    var componentType: SbisMarksComponentType

    /**
     * Слушатель завершения выбора пометок. Возвращает список выбранных пользователем моделей пометок.
     */
    var selectionCompleteListener: ((selectedItems: List<SbisMarksElement>) -> Unit)?

    /**
     * Слушатель изменения выбора пометки. Возвращает модель пометки, checkBoxSelection которой был изменён.
     */
    var selectionChangeListener: ((selectedItem: SbisMarksElement) -> Unit)?

    /**
     * Очистить чекбоксы всех пометок. Удалить все selections.
     */
    fun clearAll()

    /**
     * Получить список выбранных моделей пометок.
     */
    fun getSelected(): List<SbisMarksElement>

}