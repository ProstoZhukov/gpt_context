package ru.tensor.sbis.design_selection.domain.list

import ru.tensor.sbis.communication_decl.selection.SelectionMode

/**
 * Поставщик режима выбора [SelectionMode].
 *
 * @param initialMode режим выбора для инициализации.
 *
 * @author vv.chekurda
 */
internal class SelectionModeProvider(initialMode: SelectionMode) {

    /**
     * Получить/установить режим выбора.
     * @see SelectionMode
     */
    var selectionMode: SelectionMode = initialMode

    /**
     * Получить признак режима мультивыбора.
     * false - одиночный выбор.
     */
    val isMultiSelection: Boolean
        get() = selectionMode != SelectionMode.SINGLE
}