package ru.tensor.sbis.communicator.communicator_files.ui

import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import kotlinx.coroutines.launch
import ru.tensor.sbis.communicator.base_folders.list_section.FoldersViewHolderHelper
import ru.tensor.sbis.communicator.communicator_files.databinding.CommunicatorFilesFragmentBinding
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesView.Event
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesView.Model
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.communicator.base_folders.ROOT_FOLDER_UUID
import ru.tensor.sbis.communicator.base_folders.list_section.CommunicatorFoldersAction
import ru.tensor.sbis.design.folders.data.model.ROOT_FOLDER_ID
import java.util.UUID

/**
 * @author da.zhukov
 */
internal class CommunicatorFilesViewImpl(
    binding: CommunicatorFilesFragmentBinding,
    listComponentFactory: CommunicatorFilesListComponentFactory,
    foldersViewHolderHelper: FoldersViewHolderHelper
) : BaseMviView<Model, Event>(),
    CommunicatorFilesView {

    private val lifecycleOwner = binding.root.findViewTreeLifecycleOwner()
    private val scope = lifecycleOwner?.lifecycleScope

    override val renderer: ViewRenderer<Model> = diff {
        diff(
            get = Model::folderTitle,
            set = {
                binding.communicatorFilesFolderTitleLayout.setTitle(it)
            }
        )
        diff(
            get = Model::currentFolderViewIsVisible,
            set = {
                binding.communicatorFilesFolderTitleLayout.isVisible = it
                foldersViewHolderHelper.run {
                    if (it) hideFoldersView(true)
                }
            }
        )
    }

    init {
        listComponentFactory.create(
            view = binding.communicatorFilesList.apply {
                list.itemAnimator = null
            }
        )
        binding.communicatorFilesFolderTitleLayout.setOnClickListener {
            foldersViewHolderHelper.folderActionListener.closed()
            foldersViewHolderHelper.showFoldersView(false)
        }

        scope?.launch {
            lifecycleOwner?.lifecycle?.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launchAndCollect(foldersViewHolderHelper.foldersActionFlow) { action ->
                    action.handleFolderActions()
                }
            }
        }
    }

    private fun <T> CoroutineScope.launchAndCollect(flow: Flow<T>, collector: (T) -> Unit) =
        launch { flow.collect(collector) }

    private fun CommunicatorFoldersAction.handleFolderActions() {
        when (this) {
            is CommunicatorFoldersAction.OpenedFolder -> {
                dispatch(Event.FolderClick(UUID.fromString(folder.id), folder.title))
            }
            is CommunicatorFoldersAction.SelectedFolder -> {
                val folderId = if (folder.id == ROOT_FOLDER_ID) ROOT_FOLDER_UUID else UUID.fromString(folder.id)
                dispatch(Event.FolderSelected(folderId))
            }
            is CommunicatorFoldersAction.ClosedFolder -> {
                dispatch(Event.FolderClick())
            }
        }
    }
}