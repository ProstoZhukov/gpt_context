package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.mapper

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Хелпер для обработки кликов.
 *
 * @author da.zhukov.
 */
internal class CRMConnectionItemClickHelper(
    val scope: LifecycleCoroutineScope
) {
    val onItemCheckboxFlow: MutableSharedFlow<Pair<UUID, String>> = MutableSharedFlow()

    val onItemCheckboxClick: (Pair<UUID, String>) -> Unit = { idAndLabel: Pair<UUID, String> ->
        scope.launch {
            onItemCheckboxFlow.emit(idAndLabel)
        }
    }
}