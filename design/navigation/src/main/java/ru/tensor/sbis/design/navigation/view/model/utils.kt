/**
 * Набор утилитных функций.
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.navigation.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.toLiveData
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.navigation.view.view.util.ObserverStub
import timber.log.Timber

/**
 * Создать [LiveData] из [Observable].
 */
internal fun <T> createLiveData(observable: Observable<T>?): LiveData<T> = observable?.run {
    // Подготовка "безопасного" потока для LiveDataReactiveStreams.fromPublisher (см. документацию метода).
    val publisher = toFlowable(BackpressureStrategy.LATEST)
        // если случится ошибка, отловим её и перенаправим в лог
        .doOnError { error ->
            Timber.e(IllegalStateException("Navigation flow terminated by exception", error))
        }
        // вышестоящий поток прерван ошибкой, завершим работу без падения
        .onErrorResumeNext(Flowable.empty())
    publisher.toLiveData().apply {
        observeForever(ObserverStub)
    }
} ?: MutableLiveData()

/**
 * Создать лайвдату для определения отображения счётчика.
 *
 * @param counterData лайвдата счётчика.
 * @param countersVisibility лайвдата со значением видимости всех счётчиков.
 */
internal fun createCountersVisibilityLiveData(counterData: LiveData<Int>, countersVisibility: LiveData<Boolean>) =
    MediatorLiveData<String>().apply {
        val comparing = { _: Any ->
            val counterInt = if (countersVisibility.value == false) 0 else counterData.value ?: 0
            val counterStr = if (counterInt > 0) counterInt.toString() else StringUtils.EMPTY
            postValue(counterStr)
        }
        addSource(counterData, comparing)
        addSource(countersVisibility, comparing)
    }

/**
 * Создать лайвдату для определения отображения разделителя счётчиков.
 *
 * @param newCounter лайвдата счётчика новых сообщений.
 * @param totalCounter лайвдата счётчика общего числа сообщений.
 * @param countersVisibility лайвдата со значением видимости всех счётчиков.
 */
internal fun createCountersDividerVisibilityLiveData(
    newCounter: LiveData<Int>,
    totalCounter: LiveData<Int>,
    countersVisibility: LiveData<Boolean>
) =
    MediatorLiveData<Boolean>().apply {
        val counterObserver = Observer<Int> {
            postValue(countersVisibility.value ?: true && isDividerVisible(newCounter, totalCounter))
        }

        val countersVisibilityObserver = Observer<Boolean> {
            if (countersVisibility.value == true) {
                postValue(isDividerVisible(newCounter, totalCounter))
            } else {
                postValue(false)
            }
        }
        addSource(newCounter, counterObserver)
        addSource(totalCounter, counterObserver)
        addSource(countersVisibility, countersVisibilityObserver)
    }

/**
 * Создать лайвдату для определения отображения разделителя счётчиков.
 *
 * @param newCounter лайвдата счётчика новых сообщений.
 * @param totalCounter лайвдата счётчика общего числа сообщений.
 */
internal fun createCountersDividerVisibilityLiveData(newCounter: LiveData<Int>, totalCounter: LiveData<Int>) =
    MediatorLiveData<Boolean>().apply {
        val observer = Observer<Int> {
            postValue(isDividerVisible(newCounter, totalCounter))
        }
        addSource(newCounter, observer)
        addSource(totalCounter, observer)
    }

/**
 * Получить видимость разделителя счётчика на основе значений [newCounter] и [totalCounter].
 */
internal fun isDividerVisible(newCounter: LiveData<Int>, totalCounter: LiveData<Int>): Boolean {
    val new = newCounter.value ?: 0
    val total = totalCounter.value ?: 0
    // both values present => divider must be visible
    return new > 0 && total > 0
}

/**
 * @see compositeCounterValue
 */
internal fun createCompositeCounterObservable(newCounter: Observable<Int>, totalCounter: Observable<Int>) =
    Observable.combineLatest(newCounter, totalCounter) { new, total ->
        compositeCounterValue(new, total)
    }

/**
 * Формирует [NavigationCompositeCounterData], где [NavigationCompositeCounterData.isSecondary] определяется в
 * зависимости от [newCounter].
 */
internal fun compositeCounterValue(newCounter: Int, totalCounter: Int): NavigationCompositeCounterData {
    return when {
        newCounter > 0 -> NavigationCompositeCounterData(newCounter, false)
        totalCounter > 0 -> NavigationCompositeCounterData(totalCounter, true)
        else -> NavigationCompositeCounterData(0, false)
    }
}