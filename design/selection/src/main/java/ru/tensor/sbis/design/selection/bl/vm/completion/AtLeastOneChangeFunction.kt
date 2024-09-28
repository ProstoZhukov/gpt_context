package ru.tensor.sbis.design.selection.bl.vm.completion

import io.reactivex.functions.BiFunction
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * Функция проверки, которая разрешает выбор, если в списке есть по меньшей мере один элемент
 *
 * @author ma.kolpakov
 */
internal class AtLeastOneChangeFunction : BiFunction<List<SelectorItem>, List<SelectorItem>, Boolean> {

    override fun apply(selection: List<SelectorItem>, initialSelection: List<SelectorItem>): Boolean =
        selection.isNotEmpty()
}