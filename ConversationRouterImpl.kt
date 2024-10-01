package ru.tensor.sbis.communicator.sbis_conversation.conversation

import android.annotation.SuppressLint
import android.content.Context
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.android_ext_decl.FragmentTransactionArgs
import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListComponentConfig
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationParams
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.CHAT_SETTINGS_REQUEST_CODE
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.CONVERSATION_VIEWER_SLIDER_CODE
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.DIALOG_PARTICIPANTS_ACTIVITY_CODE
import ru.tensor.sbis.communicator.common.conversation.data.LinkDialogToTask
import ru.tensor.sbis.communicator.common.conversation.data.TaskCreationResult
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction
import ru.tensor.sbis.communicator.common.data.ConversationDetailsParams
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorConversationRouter
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.common.util.message_search.ThemeMessageSearchApi
import ru.tensor.sbis.communicator.declaration.ConversationPreviewMode
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.themesRegistryFragmentFactory
import ru.tensor.sbis.communicator.sbis_conversation.ConversationActivity
import ru.tensor.sbis.communicator.sbis_conversation.conversation.utils.DocumentOpenUtils
import ru.tensor.sbis.communicator.sbis_conversation.preview.ConversationPreviewDialogFragment
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.MessageInformationFeatureFacade
import ru.tensor.sbis.communicator.sbis_conversation.utils.PhoneNumberActionHelper
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.edo_decl.document.Document
import ru.tensor.sbis.edo_decl.document.DocumentType
import ru.tensor.sbis.localfeaturetoggle.data.FeatureSet
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.tasks.feature.DiskAttachment
import ru.tensor.sbis.tasks.feature.TasksCreateFeature
import ru.tensor.sbis.tasks.feature.TasksCreateFeature.CreateMasterArgs.DialogData
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import java.util.*
import ru.tensor.sbis.communicator.sbis_conversation.R
import ru.tensor.sbis.design.R as RDesign

/**
 * Роутер экрана сообщений.
 *
 * @property fragment    фрагмент сообщений.
 * @property containerId идентификатор корневого контейнера.
 *
 * @author vv.chekurda
 */
