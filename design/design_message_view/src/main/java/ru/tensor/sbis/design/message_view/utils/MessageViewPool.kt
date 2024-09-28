package ru.tensor.sbis.design.message_view.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.content.MessageBlockView
import ru.tensor.sbis.design.cloud_view.content.utils.MessageResourcesHolder
import ru.tensor.sbis.design.cloud_view.content.utils.MessagesViewPool
import ru.tensor.sbis.design.cloud_view.thread.CloudThreadView
import ru.tensor.sbis.design.cloud_view.thread.ThreadCreationServiceView
import ru.tensor.sbis.design.cloud_view_integration.RichTextMessageBlockTextHolder
import ru.tensor.sbis.design.message_view.MessageViewPlugin.customizationOptions
import ru.tensor.sbis.design.message_view.R
import ru.tensor.sbis.design.message_view.contact.MessageViewPoolFactory
import ru.tensor.sbis.design.message_view.content.crm_views.greetings_view.GreetingsView
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.MessageRateView
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.cloud_view.video.VideoMessageCloudView
import ru.tensor.sbis.design.cloud_view.R as RCloud
import ru.tensor.sbis.design.R as RDesign

/**
 * Пул view ячеек сообщений, обеспечивающий возможность их повторного использования.
 *
 * @author dv.baranov
 */
