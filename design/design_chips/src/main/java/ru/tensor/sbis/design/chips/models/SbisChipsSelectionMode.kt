package ru.tensor.sbis.design.chips.models

/**
 * Режим выбора элементов.
 *
 * @author ps.smirnyh
 */
sealed interface SbisChipsSelectionMode {

    /** Единичный выбор. */
    object Single : SbisChipsSelectionMode

    /** Множественный выбор. */
    object Multiple : SbisChipsSelectionMode

    /**
     * Кастомный выбор.
     *
     * [selectionHandler] обработка нажатия на элемент пользователем.
     */
    class Custom(val selectionHandler: (Int) -> List<Int>) : SbisChipsSelectionMode
}
