package ru.tensor.sbis.swipeablelayout.util.swipestate

import kotlinx.coroutines.Job
import ru.tensor.sbis.swipeablelayout.DismissListener
import ru.tensor.sbis.swipeablelayout.api.Dismissed
import ru.tensor.sbis.swipeablelayout.api.SwipeEventListener
import timber.log.Timber

/**
 * Модель для хранения данных для выполнения отложенного оповещения об удалении по смахиванию, либо его отмены.
 *
 * @author us.bessonov
 */
internal class PendingDismissal(
    private val timeoutJob: Job,
    var eventListeners: Map<String, SwipeEventListener>,
    var dismissListener: DismissListener?
) {
    /** @SelfDocumented */
    var isInDismissalProcess: Boolean = false

    /** @SelfDocumented */
    fun cancel() = timeoutJob.cancel()

    /** @SelfDocumented */
    fun notifyDismissed(id: SwipeItemId) {
        val uuid = (id as? Uuid)?.uuid
        try {
            eventListeners.forEach { it.value.invoke(Dismissed(uuid)) }
            dismissListener?.onDismissed(uuid)
        } catch (e: Exception) {
            Timber.e(
                e,
                "Error while deleting swiped item. Make sure that no Context references are captured in swipe event listener"
            )
        }
    }
}