package ru.tensor.sbis.communicator.base_folders.list_section

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.design.folders.support.FoldersProvider
import ru.tensor.sbis.design.folders.support.FoldersViewModel

/** @SelfDocumented */
class BaseListFoldersViewModelFactory(
    private val foldersProvider: FoldersProvider,
    private val openFolderByClick: Boolean = true
) : ViewModelProvider.Factory {

    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        @Suppress("UNCHECKED_CAST") // always success
        return FoldersViewModel(foldersProvider, openFolderByClick) as VM
    }
}