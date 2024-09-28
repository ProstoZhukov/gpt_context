package ru.tensor.sbis.design.picker_files_tab.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeature
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabClickAction
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabEvent
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design.files_picker.feature.waitingEmit
import ru.tensor.sbis.design.tab_panel.DefaultTabPanelItem
import ru.tensor.sbis.design.tab_panel.TabPanelItem
import ru.tensor.sbis.design.picker_files_tab.R
import ru.tensor.sbis.design.picker_files_tab.view.PickerFilesTabConfig
import ru.tensor.sbis.design.picker_files_tab.view.PickerFilesTabFragment

internal class PickerFilesTabFeature(
    private val tab: SbisFilesPickerTab.Files
) : SbisFilesPickerTabFeature<SbisFilesPickerTab.Files>, ViewModel() {

    override val tabPanelItem: TabPanelItem =
        DefaultTabPanelItem(
            id = "SbisFilesPickerTab.Files",
            icon = SbisMobileIcon.Icon.smi_ReportPublication,
            title = R.string.picker_files_tab_title
        )

    override val clickAction: SbisFilesPickerTabClickAction =
        SbisFilesPickerTabClickAction.ShowFragment {
            PickerFilesTabFragment.newInstance(PickerFilesTabConfig(tab))
        }

    override val event: MutableSharedFlow<SbisFilesPickerTabEvent> = MutableSharedFlow()

    fun onForcedSelection(files: List<SbisPickedItem>) {
        viewModelScope.launch {
            event.waitingEmit(
                SbisFilesPickerTabEvent.OnItemsSelected(
                    selectedItems = files,
                    pushRightNow = true
                )
            )
        }
    }

    var isControlsVisible: Boolean = false
        set(value) {
            field = value
            viewModelScope.launch {
                event.waitingEmit(SbisFilesPickerTabEvent.SwitchControlsVisibility(value))
            }
        }
}