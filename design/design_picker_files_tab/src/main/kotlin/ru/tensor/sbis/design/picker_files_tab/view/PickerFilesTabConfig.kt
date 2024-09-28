package ru.tensor.sbis.design.picker_files_tab.view

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab

/**
 * Конфигурация экрана "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
@Parcelize
internal class PickerFilesTabConfig(val tab: SbisFilesPickerTab.Files) : Parcelable