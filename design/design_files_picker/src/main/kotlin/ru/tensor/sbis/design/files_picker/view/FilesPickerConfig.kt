package ru.tensor.sbis.design.files_picker.view

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab

/**
 * Конфигурация экрана "Компонент выбора файлов".
 *
 * @author ai.abramenko
 */
@Parcelize
internal data class FilesPickerConfig(
    val featureKey: String?,
    val tabs: Set<SbisFilesPickerTab>,
    val featureStoreOwnerClass: Class<Any>
) : Parcelable