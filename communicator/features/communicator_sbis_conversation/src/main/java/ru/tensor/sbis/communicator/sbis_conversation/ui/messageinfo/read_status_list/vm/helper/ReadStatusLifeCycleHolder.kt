package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.helper

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwnerCompat
import androidx.lifecycle.LifecycleRegistry

/**
 * Реализация holder жизненного цикла списка статусов прочитанности
 *
 * @author vv.chekurda
 */
internal class ReadStatusLifeCycleHolderImpl : ReadStatusLifeCycleHolder {

    override val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override fun onCleared() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }
}

/**
 * Holder жизненного цикла списка статусов прочитанности
 */
internal interface ReadStatusLifeCycleHolder :
    LifecycleOwnerCompat {

    /** @SelfDocumented */
    val lifecycleRegistry: LifecycleRegistry

    /** @SelfDocumented */
    override fun getLifecycleCompat(): Lifecycle = lifecycleRegistry

    /** @SelfDocumented */
    fun onCleared()
}