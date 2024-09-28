package ru.tensor.sbis.appdesign.navigation

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.design.navigation.view.model.NavigationCounter

/**
 * Счетчик в навигации.
 *
 * @author va.shumilov
 */
open class NumberNavigationCounter(
    count: Int,
    totalCount: Int = count
) : NavigationCounter {

    protected var newCounterSubject: BehaviorSubject<Int> = BehaviorSubject.createDefault(count)
    protected var totalCounterSubject: BehaviorSubject<Int> = BehaviorSubject.createDefault(totalCount)

    override val newCounter: Observable<Int> = newCounterSubject

    override val totalCounter: Observable<Int> = totalCounterSubject

    var count: Int
        set(value) {
            newCounterSubject.onNext(value)
        }
        get() = newCounterSubject.value!!

    var totalCount: Int
        set(value) {
            totalCounterSubject.onNext(value)
        }
        get() = totalCounterSubject.value!!
}