package ru.tensor.sbis.mvi_extension.router.buffer

import androidx.lifecycle.ViewModelStore
import ru.tensor.sbis.mvi_extension.wrapViewModel
import java.util.Deque
import java.util.LinkedList

/**
 * Created by Aleksey Boldinov on 31.08.2022.
 */
object BufferFactory {

    fun <T> create(policy: BufferStatePolicy): Buffer<T> {
        return when (policy) {
            is BufferStatePolicy.Lifecycle -> DefaultBuffer()
            is BufferStatePolicy.ViewModel -> ViewModelBuffer(policy.stateKey, policy.store)
        }
    }
}

private class DefaultBuffer<T> private constructor(
    buffer: LinkedList<T>
) : Deque<T> by buffer, Buffer<T> {

    constructor() : this(LinkedList())
}

private class ViewModelBuffer<T> private constructor(
    private val buffer: Buffer<T>
) : Buffer<T> by buffer {

    constructor(
        stateKey: String,
        store: ViewModelStore
    ) : this(store.wrapViewModel(stateKey) { DefaultBuffer<T>() })
}