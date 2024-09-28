package ru.tensor.sbis.business.common.ui.base.adapter.data

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import io.reactivex.Completable
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit

/**
 * Заглушка для отображения пустого элемента или индикатора загрузки в конце списка задач
 */
class BottomStub: BaseObservable() {

    /** true, если нужно показать ромашку, иначе false */
    val progressVisibility = ObservableBoolean(false)

    /** true, если нужно принудительно показать ромашку, иначе false */
    var forceHideLoadMoreProgress = true
        set(value) {
            if (value) {
                progressVisibility.set(false)
            } else if (!timer.isDisposed) {
                progressVisibility.set(true)
            }
            field = value
        }

    private var timer = Disposables.disposed()

    /**
     * Отложенное скрытие ромашки, используется в адаптере списка задач
     */
    fun startTimer() {
        timer = Completable.timer(5, TimeUnit.SECONDS).subscribe { progressVisibility.set(false) }
    }

    /**
     * Остановка таймера
     */
    fun stopTimer() {
        timer.dispose()
    }
}