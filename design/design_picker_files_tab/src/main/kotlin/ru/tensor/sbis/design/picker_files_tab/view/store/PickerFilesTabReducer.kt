package ru.tensor.sbis.design.picker_files_tab.view.store

import com.arkivanov.mvikotlin.core.store.Reducer
import ru.tensor.sbis.design.picker_files_tab.view.di.PickerFilesTabDIScope
import javax.inject.Inject

/**
 * MVI Reducer для экрана "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
@PickerFilesTabDIScope
internal class PickerFilesTabReducer @Inject constructor() :
    Reducer<PickerFilesTabStore.State, PickerFilesTabStore.Message> {

    override fun PickerFilesTabStore.State.reduce(msg: PickerFilesTabStore.Message): PickerFilesTabStore.State = this
}