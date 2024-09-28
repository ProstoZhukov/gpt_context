/**
 * Вспомогательные инструменты для работы SelectionViewModelImpl
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.selection.bl.vm.selection

import io.reactivex.Observable
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * Применяет правила фильтрации и события остановки [other] на оригинальных поток
 */
internal fun <DATA : SelectorItem> Observable<List<DATA>>.filterSelectedItems(other: Observable<*>) =
    skipWhile(List<*>::isEmpty)
        .takeUntil(other)
