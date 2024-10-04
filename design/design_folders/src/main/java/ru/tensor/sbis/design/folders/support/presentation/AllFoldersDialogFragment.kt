package ru.tensor.sbis.design.folders.support.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.design.folders.R
import ru.tensor.sbis.design.folders.data.model.FolderActionType.CREATE
import ru.tensor.sbis.design.folders.data.model.FolderActionType.RENAME
import ru.tensor.sbis.design.folders.databinding.DesignFoldersMovablePanelBinding
import ru.tensor.sbis.design.folders.support.FoldersViewModel
import ru.tensor.sbis.design.folders.support.utils.viewModelKey
import ru.tensor.sbis.design.folders.view.full.FolderListView
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.errorSafe
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import timber.log.Timber

/**
 * Реализация диалога для отображения [FolderListView] в шторке
 *
 * @author ma.kolpakov
 */
internal class AllFoldersDialogFragment : Fragment(), Content {

    private val panelVisible = MovablePanelPeekHeight.FitToContent()
    private val panelInvisible = MovablePanelPeekHeight.Percent(0f)

    private val viewDisposable = CompositeDisposable()

    private val foldersViewModel by lazy { getViewModel() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DesignFoldersMovablePanelBinding.inflate(inflater, container, false).apply {
            designFoldersMovablePanel.setPeekHeightList(listOf(panelInvisible, panelVisible), panelVisible)
            designFoldersMovablePanel.isBehaviorLocked = true

            // Скрываем фрагмент, если убрали шторку
            designFoldersMovablePanel.getPanelStateSubject().subscribe(
                {
                    if (it == MovablePanelPeekHeight.Percent(value = 0.0f)) {
                        foldersViewModel.onHideFoldersPanel()
                    }
                },
                Timber::e
            )

            inflater.inflate(
                R.layout.design_folders_fragment_all_folders,
                designFoldersMovablePanel.contentContainer,
                true
            )
            designFoldersMovablePanel.foldersPanel.setActionHandler(foldersViewModel.folderActionHandler)
            designFoldersMovablePanel.foldersPanel.setSelectedFolder(foldersViewModel.selectedFolderId.value)

            viewDisposable.add(
                foldersViewModel.folderActionHandler
                    .folderAction
                    .filter { it.actionType == CREATE || it.actionType == RENAME }
                    // почему-то клавиатура не опускается при отрытой шторке
                    .subscribe({ KeyboardUtils.hideKeyboard(requireView()) }, Timber::e)
            )
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view as MovablePanel
        with(foldersViewModel) {
            when (val mode = folderListViewMode.value) {
                FolderListViewMode.DEFAULT -> collapsingFolders
                FolderListViewMode.SELECTION -> selectionFolders
                else -> errorSafe("Unexpected folder view mode $mode") ?: selectionFolders
            }.observe(viewLifecycleOwner, view.foldersPanel::setFolders)
        }
    }

    override fun onDestroyView() {
        viewDisposable.clear()
        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean = false

    override fun onCloseContent() {
        foldersViewModel.onHideFoldersPanel()
    }

    private fun findHostOwner(): ViewModelStoreOwner =
        requireParentFragment().run { parentFragment ?: requireActivity() }

    private fun getViewModel(): FoldersViewModel {
        val vmProvider = ViewModelProvider(findHostOwner())
        return requireArguments().viewModelKey
            ?.let { vmProvider.get(it, FoldersViewModel::class.java) }
            ?: vmProvider[FoldersViewModel::class.java]
    }

    private val MovablePanel.foldersPanel: FolderListView
        get() = with(contentContainer!!) {
            check(childCount == 1) { "Unexpected view count $childCount" }
            getChildAt(0) as FolderListView
        }
}