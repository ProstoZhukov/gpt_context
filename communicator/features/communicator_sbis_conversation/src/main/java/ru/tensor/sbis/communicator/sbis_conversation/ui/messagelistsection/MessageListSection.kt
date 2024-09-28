package ru.tensor.sbis.communicator.sbis_conversation.ui.messagelistsection

import androidx.lifecycle.LifecycleObserver
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListItem
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSection
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSectionHolder
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListSizeSettings
import ru.tensor.sbis.mvp.presenter.DisplayErrorDelegate
import ru.tensor.sbis.communicator.declaration.MessageListController
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessagesListAdapter
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesPresenter
import ru.tensor.sbis.communicator.sbis_conversation.data.CoreConversationInfo
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagelistsection.di.DaggerMessageSectionComponent
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.ConversationToolbarContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.ConversationToolbarPresenterImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.viewmodel.ConversationViewModel
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.design_dialogs.fragment.AlertDialogFragment
import java.util.*

/** @SelfDocumented */
class MessageListSectionFactory {

    companion object {

        fun createMessageListSection(fragment: Fragment,
                                     dialogUuid: UUID,
                                     sectionHolder: ListSectionHolder,
                                     displayErrorDelegate: DisplayErrorDelegate,
                                     onEditMessage: ((messageUuid: UUID) -> Unit)?,
                                     onReplyMessage: ((themeUuid: UUID, messageUuid: UUID, countersUuid: UUID, showKeyboard: Boolean) -> Unit)?,
                                     containerId: Int = 0,
                                     listDateViewUpdaterInitializer: (ListDateViewUpdater) -> Unit
        ): ListSection<in ListItem, MessageListController, *> {
            return MessageListSection.create(fragment, dialogUuid, sectionHolder, displayErrorDelegate, onEditMessage, onReplyMessage, containerId, listDateViewUpdaterInitializer)
        }
    }
}

/**
 * Секция списка сообщений
 *
 * @param sectionHolder - холдер секции
 * @param adapter - адаптер списка сообщений
 * @param controller - презентер экрана переписки
 * @param toolbarController - презентер тулбара экрана переписки
 * @param displayErrorDelegate - делегат для отображения ошибок
 * @param conversationViewDelegate - делегат экрана переписки
 */
internal class MessageListSection(private val sectionHolder: ListSectionHolder,
                                  adapter: MessagesListAdapter,
                                  controller: ConversationMessagesContract.Presenter<ConversationMessagesContract.View>,
                                  private val toolbarController: ConversationToolbarContract.Presenter<ConversationToolbarContract.View>,
                                  displayErrorDelegate: DisplayErrorDelegate,
                                  private val conversationViewDelegate: ConversationViewDelegate
) : ListSection<ListItem, ConversationMessagesContract.Presenter<ConversationMessagesContract.View>, MessagesListAdapter>(controller, adapter),
    ConversationMessagesContract.View by conversationViewDelegate,
    LifecycleObserver,
    DisplayErrorDelegate by displayErrorDelegate,
    AlertDialogFragment.YesNoListener by conversationViewDelegate {

    companion object {
        /** @SelfDocumented */
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun create(fragment: Fragment,
                   dialogUuid: UUID,
                   sectionHolder: ListSectionHolder,
                   displayErrorDelegate: DisplayErrorDelegate,
                   onEditMessage: ((messageUuid: UUID) -> Unit)?,
                   onReplyMessage: ((themeUuid: UUID, messageUuid: UUID, countersUuid: UUID, showKeyboard: Boolean) -> Unit)?,
                   containerId: Int = 0,
                   listDateViewUpdaterInitializer: (ListDateViewUpdater) -> Unit
        ): ListSection<in ListItem, MessageListController, *> {

            val context = fragment.requireContext()
            ConversationListSizeSettings.init(context.applicationContext)

            val messageSectionComponent = DaggerMessageSectionComponent.builder()
                .context(context)
                .conversationOpenData(
                    CoreConversationInfo(dialogUuid,
                        null, null, null,
                        null,
                        null, ConversationType.VIOLATION, false)
                )
                .viewModel(ViewModelProviders.of(fragment)[ConversationViewModel::class.java])
                .viewModelStoreOwner(fragment)
                .sbisConversationSingletonComponent(singletonComponent)
                .build()

            val conversationPresenter = messageSectionComponent.conversationMessagesPresenter
            val conversationToolbarPresenter = messageSectionComponent.conversationToolbarPresenter
            val messageActionsDelegate = messageSectionComponent.conversationMessagePanelPresenter
            val adapter = messageSectionComponent.adapter
            listDateViewUpdaterInitializer.invoke(messageSectionComponent.dateViewUpdater)

            val conversationViewDelegate = ConversationViewDelegate(
                context,
                sectionHolder,
                conversationPresenter as ConversationMessagesPresenter,
                conversationToolbarPresenter as ConversationToolbarPresenterImpl,
                messageActionsDelegate,
                sectionHolder.getChildFragmentManager(),
                adapter,
                onEditMessage,
                onReplyMessage,
                containerId)

            return MessageListSection(
                sectionHolder,
                adapter,
                conversationPresenter,
                conversationToolbarPresenter,
                displayErrorDelegate,
                conversationViewDelegate) as ListSection<in ListItem, MessageListController, *>
        }
    }

    init {
        MessagesSectionPresenterLifecycleSectionBinder.bind(this, sectionHolder.lifecycle, controller, this)
    }

    /** @SelfDocumented */
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            adapter.onRestoreInstanceState(savedInstanceState)
        }
    }

    /** @SelfDocumented */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adapter.onSavedInstanceState(outState)
    }

    /** @SelfDocumented */
    override fun attachToView() {
        if (!isAttachedToView) {
            controller.attachView(this)
        }
        super.attachToView()
    }

    /** @SelfDocumented */
    override fun detachFromView() {
        if (isAttachedToView) {
            controller.detachView()
            toolbarController.onDestroy()
        }
        super.detachFromView()
    }
}