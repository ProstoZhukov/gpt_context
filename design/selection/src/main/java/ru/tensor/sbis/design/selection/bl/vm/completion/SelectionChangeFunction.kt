package ru.tensor.sbis.design.selection.bl.vm.completion

import io.reactivex.functions.BiFunction
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader

/**
 * Функция, которая проверяет различие _состава элементов_ (игнорирует порядок) в списке выбранных
 * [MultiSelectionViewModel.selection] и начальном списке [MultiSelectionLoader.loadSelectedItems]
 *
 * @author ma.kolpakov
 */
internal class SelectionChangeFunction : BiFunction<List<SelectorItem>, List<SelectorItem>, Boolean> {

    override fun apply(selection: List<SelectorItem>, initialSelection: List<SelectorItem>): Boolean {
        return selection.size != initialSelection.size || selection.any { selectedItem ->
            /*
             Используется проверка по id, а не selection.containsAll(initialSelection) т.к. нет гарантии в
             реализации equals() для пользовательского типа
             */
            initialSelection.none { initialItem -> initialItem.id == selectedItem.id }
        }
    }
}