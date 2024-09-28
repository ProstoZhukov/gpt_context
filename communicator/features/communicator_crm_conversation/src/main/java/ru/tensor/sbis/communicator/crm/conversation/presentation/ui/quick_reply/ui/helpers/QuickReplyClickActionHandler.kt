package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.helpers

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.consultations.generated.QuickReplyViewModel
import java.util.UUID

/**
 * Обработчик нажатий на элемент быстрого ответа.
 *
 * @author dv.baranov
 */
internal class QuickReplyClickActionHandler(val scope: LifecycleCoroutineScope) {

    /**
     * Flow, в котором передатся uuid быстрого ответа и true, если его надо закрепить, false иначе.
     */
    val onSwipeMenuItemClick: MutableSharedFlow<Pair<UUID, Boolean>> = MutableSharedFlow()

    /**
     * Flow, в котором передается модель нажатого быстрого ответа.
     */
    val onItemClick: MutableSharedFlow<QuickReplyViewModel> = MutableSharedFlow()

    /** @SelfDocumented */
    fun pinQuickReply(uuid: UUID) {
        scope.launch { onSwipeMenuItemClick.emit(Pair(uuid, true)) }
    }

    /** @SelfDocumented */
    fun unpinQuickReply(uuid: UUID) {
        scope.launch { onSwipeMenuItemClick.emit(Pair(uuid, false)) }
    }

    /** @SelfDocumented */
    fun onItemClick(item: QuickReplyViewModel) {
        scope.launch { onItemClick.emit(item) }
    }
}
