package ru.tensor.sbis.business.common.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import ru.tensor.sbis.common.rx.RxBus

/**
 * Подписка на событие [RxBus]
 *
 * @param E класс события для подписки
 *
 * @param action действие, выполняемое при получении события
 */
inline fun <reified E> RxBus.subscribe(noinline action: (E) -> Unit): Disposable =
    subscribe(E::class.java).subscribe(action)

/**
 * Подписка на событие [RxBus]
 *
 * @param E класс события для подписки
 *
 * @param compositeDisposable disposable контейнер для отписки
 * @param action действие, выполняемое при получении события
 */
inline fun <reified E> RxBus.subscribe(compositeDisposable: CompositeDisposable, noinline action: (E) -> Unit) {
    subscribe(E::class.java).subscribe(action).addTo(compositeDisposable)
}
