package ru.tensor.sbis.design_selection.ui.main.vm

import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.ui.main.vm.contract.SelectionContentDelegate
import ru.tensor.sbis.design_selection.ui.main.vm.contract.SelectionHostViewModel

/**
 * Вью-модель компонента выбора.
 *
 * @see SelectionHostViewModel
 * @see SelectionContentDelegate
 *
 * @author vv.chekurda
 */
internal interface SelectionViewModel<ITEM : SelectionItem> :
    SelectionHostViewModel<ITEM>,
    SelectionContentDelegate<ITEM>