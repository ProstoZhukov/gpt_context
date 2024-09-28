package ru.tensor.sbis.app_file_browser.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.app_file_browser.feature.AppFileBrowserFeatureInternal
import ru.tensor.sbis.crud3.ItemWithSection
import ru.tensor.sbis.crud3.ListComponentViewViewModel
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.mfb.generated.FileInfo
import ru.tensor.sbis.mfb.generated.Filter

/**
 * @SelfDocumented
 *
 * @author us.bessonov
 */
internal class AppFileBrowserViewModelFactory(
    private val feature: AppFileBrowserFeatureInternal,
    private val listComponentViewModel: ListComponentViewViewModel<ItemWithSection<AnyItem>, Filter, FileInfo>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppFileBrowserViewModel(feature, listComponentViewModel) as T
    }
}