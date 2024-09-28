package ru.tensor.sbis.design.selection.bl.vm.completion

import io.reactivex.Observable
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.ui.contract.SelectorDoneButtonVisibilityMode

/**
 * Реализации [DoneButtonViewModel] для режима работы [SelectorDoneButtonVisibilityMode.VISIBLE]
 *
 * @author ma.kolpakov
 */
internal class VisibleDoneButtonViewModel : DoneButtonViewModel {

    override val doneButtonVisible = Observable.just(true)
    override val doneButtonEnabled = Observable.just(true)

    override fun setInitialData(data: List<SelectorItem>) = Unit

    override fun setSelectedData(data: List<SelectorItem>) = Unit
}