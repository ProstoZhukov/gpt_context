package ru.tensor.sbis.list.base.presentation

import androidx.annotation.MainThread
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity
import ru.tensor.sbis.list.view.utils.ListData

/**
 * Извлекает список данных из бизнес модели в фоновом потоке.
 */
class BackgroundListDataExtractor {


    @Suppress("unused")
    private val disposable = SerialDisposable()

    /**
     * Получить данные из [entity] в фоновом потоке и отдать приемщику [receiver] данных в UI потоке.
     */
    fun extract(entity: ListScreenEntity, receiver: ListDataReceiver) {
        disposable.set(
            Single
                .just(entity)
                .observeOn(Schedulers.computation())
                .map {
                    it.toListData()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    receiver.receive(it)
                }
        )
    }

    /**
     * Получатель списочных данных.
     */
    interface ListDataReceiver {
        @MainThread
        fun receive(data: ListData)
    }
}