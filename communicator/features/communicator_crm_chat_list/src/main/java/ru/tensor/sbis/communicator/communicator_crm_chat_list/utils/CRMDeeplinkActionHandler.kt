package ru.tensor.sbis.communicator.communicator_crm_chat_list.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import ru.tensor.sbis.communicator.declaration.crm.model.CRMOpenableFilterType
import ru.tensor.sbis.deeplink.DeeplinkAction

/**
 * Вспомогательный интерфейс для обработки [DeeplinkAction] реестра чатов тех. поддержки.
 * Выступает шиной между контроллером и фрагментом.
 *
 * @author da.zhukov
 */
internal interface CRMDeeplinkActionHandler {

    val deeplinkActionFlow: MutableSharedFlow<DeeplinkAction>
}