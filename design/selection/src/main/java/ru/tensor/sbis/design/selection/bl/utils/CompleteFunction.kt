package ru.tensor.sbis.design.selection.bl.utils

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.bl.vm.selection.ApplySelection
import ru.tensor.sbis.design.selection.bl.vm.selection.CancelSelection
import ru.tensor.sbis.design.selection.bl.vm.selection.CompleteEvent
import ru.tensor.sbis.design.selection.bl.vm.selection.SetSelection

/**
 * Функция публикует rx-поток с результатом в зависимости от [CompleteEvent]
 *
 * @author ma.kolpakov
 */
internal class CompleteFunction<DATA : SelectorItem> :
    BiFunction<CompleteEvent<DATA>, List<DATA>, Observable<List<DATA>>> {

    override fun apply(event: CompleteEvent<DATA>, selection: List<DATA>): Observable<List<DATA>> = when (event) {
        CancelSelection -> Observable.empty()
        ApplySelection -> Observable.just(selection)
        is SetSelection<DATA> -> Observable.just(listOf(event.data))
    }
}