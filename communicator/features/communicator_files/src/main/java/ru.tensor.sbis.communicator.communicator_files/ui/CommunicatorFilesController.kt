package ru.tensor.sbis.communicator.communicator_files.ui

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilter
import ru.tensor.sbis.communicator.base_folders.list_section.FoldersViewHolderHelper
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.result_mediator.MessageUuidMediator
import ru.tensor.sbis.communicator.communicator_files.CommunicatorFilesPlugin.viewerSliderIntentFactoryProvider
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileAction
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileActionData
import ru.tensor.sbis.communicator.communicator_files.store.CommunicatorFilesStore
import ru.tensor.sbis.communicator.communicator_files.store.CommunicatorFilesStoreFactory
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesView.Event
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesView.Model
import ru.tensor.sbis.communicator.communicator_files.utils.CommunicatorFileClickListener
import ru.tensor.sbis.communicator.communicator_files.utils.calculateQuantityOfViews
import ru.tensor.sbis.communicator.communicator_files.viewer.createViewerSliderArgs
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Контроллер, обеспечивающий связку компонентов Android с компонентами MVI файлов переписки.
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    @Assisted viewFactory: (View) -> CommunicatorFilesView,
    private val storeFactory: CommunicatorFilesStoreFactory,
    private val foldersViewHelper: FoldersViewHolderHelper
) : ConversationInformationFilesContent,
    CommunicatorFileClickListener {

    private val store = fragment.provideStore { storeFactory.create(it) }
    private val backButtonClick: () -> Unit = {
        fragment.parentFragmentManager.run {
            if (fragments.last().castTo<FragmentBackPress>()?.onBackPressed() == false) {
                popBackStack()
            }
        }
    }
    private val scope = fragment.lifecycle.coroutineScope

    init {
        with(fragment) {
            attachBinder(BinderLifecycleMode.CREATE_DESTROY, viewFactory) { view ->
                bind {
                    view.events.map(::toIntent) bindTo store
                    store.states.map(::toModel) bindTo view
                    store.labels bindTo { it.consume() }
                }
            }
        }
    }

    private fun toIntent(event: Event): CommunicatorFilesStore.Intent = when (event) {
        is Event.EnterSearchQuery -> CommunicatorFilesStore.Intent.SearchQuery(event.query)
        is Event.SelectedFilter -> CommunicatorFilesStore.Intent.UpdateFilter(event.filterTypes)
        is Event.FolderClick -> CommunicatorFilesStore.Intent.ChangeCurrentFolder(event.id, event.title)
        is Event.FolderSelected -> CommunicatorFilesStore.Intent.MoveToFolder(event.folderId)
        is Event.BackButtonClick -> CommunicatorFilesStore.Intent.BackButtonClick
    }

    private fun toModel(state: CommunicatorFilesStore.State) = Model(
        currentFolderViewIsVisible = state.currentFolderViewIsVisible,
        folderTitle = state.folderTitle
    )

    private fun CommunicatorFilesStore.Label.consume() = when (this) {
        is CommunicatorFilesStore.Label.BackButtonClick -> {
            backButtonClick()
        }
        is CommunicatorFilesStore.Label.ShowActionList -> {
            fragment.castTo<CommunicatorFilesFragment>()?.showAttachmentActionList(
                view,
                actions,
                actionData
            )
        }
        is CommunicatorFilesStore.Label.ShowFile -> {
            val args = createViewerSliderArgs(
                themeUuid = themeUuid,
                folderUuid = folderUuid,
                fileActionData = actionData
            )
            val intent = viewerSliderIntentFactoryProvider.get().createViewerSliderIntent(
                fragment.requireContext(), args
            )
            fragment.startActivity(intent)
        }
        is CommunicatorFilesStore.Label.CopyLink -> {
            ClipboardManager.copyToClipboard(fragment.requireContext(), link)
            fragment.castTo<CommunicatorFilesFragment>()?.showToast(RCommunicatorDesign.string.communicator_link_copied, Toast.LENGTH_LONG)
        }
        is CommunicatorFilesStore.Label.GoToMessage -> {
            MessageUuidMediator().provideResult(fragment, messageId)
        }
        is CommunicatorFilesStore.Label.ShowFolderSelection -> {
            foldersViewHelper.showFolderSelection(currentFolderId)
        }
        is CommunicatorFilesStore.Label.ShowFileSuccessMovedToFolder -> {
            fragment.castTo<CommunicatorFilesFragment>()?.showFileSuccessMoveToFolder()
        }
    }

    fun onFileActionClick(action: CommunicatorFileAction, actionData: CommunicatorFileActionData) {
        scope.launch {
            store.accept(CommunicatorFilesStore.Intent.OnFileActionClick(action, actionData))
        }
    }

    fun onConfigurationChanged(){
        scope.launch {
            store.accept(
                CommunicatorFilesStore.Intent.ConfigurationChanged(
                    fragment.requireContext().applicationContext.calculateQuantityOfViews()
                )
            )
        }
    }

    override fun setFilter(filter: List<ConversationInformationFilter>) {
        scope.launch {
            store.accept(CommunicatorFilesStore.Intent.UpdateFilter(filter))
        }
    }

    override fun setSearchQuery(query: String) {
        scope.launch {
            store.accept(CommunicatorFilesStore.Intent.SearchQuery(query))
        }
    }

    override fun addFiles(selectedFiles: List<SbisPickedItem>, compressImages: Boolean) {
        scope.launch {
            store.accept(CommunicatorFilesStore.Intent.AddFiles(selectedFiles, compressImages))
        }
    }

    override fun createFolder(folderName: String) {
        scope.launch {
            store.accept(CommunicatorFilesStore.Intent.CreateFolder(folderName))
        }
    }

    override fun onLongClick(view: View, actionData: CommunicatorFileActionData): Boolean {
        store.accept(CommunicatorFilesStore.Intent.ShowActionList(view, actionData))
        return true
    }

    override fun onClick(actionData: CommunicatorFileActionData) {
        store.accept(CommunicatorFilesStore.Intent.ShowFile(actionData))
    }
}