package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.helper

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Хелпер для обработки кликов.
 *
 * @author da.zhukov.
 */
internal class CRMChannelsItemClickHelper(
    val scope: LifecycleCoroutineScope
) {
    val onItemSuccessIconFlow: MutableSharedFlow<Triple<UUID, UUID?, String>> = MutableSharedFlow()

    val onItemCheckedFlow: MutableSharedFlow<Pair<UUID, String>> = MutableSharedFlow()

    val onItemSuccessIconClick: (UUID, UUID?, String) -> Unit = { id: UUID, parentId: UUID?, channelName: String ->
        scope.launch {
            onItemSuccessIconFlow.emit(Triple(id, parentId, channelName))
        }
    }

    val onItemCheckedClick: (UUID, String) -> Unit = { id: UUID, label: String ->
        scope.launch {
            onItemCheckedFlow.emit(id to label)
        }
    }
}