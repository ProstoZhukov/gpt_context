package ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates

import ru.tensor.sbis.communicator.common.conversation.ConversationRouter
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MediaMessageActionListener
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessageAccessButtonListener
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessageThreadActionListener
import ru.tensor.sbis.communicator.sbis_conversation.adapters.SenderActionClickListener
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationSingAndAcceptHandler
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.PhoneNumberSelectionItemListener
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.PhoneNumberVerificationErrorHandler
import ru.tensor.sbis.design.cloud_view.content.phone_number.PhoneNumberClickListener

/**
 * Делегат реестра сообщений.
 *
 * @author da.zhukov
 */
internal class ConversationActionDelegate(
    private val senderActionListenerDelegate: ConversationSenderActionListenerDelegate,
    private val phoneNumberDelegate: ConversationPhoneNumberDelegate,
    val singAndAcceptHelper: ConversationSingAndAcceptActionDelegate,
    private val mediaActionDelegate: ConversationMediaActionDelegate,
    val threadActionDelegate: ConversationThreadActionDelegate
) : ConversationMessagesBaseDelegate(),
    SenderActionClickListener by senderActionListenerDelegate,
    PhoneNumberSelectionItemListener by phoneNumberDelegate,
    PhoneNumberClickListener by phoneNumberDelegate,
    PhoneNumberVerificationErrorHandler by phoneNumberDelegate,
    MessageAccessButtonListener by singAndAcceptHelper,
    ConversationSingAndAcceptHandler by singAndAcceptHelper,
    MediaMessageActionListener by mediaActionDelegate,
    MessageThreadActionListener by threadActionDelegate {

    override fun initRouter(router: ConversationRouter?) {
        super.initRouter(router)
        senderActionListenerDelegate.initRouter(router)
        phoneNumberDelegate.initRouter(router)
        singAndAcceptHelper.initRouter(router)
        mediaActionDelegate.initRouter(router)
        threadActionDelegate.initRouter(router)
    }

    override fun initView(view: ConversationMessagesContract.View?) {
        super.initView(view)
        senderActionListenerDelegate.initView(view)
        phoneNumberDelegate.initView(view)
        singAndAcceptHelper.initView(view)
        mediaActionDelegate.initView(view)
        threadActionDelegate.initView(view)
    }

    override fun clear() {
        super.clear()
        senderActionListenerDelegate.clear()
        phoneNumberDelegate.clear()
        singAndAcceptHelper.clear()
        mediaActionDelegate.clear()
        threadActionDelegate.clear()
    }
}