package ru.tensor.sbis.design.files_picker.feature

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

suspend fun <T> MutableSharedFlow<T>.waitingEmit(value: T) =
    withContext(Dispatchers.Main) {
        suspend fun send(subCount: Int): Boolean {
            if (subCount > 0) {
                emit(value)
                return true
            }
            return false
        }

        if (send(subscriptionCount.value)) {
            cancel()
            return@withContext
        }
        subscriptionCount.collect {
            if (send(it)) {
                cancel()
                return@collect
            }
        }
    }