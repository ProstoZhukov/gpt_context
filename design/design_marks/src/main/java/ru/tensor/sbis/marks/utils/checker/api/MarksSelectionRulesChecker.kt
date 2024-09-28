package ru.tensor.sbis.marks.utils.checker.api

import android.view.View
import ru.tensor.sbis.marks.model.item.SbisMarksElement

/**
 * Интерфейс для реализации проверки выделения у view элементов пометок
 *
 * @author ra.geraskin
 */

interface MarksSelectionRulesChecker {

    /**
     * Проверка соблюдения правила выделения элементов.
     *
     * @param lastSelectedItem модель view-элемента пометки, чекбокс которой был изменён последней.
     * @param children [Sequence] последовательность view, для которых реализуется проверка выбора
     *
     */
    fun checkSelectionRules(lastSelectedItem: SbisMarksElement, children: Sequence<View>)
}