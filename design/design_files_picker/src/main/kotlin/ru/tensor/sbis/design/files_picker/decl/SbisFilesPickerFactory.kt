package ru.tensor.sbis.design.files_picker.decl

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика для создания компонента "Выбор файла".
 *
 * @author ai.abramenko
 */
interface SbisFilesPickerFactory : Feature {

    /**
     * Создать компонент "Выбор файла".
     */
    fun createSbisFilesPicker(viewModelStoreOwner: ViewModelStoreOwner, key: String? = null): SbisFilesPicker
}