package ru.tensor.sbis.communicator.sbis_conversation.interactor.data

import androidx.tracing.Trace
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.ActivityStatusUtil
import ru.tensor.sbis.common.util.ActivityStatusUtil.getActivityStateText
import ru.tensor.sbis.common.util.PreviewerUrlUtil
import ru.tensor.sbis.communicator.common.util.PersonAvatarPrefetchHelper
import ru.tensor.sbis.communicator.generated.ChatController
import ru.tensor.sbis.communicator.generated.ConversationResult
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.ConversationDataMapper
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationData
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractor.ConversationDataResult
import ru.tensor.sbis.person_decl.profile.model.Gender
import ru.tensor.sbis.person_decl.profile.model.ProfileActivityStatus
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import java.util.UUID

/**
 * Реализация интерактора для загрузки данных экрана реестра сообщений.
 *
 * @author vv.chekurda
 */
internal class ConversationDataInteractorImpl(
    private val dialogControllerProvider: DependencyProvider<DialogController>,
    private val chatControllerProvider: DependencyProvider<ChatController>,
    private val conversationDataMapper: ConversationDataMapper,
    private var prefetchHelper: PersonAvatarPrefetchHelper
) : BaseInteractor(), ConversationDataInteractor {

    /**@SelfDocumented*/
    override fun loadConversationData(
        conversationUuid: UUID?,
        documentUuid: UUID?,
        startMessage: ConversationMessage?,
        messagesCount: Int,
        isChat: Boolean,
        isConsultation: Boolean
    ): Observable<ConversationDataResult> {
        return getConversationDataObservable(
            conversationUuid,
            documentUuid,
            startMessage,
            messagesCount,
            isChat
        ).compose(getObservableBackgroundSchedulers())
    }

    /**@SelfDocumented*/
    override fun backgroundLoadConversationData(
        conversationUuid: UUID?,
        documentUuid: UUID?,
        startMessage: ConversationMessage?,
        messagesCount: Int,
        isChat: Boolean,
        isConsultation: Boolean
    ): Observable<ConversationDataResult> {
        return getConversationDataObservable(
            conversationUuid,
            documentUuid,
            startMessage,
            messagesCount,
            isChat
        ).subscribeOn(Schedulers.io())
    }

    private fun getConversationDataObservable(
        conversationUuid: UUID?,
        documentUuid: UUID?,
        startMessage: ConversationMessage?,
        messagesCount: Int,
        isChat: Boolean
    ): Observable<ConversationDataResult> =
        Observable.fromCallable {
            Trace.beginAsyncSection("getConversationData", 0)
            val result = if (isChat) {
                chatControllerProvider.get().getConversationData(conversationUuid)
            } else {
                dialogControllerProvider.get().getConversationData(
                    conversationUuid,
                    messagesCount,
                    startMessage?.timestampSent ?: 0L,
                    documentUuid
                )
            }
            prepareCollage(result)
            result
        }.flatMap { conversationResult: ConversationResult ->
            if (conversationResult.status.errorCode == ErrorCode.SYNC_IN_PROGRESS && conversationResult.data == null) {
                Observable.empty()
            } else {
                Observable.just(conversationResult)
            }
        }.map<ConversationDataResult> {
            ConversationDataResultImpl(
                it.data?.let {data -> conversationDataMapper.map(data, isChat) } ?: ConversationData(),
                it.status
            ).also {
                Trace.endAsyncSection("getConversationData", 0)
            }
        }.let {
            if (isChat && conversationUuid != null) insertChatAdditionalInfo(it, conversationUuid)
            else it
        }

    private fun prepareCollage(result: ConversationResult) {
        Trace.beginAsyncSection("prefetchCollage", 0)
        val visibleParticipants = result.data
            ?.participants
            ?.filter { !it.photoUrl.isNullOrEmpty() }
            ?.take(4)
        visibleParticipants?.forEach {
            it.photoUrl = prefetchHelper.prepareUri(it.photoUrl!!, PersonAvatarPrefetchHelper.REGISTRY_AVATAR_SIZE_DP, PreviewerUrlUtil.ScaleMode.RESIZE)
        }
        prefetchHelper.prefetchBitmaps(
            visibleParticipants.orEmpty(),
            result.data?.photoUrl,
            PersonAvatarPrefetchHelper.REGISTRY_AVATAR_SIZE_DP
        )
        Trace.endAsyncSection("prefetchCollage", 0)
    }

    private fun insertChatAdditionalInfo(
        originalObservable: Observable<ConversationDataResult>,
        conversationUuid: UUID
    ): Observable<ConversationDataResult> =
        originalObservable.zipWith(
            Observable.fromCallable {
                chatControllerProvider.get()
                    .getAllUnreadCounter(conversationUuid)
                    ?: throw RuntimeException("ChatController.getAllUnreadCounter == null")
            }
        ) { conversationDataResult: ConversationDataResult, unreadCount: Int? ->
            conversationDataResult.conversationData.unreadCount = unreadCount ?: 0
            conversationDataResult
        }

    /**@SelfDocumented*/
    override fun createToolbarSubtitle(
        conversationData: ConversationData,
        status: ProfileActivityStatus
    ): Single<String> =
        Single.fromCallable {
            val participants = conversationData.participants
            when {
                participants.isNullOrEmpty() || participants.first().isHasAccess ->
                    getActivityStateText(
                        conversationDataMapper.context,
                        status,
                        conversationData.participants
                            ?.takeIf { it.isNotEmpty() }
                            ?.getOrNull(0)
                            ?.gender
                            ?: Gender.UNKNOWN
                    )
                conversationData.isLocked == true -> StringUtils.EMPTY
                else -> ActivityStatusUtil.getActivityStateDeniedText(conversationDataMapper.context)
            }
        }

    private data class ConversationDataResultImpl(
        override val conversationData: ConversationData,
        override val commandStatus: CommandStatus
    ) : ConversationDataResult {
        init {
            conversationData.conversationAccess.isAvailable =
                ErrorCode.NOT_AVAILABLE != commandStatus.errorCode
        }
    }
}