package ru.tensor.sbis.communicator.sbis_conversation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Completable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.design.cloud_view.content.utils.MessageResourcesHolder
import androidx.core.view.updatePadding
import androidx.tracing.trace
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.communicatorSbisConversationDependency
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.feature
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.cloud_view.content.utils.BaseMessageResourceHolder
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.message_view.ui.MessageView
import ru.tensor.sbis.design.message_view.utils.SimpleViewPool
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.util.ButtonsFactory.createDefaultButton
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.getDimen
import timber.log.Timber
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.base_components.R as RBaseComponents
import ru.tensor.sbis.communicator.sbis_conversation.R as RConversation
import ru.tensor.sbis.communicator.base.conversation.R as RBaseConversation

/**
 * ViewPool реестра сообщений.
 * Содержит корневую view экрана, а также пулы view ячеек сообщений и внутреннего контента.
 *
 * @author vv.chekurda
 */
internal class ConversationViewPool(
    private val context: Context,
    messageResourcesHolder: MessageResourcesHolder = BaseMessageResourceHolder(context)
) : LifecycleObserver {

    private val conversationRootViewPool = SimpleViewPool {
        trace("ConversationViewPool inflate conversationRootView") {
            LayoutInflater.from(context).inflate(
                feature.conversationFactory.layoutId,
                null
            ).also {
                val topNavigation = it.findViewById<SbisTopNavigationView>(RConversation.id.communicator_conversation_fragment_top_navigation)
                topNavigation.apply {
                    content = SbisTopNavigationContent.SmallTitle(
                        PlatformSbisString.Value(StringUtils.EMPTY)
                    )
                    rightBtnContainer!!.isVisible = true
                    rightBtnContainer!!.addView(
                        createDefaultButton(PlatformSbisString.Icon(SbisMobileIcon.Icon.smi_navBarMore)).apply {
                            id = RConversation.id.communicator_conversation_toolbar_icon
                            setPadding(context.getDimen(RDesign.attr.offset_s).toInt())
                        }
                    )
                    personView!!.prepareCollage()
                }
                it.measure(MeasureSpecUtils.makeUnspecifiedSpec(), MeasureSpecUtils.makeUnspecifiedSpec())
                it.layout(0, 0)
            }
        }
    }

    @SuppressLint("InflateParams")
    private val progressItemViewPool = SimpleViewPool<View> {
        LayoutInflater.from(context).inflate(
            RBaseComponents.layout.base_components_progress_list_item,
            null
        )
    }

    private val messageItemViewPool = SimpleViewPool {
        trace("ConversationViewPool.createmessageItemView") {
            MessageView(context).apply {
                id = RBaseConversation.id.communicator_message
                setMessageViewPool(messageViewPool)
            }
        }
    }

    private var isViewPoolsPrepared = false

    private val disposable = SerialDisposable()

    val messageViewPool =
        communicatorSbisConversationDependency!!.messageViewComponentsFactory
            .createMessageViewPool(
                context = context,
                mediaPlayer = communicatorSbisConversationDependency?.mediaPlayerFeature?.getMediaPlayer(),
                messageResourcesHolder = messageResourcesHolder
            )

    val conversationView: View
        get() = conversationRootViewPool.getView()

    val progressView: View
        get() = progressItemViewPool.getView()

    val messageView: MessageView
        get() = messageItemViewPool.getView()

    fun recycleConversationView(view: View) {
        view.updatePadding(bottom = 0)
        conversationRootViewPool.addView(view)
    }

    fun tryToGet(context: Context): ConversationViewPool? =
        takeIf { this.context == context }

    fun prepareViewPools() {
        if (isViewPoolsPrepared) return
        isViewPoolsPrepared = true
        Looper.myQueue().addIdleHandler {
            conversationRootViewPool.prefetch()
            false
        }
        Looper.myQueue().addIdleHandler {
            Completable.fromAction {
                try {
                    safeExecute { messageItemViewPool.prefetch(MESSAGE_VIEW_POOL_SIZE) }
                    safeExecute { messageViewPool.prefetch() }
                    safeExecute { progressItemViewPool.prefetch(PROGRESS_VIEW_POOL_SIZE) }
                } catch (ex: Exception) {
                    Timber.w(ex, "Failed to async pumping up conversation view pools")
                }
            }
                .subscribeOn(Schedulers.computation())
                .subscribe()
                .storeIn(disposable)
            false
        }
    }

    private fun safeExecute(action: () -> Unit) {
        if (!disposable.isDisposed) action()
    }

    @Suppress("unused", "DEPRECATION")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun flush() {
        disposable.dispose()
        messageViewPool.clear()
        messageItemViewPool.clear()
        conversationRootViewPool.clear()
        progressItemViewPool.clear()
    }
}

private const val MESSAGE_VIEW_POOL_SIZE = 30
private const val PROGRESS_VIEW_POOL_SIZE = 4