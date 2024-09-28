package ru.tensor.sbis.communicator.contacts_registry.di.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.communicator.contacts_registry.ui.folders.ContactListFoldersInteractor
import ru.tensor.sbis.design.folders.support.FoldersViewModel
import javax.inject.Inject

/**
 * Фабрика вьюмодели [FoldersViewModel]. Необходима для создания вьюмодели через di
 *
 * @author ao.zanin
 */
internal class ContactFoldersViewModelFactory @Inject constructor(
    private val foldersProvider: ContactListFoldersInteractor
) : ViewModelProvider.Factory {

    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        @Suppress("UNCHECKED_CAST") // always success
        return FoldersViewModel(foldersProvider, openFolderByClick = false) as VM
    }
}
