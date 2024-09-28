package ru.tensor.sbis.app_file_browser.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.tensor.sbis.app_file_browser.feature.AppFileBrowserFeatureInternal
import ru.tensor.sbis.crud3.ItemWithSection
import ru.tensor.sbis.crud3.ListComponentViewViewModel
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.mfb.generated.FileInfo
import ru.tensor.sbis.mfb.generated.Filter
import ru.tensor.sbis.mfb.generated.MobileFileController
import ru.tensor.sbis.swipeablelayout.util.SwipeHelper
import java.util.LinkedList

/**
 * Вьюмодель экрана файлового браузера.
 *
 * @author us.bessonov
 */
internal class AppFileBrowserViewModel(
    private val feature: AppFileBrowserFeatureInternal,
    private val listComponentViewModel: ListComponentViewViewModel<ItemWithSection<AnyItem>, Filter, FileInfo>
) : ViewModel() {

    private val path = LinkedList<String>()
    private val controller: MobileFileController get() = feature.controller
    val currentFolder = MutableLiveData("")
    val isCurrentFolderVisible = MutableLiveData(false)

    fun onFolderClicked(file: FileInfo) {
        path.add(file.name)
        reset()
        updateCurrentFolder()
    }

    fun onSelectionChanged(file: FileInfo) {
        controller.changeSelected(file.path)
        feature.onSelectionChanged(controller.getSelectedFiles())
    }

    fun onShowItemSize(file: FileInfo) {
        controller.calculateDirSize(file.path)
        SwipeHelper.closeAll()
    }

    fun onDeleteItem(file: FileInfo) {
        controller.delete(file.path)
        SwipeHelper.findSwipeableLayoutByUuid(file.id.toString())?.dismiss()
    }

    fun onGoBackClicked() {
        path.removeLastOrNull()
        reset()
        updateCurrentFolder()
    }

    private fun reset() = listComponentViewModel.reset(Filter(ArrayList(path)))

    private fun updateCurrentFolder() {
        isCurrentFolderVisible.value = path.isNotEmpty()
        currentFolder.value = path.lastOrNull().orEmpty()
    }
}