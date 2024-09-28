package ru.tensor.sbis.design.selection.bl.vm.completion

import io.reactivex.Observable
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.ui.contract.SelectorDoneButtonVisibilityMode

/**
 * Реализации [DoneButtonViewModel] для режима работы [SelectorDoneButtonVisibilityMode.HIDDEN]
 *
 * @author ma.kolpakov
 */
internal class HiddenDoneButtonViewModel : DoneButtonViewModel {

    override val doneButtonVisible = Observable.just(false)
    override val doneButtonEnabled: Observable<Boolean> =
        // кнопка всегда скрыта, нет смысла присылать что-либо
        Observable.empty()

    override fun setInitialData(data: List<SelectorItem>) = Unit

    override fun setSelectedData(data: List<SelectorItem>) = Unit
}