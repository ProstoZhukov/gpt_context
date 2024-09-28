package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators

import io.reactivex.Observable
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.ChatAdministratorsFilter
import ru.tensor.sbis.communicator.generated.DataRefreshedChatAdministratorsControllerCallback
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantMapOfStringString
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsContactItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListCommand

/**
 * Реализация команд списка администраторов настроек канала.
 *
 * @author dv.baranov
 */
internal class ChatAdministratorsSettingsListCommand(
    repository: ChatAdministratorsRepository,
    mapper: BaseModelMapper<ListResultOfThemeParticipantMapOfStringString, PagedListResult<ChatSettingsItem>>,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
) : BaseListCommand<
        ChatSettingsItem,
        ListResultOfThemeParticipantMapOfStringString,
        ChatAdministratorsFilter,
        DataRefreshedChatAdministratorsControllerCallback>(
    repository,
    mapper
) {
    override fun list(filter: ChatAdministratorsFilter): Observable<PagedListResult<ChatSettingsItem>> {
        return super.list(filter).doOnNext {
            activityStatusSubscriptionsInitializer.initialize(
                it.dataList.filterIsInstance<ChatSettingsContactItem>().map { item ->
                    item.participant.employeeProfile.uuid
                }
            )
        }
    }

    override fun refresh(filter: ChatAdministratorsFilter): Observable<PagedListResult<ChatSettingsItem>> {
        return super.refresh(filter).doOnNext {
            activityStatusSubscriptionsInitializer.initialize(
                it.dataList.filterIsInstance<ChatSettingsContactItem>().map { item ->
                    item.participant.employeeProfile.uuid
                }
            )
        }
    }
}