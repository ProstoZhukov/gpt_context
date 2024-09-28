package ru.tensor.sbis.design.selection.ui.di

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.list.ListItemMapper
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenVM
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.FixedButtonListener
import ru.tensor.sbis.design.selection.ui.utils.vm.SearchViewModel

/**
 * Интерфейс dagger компонента для списка в компоненте выбора
 *
 * @author ma.kolpakov
 */
internal interface SbisListComponent {

    val screenVm: SelectionListScreenVM
    val searchVm: SearchViewModel

    val listItemMapper: ListItemMapper
    val fixedButtonListener: FixedButtonListener<Any, FragmentActivity>?
}