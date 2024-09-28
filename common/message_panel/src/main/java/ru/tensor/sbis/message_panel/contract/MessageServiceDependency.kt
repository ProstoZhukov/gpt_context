package ru.tensor.sbis.message_panel.contract

import ru.tensor.sbis.message_panel.decl.DraftResultHelper
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.decl.MessageServiceWrapper

/**
 * Зависимости для взаимодействия с сервисом отправки (например, сообщений).
 * Реализация не должна захватывать activity, fragment, view и другие объекты, которые опасны утечками памяти.
 *
 * @author vv.chekurda
 */
interface MessageServiceDependency<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> {

    val serviceWrapper: MessageServiceWrapper<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>

    val messageResultHelper: MessageResultHelper<MESSAGE_RESULT, MESSAGE_SENT_RESULT>

    val draftResultHelper: DraftResultHelper<DRAFT_RESULT>
}