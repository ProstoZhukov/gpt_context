package ru.tensor.sbis.message_panel.helper

import io.reactivex.functions.Function
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientDepartmentItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientPersonItem
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import java.util.*

/**
 * Проверяет список участников для определения необходимости отображения надписи "Всем участникам". В таком случае
 * функция вернёт пустой список, а не исходный
 *
 * @author us.bessonov
 */
internal class ConversationRecipientsChecker(private val interactor: MessagePanelRecipientsInteractor) :
    Function<Pair<UUID?, List<RecipientItem>>, List<RecipientItem>> {

    override fun apply(conversationAndRecipients: Pair<UUID?, List<RecipientItem>>): List<RecipientItem> {
        val (conversationUuid, recipients) = conversationAndRecipients
        conversationUuid ?: return recipients
        val isAllSelected = interactor.checkAllMembersSelected(conversationUuid, recipients.toUuidList().asArrayList())
        return if (isAllSelected) {
            // Для сообщений все участники отображаются при 0 количестве получателей
            emptyList()
        } else {
            recipients
        }
    }

    private fun List<RecipientItem>.toUuidList(): List<UUID> {
        val recipientsFromDepartments = filterIsInstance<RecipientDepartmentItem>().map { department ->
            department.personModels.map { it.uuid }
        }.flatten()
        val recipients = filterIsInstance<RecipientPersonItem>().map { it.uuid }
        return recipientsFromDepartments + recipients
    }
}
