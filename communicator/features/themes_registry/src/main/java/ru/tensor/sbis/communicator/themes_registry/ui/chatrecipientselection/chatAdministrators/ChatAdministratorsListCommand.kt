package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators

import io.reactivex.Observable
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.ChatAdministratorsFilter
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantMapOfStringString
import ru.tensor.sbis.communicator.generated.DataRefreshedChatAdministratorsControllerCallback
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListCommand

/** @SelfDocumented */
internal class ChatAdministratorsListCommand(
    repository: ChatAdministratorsRepository,
    mapper: BaseModelMapper<ListResultOfThemeParticipantMapOfStringString, PagedListResult<ThemeParticipant>>,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
) : BaseListCommand<
        ThemeParticipant,
        ListResultOfThemeParticipantMapOfStringString,
        ChatAdministratorsFilter,
        DataRefreshedChatAdministratorsControllerCallback>(
    repository,
    mapper
) {
    override fun list(filter: ChatAdministratorsFilter): Observable<PagedListResult<ThemeParticipant>> {
        return super.list(filter).doOnNext {
            activityStatusSubscriptionsInitializer.initialize(
                it.dataList.map { participant ->
                    participant.employeeProfile.uuid
                }
            )
        }
    }

    override fun refresh(filter: ChatAdministratorsFilter): Observable<PagedListResult<ThemeParticipant>> {
        return super.refresh(filter).doOnNext {
            activityStatusSubscriptionsInitializer.initialize(
                it.dataList.map { participant ->
                    participant.employeeProfile.uuid
                }
            )
        }
    }
}