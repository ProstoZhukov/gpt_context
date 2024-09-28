package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants

import io.reactivex.Observable
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantListItemMapOfStringString
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListCommand

/**
 * Реализация команд списка участников диалога/чата.
 *
 * @author da.zhukov
 */
internal class ThemeParticipantsListCommand(
    themeParticipantsRepository: ThemeParticipantsRepository,
    mapper: BaseModelMapper<ListResultOfThemeParticipantListItemMapOfStringString, PagedListResult<ThemeParticipantListItem>>,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
) : BaseListCommand<
        ThemeParticipantListItem,
        ListResultOfThemeParticipantListItemMapOfStringString,
        ThemeParticipantsFilter,
        DataRefreshedThemeParticipantsControllerCallback>(
    themeParticipantsRepository,
    mapper
) {

    override fun list(filter: ThemeParticipantsFilter): Observable<PagedListResult<ThemeParticipantListItem>> {
        return super.list(filter).doOnNext {
            activityStatusSubscriptionsInitializer.initialize(
                it.dataList.filterIsInstance<ThemeParticipantListItem.ThemeParticipant>()
                    .map { participant -> participant.employeeProfile.uuid }
            )
        }
    }

    override fun refresh(filter: ThemeParticipantsFilter): Observable<PagedListResult<ThemeParticipantListItem>> {
        return super.refresh(filter).doOnNext {
            activityStatusSubscriptionsInitializer.initialize(
                it.dataList.filterIsInstance<ThemeParticipantListItem.ThemeParticipant>()
                    .map { participant -> participant.employeeProfile.uuid }
            )
        }
    }
}
