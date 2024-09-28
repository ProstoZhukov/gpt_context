package ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts

import androidx.lifecycle.LifecycleObserver

/**
 * Базовый интерфейс наблюдателя жизненного цикла.
 *
 * @author vv.chekurda
 */
interface BaseLifecycleObserver : LifecycleObserver {

    /** @SelfDocumented */
    fun viewIsStarted()

    /** @SelfDocumented */
    fun viewIsStopped()

    /** @SelfDocumented */
    fun viewIsResumed()

    /** @SelfDocumented */
    fun viewIsPaused()
}