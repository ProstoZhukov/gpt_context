package ru.tensor.sbis.message_panel.delegate

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.design.swipeback.SwipeBackHelper
import ru.tensor.sbis.design.swipeback.SwipeBackLayout
import ru.tensor.sbis.message_panel.attachments.AttachmentsRouterImpl
import ru.tensor.sbis.message_panel.attachments.viewer.DefaultViewerSliderArgsFactory
import ru.tensor.sbis.message_panel.contract.*
import ru.tensor.sbis.message_panel.contract.attachments.ViewerSliderArgsFactory
import ru.tensor.sbis.message_panel.di.MessagePanelComponentProvider
import ru.tensor.sbis.message_panel.integration.CommunicatorMessagePanelInitializerDelegate
import ru.tensor.sbis.message_panel.integration.CommunicatorMessageServiceDependency
import ru.tensor.sbis.message_panel.interactor.attachments.MessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.view.AlertDialogDelegate
import ru.tensor.sbis.message_panel.view.MessagePanel
import ru.tensor.sbis.message_panel.view.ProgressDialogDelegate
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.recorder.decl.RecorderViewDependencyProvider
import timber.log.Timber
import java.util.*

/**
 * Класс делегат реализующий дефолтный вариант инициализации и использования панели сообщений.
 * Поведение этого класса может быть переопределено в модуле который использует панель сообщений, если это необходимо.
 *
 * @param swipeBackLayout ленивое получение [SwipeBackLayout] т.к. его контент становится доступным не сразу, на этапе
 * [initMessagePanel]
 * @param withAudioMessage определяет отображение панели записи аудиосообщения. Если в панели нужна аудиозапись,
 * необходимо:
 * * Передать этот параметр true;
 * * Реализовать в [MessagePanelDependency] метод [RecorderViewDependencyProvider.getRecorderViewDependency].
 *
 * @author Subbotenko Dmitry
 */