class MessageViewPool private constructor(
    private val context: Context,
    private val mediaPlayer: MediaPlayer?,
    val messageBlockViewPool: MessagesViewPool
) {

    companion object : MessageViewPoolFactory {
        override fun createMessageViewPool(
            context: Context,
            mediaPlayer: MediaPlayer?,
            messageResourcesHolder: MessageResourcesHolder
        ): MessageViewPool {
            val themedContext = getThemedContext(context)
            return MessageViewPool(
                themedContext,
                mediaPlayer,
                MessagesViewPool(themedContext, messageResourcesHolder)
            )
        }
    }

    private val viewPools = mutableListOf<SimpleViewPool<*>>()

    private val incomeCloudViewPool = SimpleViewPool {
        CloudView(
            context = context,
            defStyleAttr = RCloud.attr.incomeCloudViewTheme,
            defStyleRes = RCloud.style.DefaultCloudViewTheme_Income
        ).apply {
            id = R.id.design_message_cloud
            isPersonal = true
            setViewPool(messageBlockViewPool)
            setTextHolder(RichTextMessageBlockTextHolder())
            mediaPlayer?.also(::setMediaPlayer)
        }
    }.register()

    private val outcomeCloudViewPool = SimpleViewPool {
        CloudView(
            context = context,
            defStyleAttr = RCloud.attr.outcomeCloudViewTheme,
            defStyleRes = RCloud.style.DefaultCloudViewTheme_Outcome
        ).apply {
            id = R.id.design_message_cloud
            isPersonal = true
            setViewPool(messageBlockViewPool)
            setTextHolder(RichTextMessageBlockTextHolder())
            mediaPlayer?.also(::setMediaPlayer)
        }
    }.register()

    private val incomeVideoCloudViewPool = SimpleViewPool {
        VideoMessageCloudView(
            context = context,
            defStyleAttr = RCloud.attr.incomeCloudViewTheme,
            defStyleRes = RCloud.style.DefaultCloudViewTheme_Income
        ).apply {
            id = R.id.design_video_message_cloud
            isPersonal = true
            setTextHolder(RichTextMessageBlockTextHolder())
            mediaPlayer?.also(::setMediaPlayer)
        }
    }.register()

    private val outcomeVideoCloudViewPool = SimpleViewPool {
        VideoMessageCloudView(
            context = context,
            defStyleAttr = RCloud.attr.outcomeCloudViewTheme,
            defStyleRes = RCloud.style.DefaultCloudViewTheme_Outcome
        ).apply {
            id = R.id.design_video_message_cloud
            isPersonal = true
            setTextHolder(RichTextMessageBlockTextHolder())
            mediaPlayer?.also(::setMediaPlayer)
        }
    }.register()

    private val cloudThreadViewPool = SimpleViewPool {
        CloudThreadView(context)
    }.register()

    private val threadCreationServiceViewPool = SimpleViewPool {
        ThreadCreationServiceView(context)
    }.register()

    private val parentView: ViewGroup? = null

    private val serviceMessageViewPool = SimpleViewPool {
        LayoutInflater.from(context).inflate(R.layout.design_message_view_service_message, parentView)
    }.register()

    private val serviceMaterialsViewPool = SimpleViewPool {
        LayoutInflater.from(context).inflate(R.layout.design_message_view_service_materials, parentView).apply {
            findViewById<MessageBlockView>(R.id.design_message_view_service_message_materials_content).also {
                it.setMessageBlockTextHolder(RichTextMessageBlockTextHolder())
                it.setViewPool(messageBlockViewPool)
            }
        }
    }.register()

    private val chatBotButtonsViewPool = SimpleViewPool {
        LayoutInflater.from(context).inflate(R.layout.design_message_view_chat_bot_buttons, parentView).apply {
            findViewById<CloudView>(R.id.design_message_cloud).apply {
                setTextHolder(RichTextMessageBlockTextHolder())
                setViewPool(messageBlockViewPool)
            }
        }
    }.register()

    private val greetingsViewPool = SimpleViewPool {
        GreetingsView(context)
    }.register()

    private val incomeRateViewPool = SimpleViewPool {
        incomeCloudViewPool.createNewView().apply {
            addView(MessageRateView(context))
        }
    }

    private val outcomeRateViewPool = SimpleViewPool {
        outcomeCloudViewPool.createNewView().apply {
            addView(MessageRateView(context))
        }
    }

    /** Ячейка исходящего сообщения из пула. */
    internal val outcomeCloudView: CloudView
        get() = outcomeCloudViewPool.getView()

    /** Ячейка входящего сообщения из пула. */
    internal val incomeCloudView: CloudView
        get() = incomeCloudViewPool.getView()

    /** Ячейка исходящего видеосообщения из пула. */
    internal val outcomeVideoCloudView: VideoMessageCloudView
        get() = outcomeVideoCloudViewPool.getView()

    /** Ячейка входящего видеосообщения из пула. */
    internal val incomeVideoCloudView: VideoMessageCloudView
        get() = incomeVideoCloudViewPool.getView()

    /** Ячейка треда из пула. */
    internal val cloudThreadView: CloudThreadView
        get() = cloudThreadViewPool.getView()

    /** Ячейка сервисного сообщения создания треда из пула. */
    internal val threadCreationServiceView: ThreadCreationServiceView
        get() = threadCreationServiceViewPool.getView()

    /** Ячейка приветственных кнопок чата консультации из пула. */
    internal val greetingsView: GreetingsView
        get() = greetingsViewPool.getView()

    /** Ячейка сервисного сообщения из пула. */
    internal val serviceMessageView: View
        get() = serviceMessageViewPool.getView()

    /** Ячейка сервисного сообщения с контентом из пула. */
    internal val serviceMaterialsView: View
        get() = serviceMaterialsViewPool.getView()

    /** Ячейка кнопок чат-бота из пула. */
    internal val chatBotButtonsView: View
        get() = chatBotButtonsViewPool.getView()

    /** Ячейка входящего сообщения рейтинга из пула. */
    internal val incomeRateView: CloudView
        get() = incomeRateViewPool.getView()

    /** Ячейка исходящего сообщения рейтинга из пула. */
    internal val outcomeRateView: CloudView
        get() = outcomeRateViewPool.getView()

    /**
     * Добавить переданную view в предназначенный для неё пул.
     */
    fun addView(view: View) {
        when {
            view is CloudView && view.outcome -> {
                if (view.children.any { it is MessageRateView }) {
                    outcomeRateViewPool.addView(view)
                } else {
                    outcomeCloudViewPool.addView(view)
                }
                view.recycleViews()
            }
            view is CloudView -> {
                if (view.children.any { it is MessageRateView }) {
                    incomeRateViewPool.addView(view)
                } else {
                    incomeCloudViewPool.addView(view)
                }
                view.recycleViews()
            }
            view is VideoMessageCloudView && view.outcome -> {
                outcomeVideoCloudViewPool.addView(view)
            }
            view is VideoMessageCloudView -> {
                incomeVideoCloudViewPool.addView(view)
            }
            view is CloudThreadView -> {
                cloudThreadViewPool.addView(view)
            }
            view is ThreadCreationServiceView -> {
                threadCreationServiceViewPool.addView(view)
            }
            view is GreetingsView -> {
                greetingsViewPool.addView(view)
            }
            view.id == R.id.design_message_view_message_item_container -> {
                serviceMessageViewPool.addView(view)
            }
            view.id == R.id.design_message_view_message_chat_bot_buttons_container -> {
                chatBotButtonsViewPool.addView(view)
            }
            view is MessageRateView -> Unit
        }
    }

    /**
     * Заполнить view пулы.
     */
    fun prefetch() {
        messageBlockViewPool.prefetch()
        prefetchCloudViewPools()
        prefetchVideoMessageContentViewPools()
        cloudThreadViewPool.prefetch(THREAD_MESSAGE_POOL_SIZE)
        threadCreationServiceViewPool.prefetch(THREAD_CREATION_POOL_SIZE)
    }

    /**
     * Очистить пулы.
     */
    fun clear() {
        viewPools.forEach { it.clear() }
    }

    private fun prefetchCloudViewPools() {
        for (i in 0 until CLOUD_VIEW_POOL_SIZE) {
            if (i % 2 == 0) {
                incomeCloudViewPool.prefetch()
            } else {
                outcomeCloudViewPool.prefetch()
            }
        }
    }

    private fun prefetchVideoMessageContentViewPools() {
        for (i in 0 until VIDEO_MESSAGE_CONTENT_POOL_SIZE) {
            if (i % 2 == 0) {
                incomeVideoCloudViewPool.prefetch()
            } else {
                outcomeVideoCloudViewPool.prefetch()
            }
        }
    }

    private fun <T : View> SimpleViewPool<T>.register(): SimpleViewPool<T> =
        apply { viewPools.add(this) }
}

private fun getThemedContext(context: Context): Context =
    if (customizationOptions.needUseDefaultTheme) {
        ThemeContextBuilder(
            context,
            defaultStyle = RDesign.style.AppTheme
        ).build()
    } else {
        context
    }

private const val CLOUD_VIEW_POOL_SIZE = 30
private const val VIDEO_MESSAGE_CONTENT_POOL_SIZE = 4
private const val THREAD_MESSAGE_POOL_SIZE = 4
private const val THREAD_CREATION_POOL_SIZE = 2
