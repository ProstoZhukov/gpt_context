/**
 * Набор функций для определения поведения кнопки подтверждения выбора.
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.design_selection.domain.completion.button

import io.reactivex.functions.BiFunction
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData

/**
 * Функция проверки, которая разрешает выбор, если в списке есть по меньшей мере один элемент.
 */
internal class AlwaysVisibleBehavior :
    BiFunction<SelectedData<SelectionItem>, SelectedData<SelectionItem>, Boolean> {

    override fun apply(
        selection: SelectedData<SelectionItem>,
        initialSelection: SelectedData<SelectionItem>
    ): Boolean = true
}

/**
 * Функция, которая проверяет различие состава элементов в списке выбранных и начальном списке.
 */
internal class SelectionChangedBehavior :
    BiFunction<SelectedData<SelectionItem>, SelectedData<SelectionItem>, Boolean> {

    override fun apply(
        selection: SelectedData<SelectionItem>,
        initialSelection: SelectedData<SelectionItem>
    ): Boolean =
        selection.items.size != initialSelection.items.size ||
            (selection.items.isEmpty() && selection.hasSelectedItems) ||
            selection.items.any { selectedItem ->
                /*
                 Используется проверка по id, а не selection.containsAll(initialSelection) т.к. нет гарантии в
                 реализации equals() для пользовательского типа
                 */
                initialSelection.items.none { initialItem -> initialItem.id == selectedItem.id }
            }
}

/**
 * Функция проверки, которая разрешает выбор, если в списке есть по меньшей мере один элемент.
 */
internal class AtLeastOneBehavior :
    BiFunction<SelectedData<SelectionItem>, SelectedData<SelectionItem>, Boolean> {

    override fun apply(
        selection: SelectedData<SelectionItem>,
        initialSelection: SelectedData<SelectionItem>
    ): Boolean =
        selection.hasSelectedItems
}