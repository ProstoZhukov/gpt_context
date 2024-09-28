package ru.tensor.sbis.marks.utils.checker

import android.view.View
import ru.tensor.sbis.marks.item.SbisMarksElementView
import ru.tensor.sbis.marks.model.item.SbisMarksColorElement
import ru.tensor.sbis.marks.model.item.SbisMarksElement
import ru.tensor.sbis.marks.utils.checker.api.MarksSelectionRulesChecker

internal class ColorSingleSelectionChecker : MarksSelectionRulesChecker {

    /**
     * Проверка соблюдения правила "Можно выбрать только одну пометку цветом из всех предоставленных".
     * Под капотом происходит очистка чекбоксов всех пометок цветом, кроме [lastSelectedItem].
     *
     * @param lastSelectedItem модель view-элемента пометки, чекбокс которой был изменён последней.
     * @param children [Sequence] последовательность view, для которых реализуется проверка выбора
     *
     */
    override fun checkSelectionRules(lastSelectedItem: SbisMarksElement, children: Sequence<View>) {
        if (lastSelectedItem is SbisMarksColorElement) {
            children
                .filterIsInstance(SbisMarksElementView::class.java)
                .filter { (it.item is SbisMarksColorElement) && (it.item.id != lastSelectedItem.id) }
                .forEach { it.clearSelection() }
        }
    }

}
