package ru.tensor.sbis.design.files_picker.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPicker
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerFactory

/**
 * Реализация фабрики для создания фичи Выбор файла.
 *
 * @author ai.abramenko
 */
internal class SbisFilesPickerFactoryImpl : SbisFilesPickerFactory {

    @Suppress("UNCHECKED_CAST")
    override fun createSbisFilesPicker(
        viewModelStoreOwner: ViewModelStoreOwner,
        key: String?
    ): SbisFilesPicker {
        return ViewModelProvider(
            viewModelStoreOwner,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    SbisFilesPickerImpl(key, viewModelStoreOwner.javaClass) as T
            }
        )
            .run {
                if (key != null) {
                    get(key, SbisFilesPickerImpl::class.java)
                } else {
                    get(SbisFilesPickerImpl::class.java)
                }
            }
    }
}