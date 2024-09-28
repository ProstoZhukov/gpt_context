package ru.tensor.sbis.crud4.data

/**
 * Предназначен для создания observer'а коллекции и предоставления возможности обнуления ссылки на callback.
 *
 * @author us.bessonov
 */
interface HierarchyObserverWrapper<OBSERVER, OBSERVER_CALLBACK> {

    /** @SelfDocumented */
    fun createObserver(callback: OBSERVER_CALLBACK) : OBSERVER

    /** @SelfDocumented */
    fun asDisposable(observer: OBSERVER): DisposableObserver

}

/**
 * Сбрасывает состояние observer'а коллекции (предполагается обнуление ссылки на callback).
 *
 * @author us.bessonov
 */
interface DisposableObserver {
    /** @SelfDocumented */
    fun dispose()
}
