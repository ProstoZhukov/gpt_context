package ru.tensor.sbis.communicator.core.views

import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

/** @SelfDocumented */
fun RecyclerView?.safeUpdate(updateTask: () -> Unit) {
    this ?: return
    // Избегаем IllegalStateException если обновление происходит в момент перелайаута списка или скролла,
    // см. документацию к методу isComputingLayout.
    if (!isComputingLayout) {
        updateTask()
    } else {
        post {
            try {
                updateTask()
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
    }
}