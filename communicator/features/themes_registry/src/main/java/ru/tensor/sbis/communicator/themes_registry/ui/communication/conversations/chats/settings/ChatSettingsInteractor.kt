package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import android.content.Context
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.core.utils.subscribeDataRefresh
import ru.tensor.sbis.communicator.generated.ChannelType
import ru.tensor.sbis.communicator.generated.Chat
import ru.tensor.sbis.communicator.generated.ChatController
import ru.tensor.sbis.communicator.generated.ChatNotificationOptions
import ru.tensor.sbis.communicator.generated.ChatResult
import ru.tensor.sbis.communicator.generated.ConversationResult
import ru.tensor.sbis.communicator.generated.ParticipantRole
import ru.tensor.sbis.communicator.generated.ParticipationType
import ru.tensor.sbis.communicator.themes_registry.data.mapper.ChatParticipantMapper
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.ChatAdministratorsSettingsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile
import java.util.UUID

/**
 * Интерактор настроек чата.
 *
 * @param chatController - контроллер чатов [ChatController].
 * @param chatSettingsCommandWrapper - @SelfDocumented.
 * @param chatAdministratorsCommandWrapper - @SelfDocumented.
 * @param mapper - маппер участников чата.
 * @param context - контекст.
 */
internal class ChatSettingsInteractor(
    private val chatController: DependencyProvider<ChatController>,
    private val employeeProfileControllerWrapperProvider: DependencyProvider<EmployeeProfileControllerWrapper>,
    private val themeRepository: ThemeRepository,
    override val chatSettingsCommandWrapper: ChatSettingsCommandWrapper,
    override val chatAdministratorsCommandWrapper: ChatAdministratorsSettingsCommandWrapper,
    private var mapper: ChatParticipantMapper,
    private val context: Context,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer,
) : BaseInteractor(), ChatSettingsContract.ChatParticipantsInteractor {

    override fun observeThemeControllerUpdates(): Observable<HashMap<String, String>> =
        Observable.fromCallable { themeRepository }
            .flatMap { it.subscribeDataRefresh() }
            .compose(getObservableBackgroundSchedulers())

    override fun getThemeParticipantList(participantsUuids: List<UUID>): Single<List<ThemeParticipant>> =
        Single.fromCallable {
            employeeProfileControllerWrapperProvider.get().getEmployeeProfilesFromCache(participantsUuids)
        }
            .doOnSuccess {
                activityStatusSubscriptionsInitializer.initialize(
                    it.map { profile -> profile.uuid }
                )
            }
            .map { result -> result.map { ThemeParticipant(it, ParticipantRole.MEMBER, it.initialsStubData) } }
            .compose(getSingleBackgroundSchedulers())

    /** @SelfDocumented */
    fun loadMyProfile(): Observable<ContactVM> =
        Observable.fromCallable<EmployeeProfile> {
            employeeProfileControllerWrapperProvider.get().getCurrentEmployeeProfile()
        }
            .compose(getObservableBackgroundSchedulers())
            .map(
                Function { profileResult ->
                    val model = mapper.apply(profileResult)
                    model.isWithSubAttribute = true
                    return@Function model
                }
            )

    /** @SelfDocumented */
    fun loadProfile(uuid: UUID): Observable<ContactVM> =
        Observable.fromCallable<EmployeeProfile> {
            employeeProfileControllerWrapperProvider.get().getEmployeeProfileFromCache(uuid)
        }
            .compose(getObservableBackgroundSchedulers())
            .map(
                Function { profileResult ->
                    val model = mapper.apply(profileResult)
                    model.isWithSubAttribute = true
                    return@Function model
                }
            )

    /** @SelfDocumented */
    fun loadChat(uuid: UUID): Single<Chat> {
        return Single.fromCallable {
            chatController.get().getChatByUuid(uuid)
        }
            .map { it.data }
            .compose(getSingleBackgroundSchedulers())
    }

    /** @SelfDocumented */
    fun createNewChat(
        name: String,
        notificationOptions: ChatNotificationOptions,
        avatar: ByteArray?,
        fileName: String?,
        participants: List<UUID>,
        channelType: ChannelType?,
        participationType: ParticipationType?,
    ): Single<ChatResult> {
        return Single.fromCallable {
            chatController.get().create(
                name,
                notificationOptions,
                avatar,
                fileName,
                participants.asArrayList(),
                channelType,
                participationType
            )
        }
            .compose(getSingleBackgroundSchedulers())
    }

    /** @SelfDocumented */
    fun updateChat(
        uuid: UUID,
        name: String,
        notificationOptions: ChatNotificationOptions,
        avatar: ByteArray?,
        fileName: String?,
        channelType: ChannelType?,
        participationType: ParticipationType?,
    ): Single<ChatResult> {
        return Single.fromCallable {
            chatController.get().update(
                uuid,
                name,
                notificationOptions,
                avatar,
                fileName,
                channelType,
                participationType,
            )
        }
            .compose(getSingleBackgroundSchedulers())
    }

    /** @SelfDocumented */
    fun convertDialogToChat(uuid: UUID, name: String, notificationOptions: ChatNotificationOptions, avatar: ByteArray?, fileName: String?): Single<Boolean> {
        return Single.fromCallable {
            chatController.get().convertDialogToChat(uuid, name, notificationOptions, avatar, fileName)
        }
            .compose(getSingleBackgroundSchedulers())
    }

    /** @SelfDocumented */
    fun closeChat(chatUuid: UUID): Single<CommandStatus> {
        return Single.fromCallable {
            chatController.get().close(chatUuid)
        }.compose(getSingleBackgroundSchedulers())
    }

    /** @SelfDocumented */
    fun deleteAvatar(chatUuid: UUID): Single<CommandStatus> {
        return Single.fromCallable {
            chatController.get().removeChatIcon(chatUuid)
        }
    }

    /** @SelfDocumented */
    fun getConversationData(chatUuid: UUID): Single<ConversationResult> = Single.fromCallable {
        chatController.get().getConversationData(chatUuid)
    }.compose(getSingleBackgroundSchedulers())
}
