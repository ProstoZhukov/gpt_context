package ru.tensor.sbis.mvi_extension.router.buffer

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Created by Aleksey Boldinov on 31.08.2022.
 */
sealed class BufferStatePolicy {

    object Lifecycle : BufferStatePolicy()

    class ViewModel(val stateKey: String, val store: ViewModelStore) : BufferStatePolicy() {

        constructor(stateKey: String, owner: ViewModelStoreOwner) : this(
            stateKey,
            owner.viewModelStore
        )
    }
}