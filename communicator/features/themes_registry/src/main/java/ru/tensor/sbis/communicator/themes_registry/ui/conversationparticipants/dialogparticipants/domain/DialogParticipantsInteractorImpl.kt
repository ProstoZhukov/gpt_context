package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.domain

import io.reactivex.Single
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.ParticipantRole
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.contract.DialogParticipantsInteractor
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.asListItemNative
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import java.util.*

/** @SelfDocumented */
internal class DialogParticipantsInteractorImpl(
    override val themeParticipantsCommandWrapper: ThemeParticipantsCommandWrapper,
    private val dialogController: DependencyProvider<DialogController>,
    private val employeeProfileControllerWrapperProvider: DependencyProvider<EmployeeProfileControllerWrapper>,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
) : BaseInteractor(), DialogParticipantsInteractor {

    override fun getRelevantMessageReceivers(conversationUuid: UUID): Single<List<ThemeParticipantListItem.ThemeParticipant>> =
        Single.fromCallable {
            dialogController.get().getRelevantMessageReceiversProfiles(conversationUuid)
        }
            .doOnSuccess {
                activityStatusSubscriptionsInitializer.initialize(
                    it.map { profile -> profile.person.uuid }
                )
            }
            .map { result -> result.map { it.asListItemNative } }
            .compose(getSingleBackgroundSchedulers())

    override fun getThemeParticipantList(participantsUuids: List<UUID>): Single<List<ThemeParticipantListItem.ThemeParticipant>> =
        Single.fromCallable {
            employeeProfileControllerWrapperProvider.get().getEmployeeProfilesFromCache(participantsUuids)
        }
            .doOnSuccess {
                activityStatusSubscriptionsInitializer.initialize(
                    it.map { profile -> profile.uuid }
                )
            }
            .map { result -> result.map { ThemeParticipantListItem.ThemeParticipant(it, ParticipantRole.MEMBER, null) } }
            .compose(getSingleBackgroundSchedulers())
}