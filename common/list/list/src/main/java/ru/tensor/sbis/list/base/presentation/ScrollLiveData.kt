package ru.tensor.sbis.list.base.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Реализация [MutableLiveData] для событий прокрутки компонента списка. Событие доставляется только один раз т.к. после
 * поворота [RecyclerView] восстанавливает положение прокрутки
 *
 * @author ma.kolpakov
 */
internal class ScrollLiveData : MutableLiveData<Int>() {

    private val pendingEvent = AtomicBoolean(false)
    private var observerWrapper: ObserverWrapper? = null

    override fun observe(owner: LifecycleOwner, observer: Observer<in Int>) {
        if (hasObservers()) {
            Timber.w(IllegalStateException("Only one observer supported. Current observer will be replaced"))
            removeObserver(observerWrapper!!)
        }
        observerWrapper = ObserverWrapper(observer, pendingEvent).also {
            super.observe(owner, it)
        }
    }

    override fun removeObserver(observer: Observer<in Int>) {
        val wrapper = observerWrapper ?: return
        if (wrapper == observer || wrapper.observer == observer) {
            super.removeObserver(wrapper)
            observerWrapper = null
        }
    }

    override fun setValue(value: Int?) {
        observerWrapper?.reset()
        super.setValue(value)
    }

    private inner class ObserverWrapper(
        val observer: Observer<in Int>,
        private val pendingEvent: AtomicBoolean
    ) : Observer<Int> {

        override fun onChanged(t: Int) {
            if (pendingEvent.compareAndSet(false, true)) {
                observer.onChanged(t)
            }
        }

        fun reset() {
            pendingEvent.set(false)
        }
    }
}