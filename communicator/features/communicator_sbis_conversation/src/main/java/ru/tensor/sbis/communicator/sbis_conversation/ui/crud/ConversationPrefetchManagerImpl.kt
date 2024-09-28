package ru.tensor.sbis.communicator.sbis_conversation.ui.crud

import android.content.res.Resources
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.builder.EqualsBuilder
import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.communicator.common.conversation.ConversationPrefetchManager
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.MessageMapper
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractor
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractor.ConversationDataResult
import ru.tensor.sbis.mvp.data.model.PagedListResult
import java.util.*
import kotlin.math.ceil
import kotlin.math.min

/** @SelfDocumented */
internal class ConversationPrefetchManagerImpl(
    private val conversationListCommand: ConversationListCommand,
    private val messageMapper: MessageMapper,
    private val conversationDataInteractor: ConversationDataInteractor
) : ConversationPrefetchManager {
    private var prefetchRefreshObservable: Observable<PagedListResult<ConversationMessage>>? = null
    private var prefetchFilter: MessageFilter? = null
    @Volatile private var prefetchRefreshResult: PagedListResult<ConversationMessage>? = null

    private var prefetchDataObservable: Observable<ConversationDataResult>? = null
    private var prefetchDataSubscription: Disposable? = null
    private var prefetchRefreshSubscription: Disposable? = null
    private var prefetchDataFilter: ConversationDataFilter? = null
    @Volatile private var prefetchDataResult: ConversationDataResult? = null

    @Synchronized
    override fun prefetch(
        themeUuid: UUID,
        documentUuid: UUID?,
        relevantMessageUUID: UUID?,
        isGroupConversation: Boolean,
        isChat: Boolean,
        isConsultation: Boolean
    ): Completable {
        prefetchRefreshResult = null
        prefetchDataResult = null

        prefetchDataSubscription = null
        messageMapper.isGroupDialog = isGroupConversation
        messageMapper.isChannel = isChat
        val messagesCount = calculateMaxItemsCountForInitialLoading(isGroupConversation)

        val messageFilter = ConversationFilter()
            .also { it.themeUuid = themeUuid }
            .queryBuilder()
            .direction(if (relevantMessageUUID != null) QueryDirection.TO_BOTH else QueryDirection.TO_OLDER)
            .inclusive(true)
            .itemsCount(messagesCount)
            .castTo<ConversationFilter.ConversationFilterBuilder>()!!
            .relevantMessageUuid(relevantMessageUUID)
            .requestId(INITIAL_LOADING_STRING_ID)
            .build()

        val cachedDataObservable =
            loadConversationData(
                themeUuid,
                documentUuid,
                messagesCount,
                isChat,
                isConsultation
            )
                .doOnNext { prefetchDataResult = it }
                .observeOn(AndroidSchedulers.mainThread())
                .replay(1)
                .refCount()
                .doOnSubscribe { prefetchDataSubscription = it }

        val cachedRefreshObservable = conversationListCommand.prefetch(messageFilter)
            .doOnNext { prefetchRefreshResult = it }
            .replay(1)
            .refCount()
            .doOnSubscribe { prefetchRefreshSubscription = it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        prefetchFilter = messageFilter
        prefetchDataObservable = cachedDataObservable
        prefetchRefreshObservable = cachedRefreshObservable
        return Observable.combineLatest(
            cachedDataObservable,
            cachedRefreshObservable
        ) { _, _-> }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .ignoreElements()
    }

    override fun isReady(themeUuid: UUID): Boolean =
        prefetchFilter?.themeId == themeUuid && prefetchDataObservable != null

    @Synchronized
    fun prefetchListCommand(
        filter: MessageFilter
    ): Observable<PagedListResult<ConversationMessage>>? {
        val cachedObservable = prefetchRefreshObservable
        val cachedFilter = prefetchFilter
        val cachedResult = prefetchRefreshResult
        val subscription = prefetchRefreshSubscription
        prefetchRefreshSubscription = null
        prefetchRefreshObservable = null
        prefetchFilter = null
        prefetchRefreshResult = null
        return if (cachedObservable != null &&
            cachedFilter != null &&
            subscription?.isDisposed == false &&
            isFiltersEquals(cachedFilter, filter)
        ) {
            cachedResult?.let {
                Observable.just(it)
            } ?: cachedObservable
        } else null
    }

    @Synchronized
    fun alterLoadConversationData(
        conversationUuid: UUID?,
        documentUuid: UUID?,
        isChat: Boolean
    ): Observable<ConversationDataResult>? {
        val cachedObservable = prefetchDataObservable
        val subscription = prefetchDataSubscription
        val filter = prefetchDataFilter
        val cachedResult = prefetchDataResult
        prefetchDataObservable = null
        prefetchDataSubscription = null
        prefetchDataFilter = null
        prefetchDataResult = null
        return if (cachedObservable != null && subscription?.isDisposed == false
            && filter == ConversationDataFilter(conversationUuid, documentUuid, isChat)) {
            cachedResult?.let { Observable.just(it) }
                ?: cachedObservable
        } else null
    }

    override fun clear() {
        prefetchRefreshObservable = null
        prefetchFilter = null
        prefetchRefreshResult = null
        prefetchDataObservable = null
        prefetchDataSubscription = null
        prefetchDataFilter = null
        prefetchDataResult = null
    }

    private fun loadConversationData(
        conversationUuid: UUID?,
        documentUuid: UUID?,
        messagesCount: Int,
        isChat: Boolean,
        isConsultation: Boolean
    ): Observable<ConversationDataResult> =
        conversationDataInteractor.backgroundLoadConversationData(
            conversationUuid = conversationUuid,
            documentUuid = documentUuid,
            startMessage = null,
            messagesCount = messagesCount,
            isChat = isChat,
            isConsultation = isConsultation
        ).also {
            prefetchDataFilter = ConversationDataFilter(conversationUuid, documentUuid, isChat)
        }

    private data class ConversationDataFilter(
        val conversationUuid: UUID?,
        val documentUuid: UUID?,
        val isChat: Boolean
    )

    private fun isFiltersEquals(
        prefetch: MessageFilter,
        screen: MessageFilter
    ): Boolean =
        EqualsBuilder()
            .append(prefetch.themeId, screen.themeId)
            .append(prefetch.fromUuid, screen.fromUuid)
            .append(prefetch.direction, screen.direction)
            .build()

    companion object {
        const val INITIAL_LOADING_STRING_ID = "initial_loading"

        private const val ITEMS_RESERVE = 15
        private const val GROUP_CONVERSATION_MIN_ITEM_HEIGHT_DP = 56
        private const val PRIVATE_CONVERSATION_MIN_ITEM_HEIGHT_DP = 40
        private const val MESSAGE_PANEL_HEIGHT_DP = 44
        private const val TOOLBAR_HEIGHT_DP = 56
        private const val DEFAULT_PAGE_SIZE = 50

        fun calculateMaxItemsCountForInitialLoading(isGroupConversation: Boolean): Int {
            val displayMetrics = Resources.getSystem().displayMetrics
            val density = displayMetrics.density
            val minItemHeightDp =
                if (isGroupConversation) GROUP_CONVERSATION_MIN_ITEM_HEIGHT_DP else PRIVATE_CONVERSATION_MIN_ITEM_HEIGHT_DP
            val minItemHeightPx = minItemHeightDp.toPx(density)
            val availableListHeightPx =
                displayMetrics.heightPixels - TOOLBAR_HEIGHT_DP.toPx(density) - MESSAGE_PANEL_HEIGHT_DP.toPx(density)
            return min(ceil(availableListHeightPx / minItemHeightPx).toInt() + ITEMS_RESERVE, DEFAULT_PAGE_SIZE)
        }

        private fun Int.toPx(density: Float): Float {
            return this * density
        }
    }
}