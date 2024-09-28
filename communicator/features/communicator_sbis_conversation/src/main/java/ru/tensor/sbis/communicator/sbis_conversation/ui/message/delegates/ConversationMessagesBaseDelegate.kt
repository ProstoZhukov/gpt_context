package ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates

import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesContract

/**
 * Базовая реализация делегата работы с сообщениями.
 *
 * @author da.zhukov
 */
internal abstract class ConversationMessagesBaseDelegate {

    protected val compositeDisposable = CompositeDisposable()

    protected var router: ConversationRouter? = null
    protected var view: ConversationMessagesContract.View? = null

    /** @SelfDocumented */
    open fun initRouter(router: ConversationRouter?) {
        this.router = router
    }

    /** @SelfDocumented */
    open fun initView(view: ConversationMessagesContract.View?) {
        this.view = view
    }

    /** @SelfDocumented */
    open fun clear() {
        compositeDisposable.dispose()
    }
}