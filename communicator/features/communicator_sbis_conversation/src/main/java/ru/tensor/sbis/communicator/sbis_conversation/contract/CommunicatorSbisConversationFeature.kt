package ru.tensor.sbis.communicator.sbis_conversation.contract

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ParcelUuid
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListItem
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSection
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSectionHolder
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communication_decl.communicator.ConversationToolbarEventProvider
import ru.tensor.sbis.communication_decl.communicator.event.ConversationToolbarEvent
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationParams
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationProvider
import ru.tensor.sbis.communication_decl.communicator.ui.DialogCreationParams
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.ACTION_CONVERSATION_ACTIVITY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_ARCHIVED_CONVERSATION
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_ARE_RECIPIENTS_SELECTED
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_CHAT_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_CONVERSATION_ARG
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_FOLDER_UUID_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_IS_GROUP_CONVERSATION
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_MESSAGE_UUID_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_PARTICIPANTS_UUIDS_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.EXTRA_CONVERSATION_ACTIVITY_DIALOG_UUID_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.EXTRA_CONVERSATION_ACTIVITY_DOCUMENT_KEY
import ru.tensor.sbis.communicator.common.conversation.ConversationPrefetchManager
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter
import ru.tensor.sbis.communicator.common.conversation.ConversationRouterProvider
import ru.tensor.sbis.communicator.common.conversation.ConversationToolbarEventManager
import ru.tensor.sbis.communicator.common.conversation.ConversationToolbarEventManagerProvider
import ru.tensor.sbis.communicator.common.conversation.utils.pool.ConversationViewPoolInitializer
import ru.tensor.sbis.communicator.common.util.doIfNotNull
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction
import ru.tensor.sbis.communicator.declaration.MessageListController
import ru.tensor.sbis.communicator.declaration.MessageListSectionProvider
import ru.tensor.sbis.communicator.sbis_conversation.ConversationActivity
import ru.tensor.sbis.communicator.sbis_conversation.DialogCreationActivity
import ru.tensor.sbis.communicator.sbis_conversation.conversation.ConversationRouterImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationFragmentFactory
import ru.tensor.sbis.communicator.sbis_conversation.di.singleton.CommunicatorSbisConversationSingletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.preview.ConversationPreviewDialogFragment
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewFragmentFactory
import ru.tensor.sbis.communicator.declaration.ConversationPreviewMode
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagelistsection.MessageListSectionFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.ConversationToolbarEventManagerImpl
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.edo_decl.document.Document
import ru.tensor.sbis.edo_decl.document.DocumentType
import ru.tensor.sbis.mvp.presenter.DisplayErrorDelegate
import java.util.UUID
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationFragment as CrudConversationFragment

/**
 * Api модуля переписки сбис
 * @see ConversationRouterProvider
 * @see ConversationProvider
 * @see MessageListSectionProvider
 * @see ConversationToolbarEventProvider
 * @see ConversationToolbarEventManagerProvider
 *
 * @author vv.chekurda
 */
interface CommunicatorSbisConversationFeature :
    ConversationRouterProvider,
    ConversationProvider,
    MessageListSectionProvider,
    ConversationToolbarEventProvider,
    ConversationToolbarEventManagerProvider,
    ConversationPrefetchManager.Provider,
    ConversationViewPoolInitializer,
    ConversationPreviewFragmentFactory

/**
 * Реализаия фичи экрана переписки сбис
 */
