package ru.tensor.sbis.communicator.themes_registry.di.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.folders.ThemeFoldersInteractor
import ru.tensor.sbis.design.folders.support.FoldersViewModel
import javax.inject.Inject

/**
 * Фабрика вьюмодели [FoldersViewModel]. Необходима для создания вьюмодели через di
 *
 * @author rv.krohalev
 */
internal class DialogFoldersViewModelFactory @Inject constructor(
    private val foldersProvider: ThemeFoldersInteractor,
) : ViewModelProvider.Factory {

    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        @Suppress("UNCHECKED_CAST") // always success
        return FoldersViewModel(foldersProvider, openFolderByClick = false) as VM
    }
}
