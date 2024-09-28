package ru.tensor.sbis.communication_decl.crm

import java.io.Serializable
import java.util.UUID

/**
 * Модель параметров для открытия реестра чатов CRM.
 * @property crmListMode режим отображения списка консультаций.
 * @property excludeId идентификатор консультации, которая должна быть исключена из результатов поиска.
 * @property userId идентификатор пользователя для которого получаем историю консультаций.
 *
 * @author da.zhukov
 */
sealed interface CRMChatListParams : Serializable {
    val crmListMode: CRMChatListMode
    val userId: UUID?
    val excludeId: UUID?
}

/**
 *  Стандартная модель параметров для открытия реестра чатов CRM.
 *  @property consultationUuid идентификатор консультации которую необходимо сразу открыть.
 */
class CRMChatListDefaultParams(
    val consultationUuid: UUID? = null
) : CRMChatListParams {
    override val crmListMode: CRMChatListMode = CRMChatListMode.OPERATORS_CONSULTATION
    override val userId: UUID? = null
    override val excludeId: UUID? = null
}

/**
 *  Модель параметров для открытия реестра чатов CRM в режиме истории.
 */
class CRMChatListHistoryParams(
    override val userId: UUID,
    override val excludeId: UUID
) : CRMChatListParams {
    override val crmListMode: CRMChatListMode = CRMChatListMode.USER_HISTORY
}

/**
 *  Модель параметров для открытия реестра чатов CRM со стороны клиента.
 *  @property clientId идентификатор клиента, по которому будет фильтроваться список консультаций.
 *  @property clientName название клиента, для отображения в шапке.
 */
class CRMChatListClientsParams(
    val clientId: UUID,
    val clientName: String?
) : CRMChatListParams {
    override val crmListMode: CRMChatListMode = CRMChatListMode.OPERATORS_CONSULTATION
    override val userId: UUID? = null
    override val excludeId: UUID? = null
}

/**
 * Режим отображения списка консультаций.
 */
enum class CRMChatListMode {

    /**
     * Для списка консультаций оператора.
     */
    OPERATORS_CONSULTATION,

    /**
     * Для истории консультаций заданного пользователя.
     */
    USER_HISTORY
}