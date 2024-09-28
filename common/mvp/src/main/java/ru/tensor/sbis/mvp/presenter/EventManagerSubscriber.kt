package ru.tensor.sbis.mvp.presenter

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * Интерфейс подписчика
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface EventManagerSubscriber<EventData> {

    /** @SelfDocumented */
    fun subscribe(
        key: String, relevantEntity: String,
        onEvent: Consumer<EventData>
    ): Disposable

}
