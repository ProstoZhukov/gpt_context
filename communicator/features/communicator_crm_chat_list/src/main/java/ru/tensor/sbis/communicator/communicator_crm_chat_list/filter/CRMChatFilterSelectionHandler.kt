package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter

import kotlinx.coroutines.flow.MutableSharedFlow
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import java.util.UUID

/**
 * Шина данных.
 *
 * @author da.zhukov
 */

/**
 * Flow сохранения фильтра.
 */
val crmFilterFlow: MutableSharedFlow<Pair<CRMChatFilterModel, List<String>>> = MutableSharedFlow()