package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information

import io.reactivex.Single
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.ParticipantRole
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.asListItemNative
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import java.util.*

/**
 * Реализация интерактора экрана информации о диалоге.
 *
 * @author da.zhukov
 */
internal class DialogInformationInteractorImpl(
    override val themeParticipantsCommandWrapper: ThemeParticipantsCommandWrapper,
    private val dialogControllerProvider: DependencyProvider<DialogController>,
    private val employeeProfileControllerWrapperProvider: DependencyProvider<EmployeeProfileControllerWrapper>,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
) : BaseInteractor(), DialogInformationInteractor {

    /** @SelfDocumented */
    override fun getRelevantMessageReceivers(conversationUuid: UUID): Single<List<ThemeParticipantListItem.ThemeParticipant>> =
        Single.fromCallable {
            dialogControllerProvider.get().getRelevantMessageReceiversProfiles(conversationUuid)
        }
            .doOnSuccess {
                activityStatusSubscriptionsInitializer.initialize(
                    it.map { profile -> profile.person.uuid }
                )
            }
            .map { result -> result.map { it.asListItemNative } }
            .compose(getSingleBackgroundSchedulers())

    /** @SelfDocumented */
    override fun setDialogTitle(dialogUuid: UUID, newTitle: String): Single<CommandStatus> =
        Single.fromCallable { dialogControllerProvider.get().setDialogTitle(dialogUuid, newTitle) }
            .compose(getSingleBackgroundSchedulers())

    /** @SelfDocumented */
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