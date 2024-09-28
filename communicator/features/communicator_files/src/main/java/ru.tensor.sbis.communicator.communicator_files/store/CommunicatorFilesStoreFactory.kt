package ru.tensor.sbis.communicator.communicator_files.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import ru.tensor.sbis.attachments.decl.attachment_list.data.DocumentParams
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilter
import ru.tensor.sbis.communicator.communicator_files.CommunicatorFilesPlugin.addAttachmentsUseCaseProvider
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileAction
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileAction.*
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileActionData
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFilesFilterHolder
import ru.tensor.sbis.communicator.communicator_files.store.CommunicatorFilesStore.Intent
import ru.tensor.sbis.communicator.communicator_files.store.CommunicatorFilesStore.Label
import ru.tensor.sbis.communicator.communicator_files.store.CommunicatorFilesStore.State
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesListComponentFactory
import ru.tensor.sbis.communicator.generated.AttachmentOrigin
import ru.tensor.sbis.communicator.generated.ThemeAttachmentController
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.disk.decl.params.DiskDocumentParams
import ru.tensor.sbis.mvi_extension.create
import java.util.UUID

/**
 * Фабрика стора файлов переписки.
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesStoreFactory(
    private val storeFactory: StoreFactory,
    private val listComponentFactory: CommunicatorFilesListComponentFactory,
    private val filterHolder: CommunicatorFilesFilterHolder,
) {

    /** @SelfDocumented */
    fun create(stateKeeper: StateKeeper): CommunicatorFilesStore =
        object : CommunicatorFilesStore,
    Store<Intent, State, Label> by storeFactory.create(
        stateKeeper = stateKeeper,
        name = COMMUNICATOR_FILES_STORE_NAME,
        initialState = State(),
        bootstrapper = SimpleBootstrapper(),
        executorFactory = {
            ExecutorImpl(
                listComponentFactory,
                filterHolder
            )
        } ,
        reducer = ReducerImpl()
    ) {}

    private sealed interface Action

    private sealed interface Message {
        data class UpdateCurrentFolderViewTitle(val folderTitle: String) : Message
    }

    private class ExecutorImpl(
        private val listComponentFactory: CommunicatorFilesListComponentFactory,
        private val filter: CommunicatorFilesFilterHolder
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        private val attachmentController by lazy { ThemeAttachmentController.instance() }
        private var selectedFileIdToMove: UUID? = null

        override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
            is Intent.InitialLoading -> {
                listComponentFactory.get()?.reset()
                Unit
            }
            is Intent.OnFileActionClick -> {
                onFileActionClick(intent.action, intent.actionData)
            }
            is Intent.ConfigurationChanged -> {
                listComponentFactory.get()?.resetForce(filter().apply {
                    rowItemCount = intent.quantityOfViews
                })
                Unit
            }
            is Intent.ChangeCurrentFolder -> {
                filter.setFolderId(intent.id)
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateCurrentFolderViewTitle(intent.title))
            }
            is Intent.ShowActionList -> {
                publish(
                    Label.ShowActionList(
                        view = intent.view,
                        actions = prepareActionList(intent.actionData),
                        actionData = intent.actionData
                    )
                )
            }
            is Intent.ShowFile -> {
                publish(Label.ShowFile(filter().themeId, filter().folderId, intent.actionData))
            }
            is Intent.MoveToFolder -> {
                moveFileToFolder(intent.folderId)
            }
            is Intent.SearchQuery -> {
                onChangeQuery(intent.query)
            }
            is Intent.UpdateFilter -> {
                onChangeFilter(intent.filterTypes)
            }
            is Intent.BackButtonClick -> {
                publish(Label.BackButtonClick)
            }
            is Intent.AddFiles -> {
                addFiles(intent.selectedFiles, intent.compressImages)
            }
            is Intent.CreateFolder -> {
                createFolder(intent.folderName)
            }
        }

        private fun prepareActionList(actionData: CommunicatorFileActionData): List<CommunicatorFileAction> {
            return buildList {
                if (actionData.attachmentOrigin == AttachmentOrigin.PINNED) add(UNPIN) else add(PIN)
                add(COPY_LINK)
                if (actionData.messageId != null) add(GO_TO_MESSAGE)
                add(MOVE_TO_FOLDER)
                add(DELETE)
            }
        }

        private fun onChangeQuery(newQuery: String) {
            if (filter.invoke().searchQuery != newQuery) {
                filter.setQuery(newQuery)
                listComponentFactory.get()?.reset()
            }
        }

        private fun onChangeFilter(filterTypes: List<ConversationInformationFilter>) {
            filter.setFilters(filterTypes)
            listComponentFactory.get()?.reset()
        }

        private fun onFileActionClick(action: CommunicatorFileAction, actionData: CommunicatorFileActionData) {
            val themeId = filter().themeId
            when (action) {
                PIN -> scope.launch { attachmentController.pin(themeId, actionData.fileId) }
                UNPIN -> scope.launch { attachmentController.unpin(themeId, actionData.fileId) }
                DELETE -> scope.launch { attachmentController.detachFile(themeId, actionData.fileId) }
                COPY_LINK -> publish(Label.CopyLink(actionData.attachmentLink))
                MOVE_TO_FOLDER -> {
                    selectedFileIdToMove = actionData.fileId
                    publish(Label.ShowFolderSelection(filter().folderId))
                }
                GO_TO_MESSAGE -> actionData.messageId?.let { publish(Label.GoToMessage(it)) }
            }
        }

        private fun moveFileToFolder(folderId: UUID?) {
            scope.launch {
                async {
                    selectedFileIdToMove?.let {
                        attachmentController.moveAttachment(
                            themeId = filter().themeId,
                            attachmentId = it,
                            folderId = folderId
                        )
                    }
                }.await()?.handleActionsResults {
                    selectedFileIdToMove = null
                    publish(Label.ShowFileSuccessMovedToFolder)
                }
            }
        }

        private fun CommandStatus.handleActionsResults(onSuccess: ()-> Unit) {
            when (this.errorCode) {
                ErrorCode.SUCCESS -> { onSuccess() }
                else -> {}
            }
        }

        private fun createFolder(folderName: String) {
            scope.launch(Dispatchers.IO) {
                attachmentController.createFolder(filter().themeId, folderName, filter().folderId)
            }
        }

        private fun addFiles(selectedFiles: List<SbisPickedItem>, compressImages: Boolean) {
            scope.launch(Dispatchers.IO) {
                val localUris = mutableListOf<String>()
                val diskDocParams = mutableListOf<DiskDocumentParams>()
                selectedFiles.forEach {
                    when (it) {
                        is SbisPickedItem.LocalFile -> {
                            localUris.add(it.uri)
                        }
                        is SbisPickedItem.DiskDocument -> {
                            diskDocParams.add(it.params)
                        }
                        else-> Unit
                    }
                }
                // сначала необходимо прикрепить файлы к переписки через диск
                addAttachmentsUseCaseProvider.get().addAttachmentsWithResult(
                    params = DocumentParams(
                        blObjectName = UrlUtils.FILE_SD_OBJECT,
                        id = filter().themeId,
                        null
                    ),
                    uriList = localUris,
                    diskDocumentParamsList = diskDocParams,
                    compressImages = compressImages
                ).toFlowable().asFlow().collect {
                    attachmentController.attachFiles(
                        themeId = filter().themeId,
                        folderId =filter().folderId,
                        files = it.asArrayList()
                    )
                }
            }
        }
    }

    private class ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is Message.UpdateCurrentFolderViewTitle -> copy(
                folderTitle = msg.folderTitle,
                currentFolderViewIsVisible = msg.folderTitle.isNotEmpty()
            )
        }
    }
}
const val COMMUNICATOR_FILES_STORE_NAME = "CommunicatorFilesStore"