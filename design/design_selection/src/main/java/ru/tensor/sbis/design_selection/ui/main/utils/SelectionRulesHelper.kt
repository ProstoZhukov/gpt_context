package ru.tensor.sbis.design_selection.ui.main.utils

import ru.tensor.sbis.communication_decl.selection.SelectionConfig

/**
 * Вспомогательная реализация для определения правил компонента выбора.
 * Определение необходимо для изменения поведения компонента специально для этих сценариев.
 *
 * @author vv.chekurda
 */
internal class SelectionRulesHelper(
    val config: SelectionConfig,
    val autoHideKeyboard: Boolean
) {

    /**
     * Мод механики завершения.
     *
     * @see SelectionConfig.isFinalComplete
     */
    val isFinalComplete: Boolean
        get() = config.isFinalComplete
}