internal class ConversationRouterImpl(
    private val fragment: BaseFragment,
    private val containerId: Int,
    private val conversationRouter: CommunicatorConversationRouter? = null,
    private val phoneNumberActionHelper: PhoneNumberActionHelper? = null
) : ConversationRouter {

    private val communicatorConversationRouter: CommunicatorConversationRouter by lazy {
        conversationRouter ?: dependency.getCommunicatorConversationRouter()
    }

    private val dependency = singletonComponent.dependency

    private val isTablet: Boolean
        get() = DeviceConfigurationUtils.isTablet(fragment.requireContext())

    private var isExitCalled = false

    private val filesTasksDialogFeatureOn: Boolean
        get() = singletonComponent.featureService?.isActive(FILES_TASKS_DIALOG_CLOUD_FEATURE) == true ||
            singletonComponent.localFeatureService.isFeatureActivated(FeatureSet.FILES_TASKS_DIALOG)

    override fun showProfile(personUuid: UUID) =
        communicatorConversationRouter.showProfile(personUuid)

    override fun showConversation(params: ConversationDetailsParams) {
        communicatorConversationRouter.showConversationDetailsScreen(params)
    }

    @SuppressLint("CommitTransaction")
    override fun showPhoneVerification() {
        val phoneVerificationFragment = dependency.createVerificationFragment()
        fragment.childFragmentManager
            .beginTransaction()
            .doIf(!isTablet) {
                setCustomAnimations(
                    RDesign.anim.right_in,
                    RDesign.anim.left_out,
                    RDesign.anim.left_in,
                    RDesign.anim.right_out
                )
            }
            .add(
                containerId,
                phoneVerificationFragment,
                phoneVerificationFragment::class.java.simpleName
            )
            .addToBackStack(null)
            .commit()
    }

    override fun showConversationPreview(conversationParams: ConversationParams, menuActions: List<ConversationPreviewMenuAction>) {
        ConversationPreviewDialogFragment.create(
            menuActions,
            ConversationPreviewMode.PREVIEW,
            conversationParams
        ).show(this.fragment.parentFragmentManager, fragment::class.java.simpleName)
    }

    override fun prepareSearchMode(conversationUuid: UUID): ThemeMessageSearchApi {
        val themeFragment = themesRegistryFragmentFactory.get().createSearchMessagesThemeFragment(conversationUuid)
        this.fragment.childFragmentManager
            .beginTransaction()
            .add(R.id.communicator_conversation_search, themeFragment, themeFragment::class.java.simpleName)
            .commitNow()

        return themeFragment as ThemeMessageSearchApi
    }

    override fun showRecipientSelection(useCase: RecipientSelectionUseCase) {
        val context = fragment.requireContext()
        val intent = dependency.getRecipientSelectionIntent(context, RecipientSelectionConfig(useCase))
        fragment.startActivity(intent)
    }

    override fun showAddChatParticipants(chatUuid: UUID) {
        val context = fragment.requireContext()
        dependency.chatRecipientSelectionIntentFactory?.also {
            val intent = it.createAddChatParticipantsIntent(context, chatUuid)
            fragment.startActivity(intent)
        }
    }

    @Suppress("DEPRECATION")
    override fun showConversationMembers(
        conversationUuid: UUID,
        subtitle: String,
        isNewDialog: Boolean,
        isChat: Boolean,
        conversationName: String?,
        permissions: Permissions?,
        participantsUuids: ArrayList<UUID>?,
        isGroupConversation: Boolean,
        photoData: List<PhotoData>,
        singleParticipant: ContactVM?
    ) {
        if (filesTasksDialogFeatureOn) {
            showConversationInfo(
                conversationUuid,
                subtitle,
                isNewDialog,
                isChat,
                conversationName ?: StringUtils.EMPTY,
                permissions ?: Permissions(),
                photoData,
                isGroupConversation,
                singleParticipant
            )
        } else {
            val context = fragment.requireContext()
            dependency.conversationParticipantsFactory?.let {
                val intent = it.createConversationParticipantsIntent(
                    context,
                    conversationUuid,
                    isNewDialog,
                    isChat,
                    !isChat,
                    conversationName,
                    permissions,
                    participantsUuids
                )
                fragment.startActivityForResult(intent, DIALOG_PARTICIPANTS_ACTIVITY_CODE)
            }
        }
    }

    @SuppressLint("CommitTransaction")
    private fun showConversationInfo(
        conversationUuid: UUID,
        subtitle: String,
        isNewDialog: Boolean,
        isChat: Boolean,
        conversationName: String,
        permissions: Permissions,
        photoData: List<PhotoData>,
        isGroupConversation: Boolean,
        singleParticipant: ContactVM?
    ) {
        dependency.conversationInformationFactory?.createConversationInformationFragment(
            conversationUuid,
            subtitle,
            isNewDialog,
            isChat,
            conversationName,
            permissions,
            photoData,
            isGroupConversation,
            singleParticipant
        )?.let {
            fragment.childFragmentManager
                .beginTransaction()
                .doIf(!isTablet) {
                    setCustomAnimations(
                        RDesign.anim.right_in,
                        RDesign.anim.left_out,
                        RDesign.anim.left_in,
                        RDesign.anim.right_out
                    )
                }
                .add(containerId, it, it::class.java.simpleName)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun showTaskCreation(
        dialogUuid: UUID,
        linkType: LinkDialogToTask?,
        currentAccountUUID: UUID,
        executors: List<UUID>,
        description: String,
        attachmentsFromUris: List<String>,
        attachmentsFromDisk: List<DiskAttachment>,
        listener: ((TaskCreationResult) -> Unit)?
    ) {
        val tasksCreateFeature = dependency.tasksCreateFeature ?: return
        if (containerId == 0) return
        val linking = when (linkType) {
            LinkDialogToTask.LINK   -> DialogData.Linking.LINK
            LinkDialogToTask.APPEND -> DialogData.Linking.APPEND
            LinkDialogToTask.ASK    -> DialogData.Linking.ASK
            null                    -> null
        }
        val createMasterArgs = TasksCreateFeature.CreateMasterArgs.InFolder(
            folderUuid = null,
            authorFaceUuid = currentAccountUUID,
            presets = TasksCreateFeature.CreateMasterPreset(
                executorsUuids = executors,
                attachmentsFromUris = attachmentsFromUris,
                attachmentsFromDisk = attachmentsFromDisk,
                description = description
            ),
            dialogData = linking?.let { DialogData(dialogUuid, linking) },
            isCreateImmediately = false,
        )
        val fragmentTransactionArgs = FragmentTransactionArgs(
            containerResId = containerId,
            fragmentTag = "TASK_CREATE_MASTER",
            backStackName = "TASK_CREATE_MASTER"
        )
        tasksCreateFeature.createTasksCreateMasterFragmentTransaction(
            createMasterArgs,
            fragment.childFragmentManager,
            fragmentTransactionArgs,
            listener = { result ->
                listener?.invoke(
                    when (result) {
                        TasksCreateFeature.ExitResult.SUCCESS -> TaskCreationResult.SUCCESS
                        TasksCreateFeature.ExitResult.CANCELLED -> TaskCreationResult.CANCELLED
                        TasksCreateFeature.ExitResult.ERROR -> TaskCreationResult.ERROR
                    }
                )
            }
        ).commit()
    }

    override fun showMessageInformationScreen(dialogUuid: UUID, messageUuid: UUID, groupDialog: Boolean, isChannel: Boolean) {
        if (isTablet) {
            val messageInformationFragment = MessageInformationFeatureFacade.createMessageInformationFragment(
                dialogUuid,
                messageUuid,
                groupDialog,
                isChannel
            )
            fragment.childFragmentManager
                .beginTransaction()
                .add(
                    containerId,
                    messageInformationFragment,
                    messageInformationFragment::class.java.simpleName
                )
                .addToBackStack(null)
                .commit()
        } else {
            MessageInformationFeatureFacade.createIntent(
                fragment.requireContext(),
                dialogUuid,
                messageUuid,
                groupDialog,
                isChannel
            ).let {
                fragment.startActivity(it)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun showChatSettings(conversationUuid: UUID?, isNewChat: Boolean, isDraft: Boolean) {
        val context = fragment.requireContext()
        dependency.chatSettingsIntentFactory?.let {
            val intent = it.createChatSettingsIntent(context, conversationUuid, isNewChat, isDraft)
            fragment.startActivityForResult(intent, CHAT_SETTINGS_REQUEST_CODE)
        }
    }

    @Suppress("DEPRECATION")
    override fun exit() {
        if (isExitCalled) return
        isExitCalled = true

        val activity = fragment.requireActivity()
        when {
            activity is ConversationActivity -> activity.finish()
            isTablet -> {
                if (communicatorConversationRouter.canPopBackStack()) {
                    activity.onBackPressed()
                } else {
                    communicatorConversationRouter.removeSubContent()
                }
            }
            else -> activity.onBackPressed()
        }
        activity.overridePendingTransition(RDesign.anim.nothing, RDesign.anim.right_out)
    }

    override fun showDocument(document: Document) {
        when {
            DocumentOpenUtils.isWorkPlanDocument(document) -> DocumentOpenUtils.showIncomingDocument(document)
            document.type === DocumentType.TASK -> communicatorConversationRouter.showTask(document.uuid)
            else -> DocumentOpenUtils.showDocument(fragment.requireContext(), document)
        }
    }

    override fun openWebView(title: String, url: String) {
        val context = fragment.requireContext()
        dependency.docWebViewerFeature?.showDocumentLink(context, title, url)
    }

    @Suppress("DEPRECATION")
    override fun showViewerSlider(args: ViewerSliderArgs) {
        val context = fragment.requireContext()
        dependency.viewerSliderIntentFactory?.let {
            val intent = it.createViewerSliderIntent(context, args)
            fragment.startActivityForResult(intent, CONVERSATION_VIEWER_SLIDER_CODE)
        }
    }

    override fun showFolder(attachmentListComponentConfig: DefAttachmentListComponentConfig) {
        val context = fragment.requireContext()
        dependency.attachmentListViewerIntentFactory?.let {
            context.startActivity(it.newAttachmentListViewerIntent(context, attachmentListComponentConfig))
        }
    }

    override fun openLink(url: String) {
        communicatorConversationRouter.showLinkInWebView(url)
    }

    override fun openTask(uuid: String) {
        communicatorConversationRouter.showTask(uuid)
    }

    override fun runWithContext(action: (Context) -> Unit) {
        action(fragment.requireContext())
    }

    override fun callTheNumber(phoneNumber: String) {
        phoneNumberActionHelper?.tryCallTheNumber(phoneNumber)
    }

    override fun addNumberToPhoneBook(phoneNumber: String) {
        phoneNumberActionHelper?.addNumberToPhoneBook(phoneNumber)
    }

    override fun dialPhoneNumber(phoneNumber: String) {
        phoneNumberActionHelper?.dialPhoneNumber(phoneNumber)
    }

    override fun changeRegistrySelectedItem(conversationUuid: UUID) {
        communicatorConversationRouter.changeRegistrySelectedItem(conversationUuid)
    }
}

private const val FILES_TASKS_DIALOG_CLOUD_FEATURE = "files_tasks_dialog"