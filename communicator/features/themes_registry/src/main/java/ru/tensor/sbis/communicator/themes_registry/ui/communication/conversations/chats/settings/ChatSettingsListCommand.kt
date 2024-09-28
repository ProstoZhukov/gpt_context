package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import io.reactivex.Observable
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantListItemMapOfStringString
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsContactItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsRepository
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListCommand

/**
 * Реализация команды списка для экрана настроек чата.
 *
 * @author dv.baranov
 */
internal class ChatSettingsListCommand(
    themeParticipantsRepository: ThemeParticipantsRepository,
    mapper: BaseModelMapper<ListResultOfThemeParticipantListItemMapOfStringString, PagedListResult<ChatSettingsItem>>,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer,
) : BaseListCommand<
    ChatSettingsItem,
    ListResultOfThemeParticipantListItemMapOfStringString,
    ThemeParticipantsFilter,
    DataRefreshedThemeParticipantsControllerCallback,>(
    themeParticipantsRepository,
    mapper,
) {

    override fun list(filter: ThemeParticipantsFilter): Observable<PagedListResult<ChatSettingsItem>> {
        return super.list(filter).doOnNext {
            activityStatusSubscriptionsInitializer.initialize(
                it.dataList.filterIsInstance<ChatSettingsContactItem>().map { item ->
                    item.participant.employeeProfile.uuid
                },
            )
        }
    }

    override fun refresh(filter: ThemeParticipantsFilter): Observable<PagedListResult<ChatSettingsItem>> {
        return super.refresh(filter).doOnNext {
            activityStatusSubscriptionsInitializer.initialize(
                it.dataList.filterIsInstance<ChatSettingsContactItem>().map { item ->
                    item.participant.employeeProfile.uuid
                },
            )
        }
    }
}
