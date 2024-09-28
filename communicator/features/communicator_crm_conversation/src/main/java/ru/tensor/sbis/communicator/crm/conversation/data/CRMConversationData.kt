package ru.tensor.sbis.communicator.crm.conversation.data

import ru.tensor.sbis.communicator.base.conversation.data.BaseConversationData
import ru.tensor.sbis.communicator.base.conversation.data.model.ConversationAccess
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData
import ru.tensor.sbis.consultations.generated.ConsultationActions
import ru.tensor.sbis.consultations.generated.ConsultationChannel
import ru.tensor.sbis.consultations.generated.CreateConsultationButtonPosition
import ru.tensor.sbis.consultations.generated.SourceViewModel
import java.util.UUID

/**
 * Модель чата CRM.
 *
 * @property toolbarData                данные для отображения в тулбаре.
 * @property conversationAccess         модель разрешений и признаков доступности чата.
 * @property unreadCount                количество непрочитанных сообщений.
 * @property isCompletedChat            true, если чат завершенный.
 * @property unreadChatsMessagesCounter количество непрочитанных сообщений по заданному автору или каналу (отображается в шапке чата).
 * @property createConsultationButton   где отображать кнопку начала новой консультации.
 * @property sourceId                   идентификатор связанного источника консультаций, для возможности создавать консультации.
 * @property conversationUUID           идентификатор консультации, совпадает с идентификатором чата.
 * @property allowedActions             доступные действия с чатом через меню в тулбаре.
 * @property nextConsultationUUID       uuid следующей консультации.
 * @property isHistory                  отображать ли кнопку истории консультаций по клиенту.
 * @property isNew                      новая ли консультация, для возможности разного поведения шторки с историей по консультируемому.
 * @property isHideMenu                 необходимо ли показывать менб в переписке(для консультаций со стороны оператора).
 * @property authorId                   идентификатор консультируемого(автора консультации или адресата для консультаций созданных оператором), используется при вызове метода создания новой консультации.
 * @property source                     связанный источник консультации.
 * @property isDraft                    является ли консультация драфтовой (используется для отображения большой или маленькой панели ввода).
 * @property operatorId                 uuid оператора.
 * @property channel                    связанный канал, используется в фильтре операторов при переназначении консультаций, для подсветки канала выбранной консультации в службе поддержки на планшетах.
 * @property isGroupConversation        является ли переписка групповой.
 *
 * @author da.zhukov
 */
internal data class CRMConversationData(
    override var toolbarData: ToolbarData? = null,
    override var conversationAccess: ConversationAccess = ConversationAccess(),
    var unreadCount: Int = 0,
    var isCompletedChat: Boolean = false,
    var unreadChatsMessagesCounter: Int = 0,
    var createConsultationButton: CreateConsultationButtonPosition? = null,
    var sourceId: UUID? = null,
    var conversationUUID: UUID? = null,
    var allowedActions: ConsultationActions? = null,
    var nextConsultationUUID: UUID? = null,
    var isHistory: Boolean = false,
    var isNew: Boolean = false,
    var isHideMenu: Boolean = false,
    var authorId: UUID? = null,
    var source: SourceViewModel? = null,
    var isDraft: Boolean = false,
    var operatorId: UUID? = null,
    var channel: ConsultationChannel? = null,
    var isGroupConversation: Boolean = false
) : BaseConversationData