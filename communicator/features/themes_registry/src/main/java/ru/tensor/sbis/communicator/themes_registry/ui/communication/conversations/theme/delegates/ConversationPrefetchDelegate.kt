package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates

import android.os.Handler
import android.view.MotionEvent
import androidx.annotation.Px
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.base_components.adapter.checkable.ObservableCheckItemsHelper
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.common.conversation.ConversationPrefetchManager
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.generated.ChatType
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.router.theme.getFromMessageUuid
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.isConversation
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.contract.ThemeView
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorUtils.dp
import ru.tensor.sbis.persons.ConversationRegistryItem
import kotlin.math.abs

/**
 * Делегат для предзагрузки данных по переписке из реестра диалогов/каналов.
 * Определяет необходимость предзагрузки данных при взаимодействии с ячейками реестра (при касании похожих на клик),
 * а также позволяет гарантированно запустить загрузку данных по открываемой переписке при обработке клика на ячейке.
 *
 * @author vv.chekurda
 */
internal class ConversationPrefetchDelegate(
    private val checkHelper: ObservableCheckItemsHelper<ConversationRegistryItem>,
    private val handler: Handler
) {
    private var view: ThemeView? = null
    private var lastPrefetchAction: Runnable? = null
    private var lastOpenedConversation: ConversationModel? = null
    private var clearConversationRunnable = Runnable { lastOpenedConversation = null }
    private var downX = 0f
    private var downY = 0f
    @Px private var allowableMovementX = 0
    @Px private var allowableMovementY = 0
    private val serialDisposable = SerialDisposable()
    private var prefetchManager: ConversationPrefetchManager? =
        themesRegistryDependency
            .conversationListCommandPrefetchManagerProvider
            ?.prefetchManager

    /** Предварительно загрузить переписку. */
    fun prefetchConversation(conversation: ConversationModel, isSearchEmpty: Boolean = true) {
        if (prefetchManager?.isReady(conversation.uuid) == true) return
        lastOpenedConversation = conversation

        // запрашиваем заранее список сообщений в открываемой переписке
        prefetchManager
            ?.prefetch(
                themeUuid = conversation.uuid,
                documentUuid = conversation.documentUuid,
                relevantMessageUUID = conversation.getFromMessageUuid(conversation.isChatForView, isSearchEmpty),
                isGroupConversation = conversation.isGroupConversation,
                isChat = conversation.isChatForOperations,
                isConsultation = conversation.chatType == ChatType.CONSULTATION
            )
            ?.doOnSubscribe {
                handler.removeCallbacks(clearConversationRunnable)
                handler.postDelayed(clearConversationRunnable, ALLOWABLE_TOUCH_DURATION)
            }
            ?.subscribe()
            ?.storeIn(serialDisposable)
    }

    /** Изменить предзагрузку данных в зависимости от клика на ячейку диалога/канала. */
    fun onConversationItemTouch(conversation: ConversationModel, isSearchEmpty: Boolean, event: MotionEvent) {
        if (!view!!.isScrolling && !checkHelper.isCheckModeEnabled && conversation.isConversation) {
            if (event.action != MotionEvent.ACTION_DOWN && event.action != MotionEvent.ACTION_MOVE) return
            val timeDiff = event.eventTime - event.downTime
            val xDiff = abs(event.x - downX)
            val yDiff = abs(event.y - downY)

            if (event.action == MotionEvent.ACTION_DOWN) {
                removeLastPrefetchAction()
                handler.postDelayed(getPrefetchRunnable(conversation, isSearchEmpty), TAP_DOWN_DELAY_MS)
                downX = event.x
                downY = event.y
            } else if (timeDiff <= TAP_DOWN_DELAY_MS && (xDiff >= allowableMovementX || yDiff >= allowableMovementY)) {
                removeLastPrefetchAction()
            }
        } else {
            removeLastPrefetchAction()
        }
    }

    private fun getPrefetchRunnable(conversation: ConversationModel, isSearchEmpty: Boolean) =
        Runnable {
            prefetchConversation(conversation, isSearchEmpty)
        }.also { lastPrefetchAction = it }

    private fun removeLastPrefetchAction() {
        lastPrefetchAction?.let {
            handler.removeCallbacks(it)
            lastPrefetchAction = null
        }
    }

    /** @SelfDocumented */
    fun attachView(view: ThemeView) {
        this.view = view
        allowableMovementX = dp(ALLOWABLE_MOVEMENT_X_DP)
        allowableMovementY = dp(ALLOWABLE_MOVEMENT_Y_DP)
    }

    /** @SelfDocumented */
    fun detachView() {
        view = null
    }

    /** @SelfDocumented */
    fun onDestroy() {
        serialDisposable.dispose()
        prefetchManager?.clear()
        prefetchManager = null
    }
}

/** Интервал времени в мс до запуска предварительной загрузки, чтобы сборать данные по касанию пользователя */
private const val TAP_DOWN_DELAY_MS = 32L
/** Эмпирическая максимально допустимая дистанция смещения пальца по оси Х в dp при клике на ячейку */
private const val ALLOWABLE_MOVEMENT_X_DP = 0.5f
/** Эмпирическая максимально допустимая дистанция смещения пальца по оси Y в dp при клике на ячейку */
private const val ALLOWABLE_MOVEMENT_Y_DP = 1f
/**
 * Допустимый интервал обработки кликов от начала касания до распознавания клика на view.
 * Необходим для предотвращения 2ых загрузок одного и того же диалога при касаниии/клике на ячейку.
 */
private const val ALLOWABLE_TOUCH_DURATION = 500L