internal class CommunicatorSbisConversationFeatureImpl(
    private val holder: CommunicatorSbisConversationSingletonComponent.Holder
) : CommunicatorSbisConversationFeature {

    private val singletonComponent get() = holder.communicatorSbisConversationSingletonComponent

    internal val conversationFactory: ConversationFragmentFactory
        get() = CrudConversationFragment.Companion


    override fun getConversationRouter(fragment: BaseFragment, containerId: Int): ConversationRouter =
        ConversationRouterImpl(fragment, containerId)


    override fun create(
        menuItems: List<ConversationPreviewMenuAction>,
        mode: ConversationPreviewMode,
        params: ConversationParams
    ): DialogFragment = ConversationPreviewDialogFragment.create(menuItems, mode, params)

    override fun getConversationActivityIntent(
        dialogUuid: UUID?,
        messageUuid: UUID?,
        folderUuid: UUID?,
        participantsUuids: ArrayList<UUID>?,
        files: ArrayList<Uri>?,
        text: String?,
        document: Document?,
        type: ConversationType?,
        isChat: Boolean?,
        archivedDialog: Boolean?,
        isGroupConversation: Boolean?
    ): Intent =
        Intent(singletonComponent.context, ConversationActivity::class.java)
            .setAction(ACTION_CONVERSATION_ACTIVITY)
            .doIfNotNull(dialogUuid) { putExtra(EXTRA_CONVERSATION_ACTIVITY_DIALOG_UUID_KEY, dialogUuid) }
            .doIfNotNull(messageUuid) { putExtra(CONVERSATION_ACTIVITY_MESSAGE_UUID_KEY, messageUuid) }
            .doIfNotNull(folderUuid) { putExtra(CONVERSATION_ACTIVITY_FOLDER_UUID_KEY, folderUuid) }
            .doIfNotNull(files) { putExtra(Intent.EXTRA_STREAM, files) }
            .doIfNotNull(text) { putExtra(Intent.EXTRA_TEXT, text) }
            .doIfNotNull(type) { putExtra(EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY, type) }
            .doIfNotNull(document) { putExtra(EXTRA_CONVERSATION_ACTIVITY_DOCUMENT_KEY, document) }
            .doIfNotNull(isChat) { putExtra(CONVERSATION_ACTIVITY_CHAT_KEY, isChat) }
            .doIfNotNull(archivedDialog) { putExtra(CONVERSATION_ACTIVITY_ARCHIVED_CONVERSATION, archivedDialog) }
            .doIfNotNull(isGroupConversation) {
                putExtra(
                    CONVERSATION_ACTIVITY_IS_GROUP_CONVERSATION,
                    isGroupConversation
                )
            }
            .doIfNotNull(participantsUuids) {
                putParcelableArrayListExtra(
                    CONVERSATION_ACTIVITY_PARTICIPANTS_UUIDS_KEY,
                    UUIDUtils.toParcelUuids(participantsUuids) as ArrayList<ParcelUuid>
                )
            }

    override fun getConversationActivityIntent(extras: Bundle): Intent =
        Intent(singletonComponent.context, ConversationActivity::class.java)
            .setAction(ACTION_CONVERSATION_ACTIVITY)
            .putExtras(extras)

    override fun getConversationActivityIntentForDocument(
        documentUuid: String,
        documentType: DocumentType,
        documentTitle: String?,
        startNewDialogAnyway: Boolean
    ): Observable<Intent> {
        val document = Document().apply {
            id = -1
            uuid = documentUuid
            type = documentType
            title = documentTitle ?: StringUtils.EMPTY
        }
        return if (startNewDialogAnyway) {
            Observable.just(
                getDialogCreationActivityIntent(
                    document = document,
                    type = ConversationType.DOCUMENT_CONVERSATION
                )
            )
        } else {
            singletonComponent.dialogControllerDependencyProvider.async.map {
                it.getDocumentDialogIfExists(UUID.fromString(document.uuid))
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    if (it.data != null) {
                        getConversationActivityIntent(
                            it.data,
                            null,
                            null,
                            null,
                            null,
                            null,
                            document,
                            ConversationType.DOCUMENT_CONVERSATION,
                            isChat = false,
                            archivedDialog = false
                        )
                    } else {
                        getDialogCreationActivityIntent(
                            document = document,
                            type = ConversationType.DOCUMENT_CONVERSATION
                        )
                    }
                }
        }
    }

    override fun getNewDialogConversationActivityIntent(
        folderUuid: UUID?,
        files: ArrayList<Uri>?,
        text: String?,
        areRecipientsSelected: Boolean
    ): Intent {
        return getConversationActivityIntent(
            null,
            null,
            folderUuid,
            null,
            files,
            text,
            null,
            null,
            false,
            null
        ).apply {
            putExtra(CONVERSATION_ACTIVITY_ARE_RECIPIENTS_SELECTED, areRecipientsSelected)
        }
    }

    override fun getDialogCreationActivityIntent(
        folderUuid: UUID?,
        document: Document?,
        type: ConversationType?
    ): Intent =
        Intent(singletonComponent.context, DialogCreationActivity::class.java)
            .doIfNotNull(folderUuid) { putExtra(CONVERSATION_ACTIVITY_FOLDER_UUID_KEY, folderUuid) }
            .doIfNotNull(document) { putExtra(EXTRA_CONVERSATION_ACTIVITY_DOCUMENT_KEY, document) }
            .doIfNotNull(type) { putExtra(EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY, type) }

    override fun getConversationFragment(
        dialogUuid: UUID?,
        messageUuid: UUID?,
        folderUuid: UUID?,
        participantsUuids: ArrayList<UUID>?,
        files: ArrayList<Uri>?,
        text: String?,
        document: Document?,
        type: ConversationType?,
        isChat: Boolean,
        archivedDialog: Boolean
    ): Fragment {
        return conversationFactory.createConversationFragment(
            dialogUuid,
            messageUuid,
            folderUuid,
            participantsUuids,
            files,
            text,
            document,
            type,
            isChat,
            archivedDialog
        )
    }

    override fun getConversationFragment(arguments: Bundle): Fragment {
        return conversationFactory.createConversationFragment(arguments)
    }

    override fun getMessageListSection(
        fragment: Fragment,
        dialogUuid: UUID,
        sectionHolder: ListSectionHolder,
        displayErrorDelegate: DisplayErrorDelegate,
        onEditMessage: ((UUID) -> Unit)?,
        onReplayMessage: ((UUID, UUID, UUID, Boolean) -> Unit)?,
        containerId: Int,
        listDateViewUpdaterInitializer: (ListDateViewUpdater) -> Unit
    ): ListSection<in ListItem, MessageListController, *> =
        MessageListSectionFactory.createMessageListSection(
            fragment,
            dialogUuid,
            sectionHolder,
            displayErrorDelegate,
            onEditMessage,
            onReplayMessage,
            containerId,
            listDateViewUpdaterInitializer
        )

    override fun getConversationToolbarEventObservable(): Observable<ConversationToolbarEvent> =
        singletonComponent.dependency.getConversationToolbarEventManager().getConversationToolbarEventObservable()

    override fun getConversationToolbarEventManager(): ConversationToolbarEventManager =
        ConversationToolbarEventManagerImpl()

    override val prefetchManager: ConversationPrefetchManager
        get() = singletonComponent.conversationPrefetchManager

    override fun initViewPool(fragment: Fragment) {
        singletonComponent.conversationViewPoolController.initViewPool(fragment)
    }

    override fun getConversationActivityIntent(arg: ConversationParams): Intent {
        val action: String?
        val activityClass = if (arg is DialogCreationParams) {
            action = null
            DialogCreationActivity::class.java
        } else {
            action = ACTION_CONVERSATION_ACTIVITY
            ConversationActivity::class.java

        }
        return Intent(singletonComponent.context, activityClass)
            .doIfNotNull(action) { setAction(ACTION_CONVERSATION_ACTIVITY) }
            .putExtra(CONVERSATION_ACTIVITY_CONVERSATION_ARG, arg)
    }

    override fun getConversationFragment(arg: ConversationParams): Fragment {
        return conversationFactory.createConversationFragment(arg)
    }
}