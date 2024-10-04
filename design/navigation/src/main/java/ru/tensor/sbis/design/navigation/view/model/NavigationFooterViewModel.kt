package ru.tensor.sbis.design.navigation.view.model

import androidx.lifecycle.LiveData
import io.reactivex.subjects.BehaviorSubject

/**
 * Реализация вьюмодели для подвала аккордеона.
 *
 * @author ra.geraskin
 */

internal class NavigationFooterViewModel {

    private val newCounterRaw = BehaviorSubject.create<Int>()

    private val totalCounterRaw = BehaviorSubject.create<Int>()

    val newCounter = createLiveData(newCounterRaw.map(DEFAULT_FORMAT::apply))

    val totalCounter = createLiveData(totalCounterRaw.map(DEFAULT_FORMAT::apply))

    val countersDividerVisible: LiveData<Boolean> =
        createCountersDividerVisibilityLiveData(createLiveData(newCounterRaw), createLiveData(totalCounterRaw))

    fun updateCounters(counters: NavigationCounters) {
        newCounterRaw.onNext(counters.unviewedCounter)
        totalCounterRaw.onNext(counters.totalCounter)
    }
}