package ru.tensor.sbis.communicator.sbis_conversation.ui.messagelistsection

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSection
import ru.tensor.sbis.base_components.adapter.sectioned.lifecycle.LifecycleSimulator
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesContract
import ru.tensor.sbis.mvp.adapter.sectioned.lifecycle.PresenterLifecycleBinder

/**
 * Класс для привязки вью к презентеру.
 *
 * @author da.zhukov
 */
internal object MessagesSectionPresenterLifecycleSectionBinder {

    /**
     * Присоединяет экземпляр [PresenterLifecycleBinder] к секции [section].
     */
    @JvmStatic
    fun bind(
        section: ListSection<*, *, *>,
        lifecycle: Lifecycle,
        presenter: ConversationMessagesContract.Presenter<ConversationMessagesContract.View>,
        view: ConversationMessagesContract.View
    ) {
        object : MessagesSectionPresenterLifecycleBinder(lifecycle, presenter, view) {
            override fun onAttachView() {
                if (section.isAttachedToView) { //в противном случае произошел detach секции и мы не должны реагировать на методы жизненного цикла
                    super.onAttachView()
                }
            }

            override fun onDetachView() {
                if (section.isAttachedToView) {
                    super.onDetachView()
                }
            }
        }
    }
}

internal open class MessagesSectionPresenterLifecycleBinder(
    lifecycle: Lifecycle,
    private val presenter: ConversationMessagesContract.Presenter<ConversationMessagesContract.View>,
    private val view: ConversationMessagesContract.View
) {

    @Suppress("unused")
    private val lifecycleSimulator = object : LifecycleSimulator(lifecycle) {
        override fun onStart() {
            this@MessagesSectionPresenterLifecycleBinder.onAttachView()
        }

        override fun onStop() {
            this@MessagesSectionPresenterLifecycleBinder.onDetachView()
        }
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onAttachView() {
        presenter.attachView(view)
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onDetachView() {
        presenter.detachView()
    }
}