package ru.tensor.sbis.design.message_panel.vm.recipients

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import java.util.*

/**
 * Внутренний API для управления адресатами панели ввода
 *
 * @author ma.kolpakov
 */
internal interface RecipientsDelegate : MessagePanelRecipientsApi {

    val recipientsUuid: StateFlow<List<UUID>>

    fun attachRecipientsScope(scope: CoroutineScope)

    fun clearRecipients()
}
