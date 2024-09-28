package ru.tensor.sbis.appdesign.navigation

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * Класс для увеличения значений счетчиков в навигации через промежуток времени.
 *
 * @author va.shumilov
 */
class IntervalNavigationCounter(
    count: Int, totalCount: Int = count
) : NumberNavigationCounter(count, totalCount) {

    init {
        Observable.interval(1, TimeUnit.SECONDS)
            .map { ++this.count }
            .subscribe(newCounterSubject)

        Observable.interval(1, TimeUnit.SECONDS)
            .map { ++this.totalCount }
            .subscribe(totalCounterSubject)
    }
}