package ru.tensor.sbis.design.selection.bl.vm.selection.multi.command

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * Функция для применения [SelectionCommand] на список выбранных элементов
 *
 * @author ma.kolpakov
 */
internal class SelectionCommandAccumulator<DATA : SelectorItem> : (List<DATA>, SelectionCommand<DATA>) -> List<DATA> {

    override fun invoke(selection: List<DATA>, command: SelectionCommand<DATA>): List<DATA> =
        command.invoke(selection)
}