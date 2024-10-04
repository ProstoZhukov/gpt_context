package ru.tensor.sbis.design.navigation.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Реализация вьюмодели шапки аккордеона с логотипом приложения.
 *
 * @author us.bessonov
 */
internal class NavigationHeaderViewModelImpl(
    override val data: NavigationHeaderData,
) : NavigationHeaderViewModel {

    private val newCounterRaw = BehaviorSubject.create<Int>()

    private val totalCounterRaw = BehaviorSubject.create<Int>()

    override val isSelected = MutableLiveData(false)

    @Deprecated("https://online.sbis.ru/opendoc.html?guid=ec38d634-c12d-496b-8367-27214f232ac1&client=3")
    override val selectionFlow = MutableSharedFlow<Boolean>(replay = 1)

    override val countersVisibility = MutableLiveData(true)

    override val newCounter = createCountersVisibilityLiveData(createLiveData(newCounterRaw), countersVisibility)

    override val totalCounter = createCountersVisibilityLiveData(createLiveData(totalCounterRaw), countersVisibility)

    override val countersDividerVisible: LiveData<Boolean> = createCountersDividerVisibilityLiveData(
        createLiveData(newCounterRaw),
        createLiveData(totalCounterRaw),
        countersVisibility
    )

    override fun setSelected(selected: Boolean) {
        if (isSelected.value != selected) {
            isSelected.value = selected
            selectionFlow.tryEmit(selected)
        }
    }

    override fun onClicked() {
        isSelected.value = true
        selectionFlow.tryEmit(true)
    }

    override fun updateCounters(counters: NavigationCounters) {
        newCounterRaw.onNext(counters.unviewedCounter)
        totalCounterRaw.onNext(counters.totalCounter)
    }

    override fun updateCountersVisibility(enabled: Boolean) {
        countersVisibility.value = enabled
    }
}