open class MessagePanelInitializerDelegate<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> private constructor(
    context: Context,
    private val fragment: Fragment,
    swipeBackLayout: Lazy<SwipeBackLayout?>,
    withAudioMessage: Boolean,
    signDelegate: MessagePanelSignDelegate?,
    messageServiceDependency: MessageServiceDependency<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    customRecipientsInteractor: MessagePanelRecipientsInteractor?,
    customAttachmentsInteractor: MessagePanelAttachmentsInteractor?
) : AdjustResizeHelper.KeyboardEventListener {

    private val messagePanelComponent = MessagePanelComponentProvider[context]
    private val messagePanelDependency = messagePanelComponent.dependency
    private val resourceProvider = messagePanelComponent.getResourceProvider()

    /**
     * Для показа полноэкранного прогресс бара
     */
    private val progressDialogDelegate by lazy { ProgressDialogDelegate(fragment, resourceProvider) }

    /**
     * Для показа алерт диалога
     */
    private val alertDialogDelegate = AlertDialogDelegate(fragment)

    /**
     * Для обработки появления клавиатуры в альбомной ориентации
     */
    private var keyboardEventListener: AdjustResizeHelper.KeyboardEventListener? = null

    /**
     * Основной класс для управления панелью сообщений
     */
    protected open val controller: MessagePanelControllerImpl<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> by lazy {
        val dependency = if (withAudioMessage) {
            val recorderViewDependency = messagePanelDependency.getRecorderViewDependency(
                context,
                fragment.requireActivity(),
                swipeBackLayout.value
            )
            if (recorderViewDependency != null) {
                recorderViewDependency
            } else {
                Timber.i("$messagePanelDependency does not provide recorder dependency. Method RecorderViewDependencyProvider.getRecorderViewDependency() return null")
                null
            }
        } else {
            null
        }
        val attachmentsInteractor = customAttachmentsInteractor ?:
            messagePanelComponent.attachmentsComponentFactory.create().interactor
        val recipientsInteractor = customRecipientsInteractor ?:
            messagePanelComponent.recipientsComponent.recipientsInteractor
        MessagePanelControllerImpl(
            fragment,
            context,
            messageServiceDependency,
            recipientsInteractor,
            attachmentsInteractor,
            dependency,
            signDelegate
        )
    }

    constructor(
        context: Context,
        fragment: BaseFragment,
        withAudioMessage: Boolean = true,
        signDelegate: MessagePanelSignDelegate? = null,
        messageServiceDependency: MessageServiceDependency<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
        customRecipientsInteractor: MessagePanelRecipientsInteractor? = null,
        customAttachmentsInteractor: MessagePanelAttachmentsInteractor? = null
    ) : this(
        context,
        fragment,
        lazy { fragment.swipeBackLayout },
        withAudioMessage,
        signDelegate,
        messageServiceDependency,
        customRecipientsInteractor,
        customAttachmentsInteractor
    )

    constructor(
        context: Context,
        fragment: Fragment,
        swipeBackHelper: SwipeBackHelper? = null,
        withAudioMessage: Boolean = true,
        signDelegate: MessagePanelSignDelegate? = null,
        messageServiceDependency: MessageServiceDependency<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
        customRecipientsInteractor: MessagePanelRecipientsInteractor? = null,
        customAttachmentsInteractor: MessagePanelAttachmentsInteractor? = null
    ) : this(
        context,
        fragment,
        lazy { swipeBackHelper?.swipeBackLayout },
        withAudioMessage,
        signDelegate,
        messageServiceDependency,
        customRecipientsInteractor,
        customAttachmentsInteractor
    )

    /**
     * Инициализация и старт панели сообщений.
     */
    fun initMessagePanel(
        messagePanel: MessagePanel,
        coreConversationInfo: CoreConversationInfo?,
        attachmentViewerArgsFactory: ViewerSliderArgsFactory = DefaultViewerSliderArgsFactory,
        filesPickerConfig: MessagePanelFilesPickerConfig = MessagePanelFilesPickerConfig(),
        @IdRes movablePanelContainerId: Int = ID_NULL
    ): MessagePanelController<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> {
        attachAttachmentsRouter(fragment, messagePanelDependency, attachmentViewerArgsFactory)
        controller.initAttachmentsDelegate(filesPickerConfig)
        controller.setConversationInfo(coreConversationInfo)

        messagePanel.initViewModel(
            controller,
            controller,
            progressDialogDelegate,
            alertDialogDelegate,
            movablePanelContainerId
        )
        keyboardEventListener = messagePanel

        return controller
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int) =
        keyboardEventListener?.onKeyboardOpenMeasure(keyboardHeight) ?: false

    override fun onKeyboardCloseMeasure(keyboardHeight: Int) =
        keyboardEventListener?.onKeyboardCloseMeasure(keyboardHeight) ?: false

    fun onSaveInstanceState(outState: Bundle) {
        controller.onSaveInstanceState(outState)
    }

    private fun attachAttachmentsRouter(
        fragment: Fragment,
        dependency: MessagePanelDependency,
        attachmentViewerArgsFactory: ViewerSliderArgsFactory
    ) {
        controller.attachmentPresenter.router = AttachmentsRouterImpl(fragment, dependency, attachmentViewerArgsFactory)
        fragment.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                controller.attachmentPresenter.router = null
            }
        })
    }

    companion object {

        /**
         * Создание [MessagePanelInitializerDelegate] для работы с микросервисом сообщений
         */
        fun createCommunicatorMessagePanelInitializer(
            context: Context,
            fragment: BaseFragment,
            withAudioMessage: Boolean = true,
            signDelegate: MessagePanelSignDelegate? = null
        ): CommunicatorMessagePanelInitializerDelegate {
            val messageServiceDependency = MessagePanelComponentProvider[context]
                .getMessageController()
                .run(::CommunicatorMessageServiceDependency)
            return MessagePanelInitializerDelegate(
                context,
                fragment,
                withAudioMessage,
                signDelegate,
                messageServiceDependency,
                null
            )
        }
    }
}