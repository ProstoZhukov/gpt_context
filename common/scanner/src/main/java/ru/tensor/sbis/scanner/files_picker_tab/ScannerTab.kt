package ru.tensor.sbis.scanner.files_picker_tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import io.reactivex.disposables.SerialDisposable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.communication_decl.analytics.model.ScanningDocuments
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabClickAction
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabEvent
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeature
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design.tab_panel.DefaultTabPanelItem
import ru.tensor.sbis.design.tab_panel.TabPanelItem
import ru.tensor.sbis.edo_decl.scanner.ScannerResult
import ru.tensor.sbis.scanner.R
import ru.tensor.sbis.scanner.ScannerPlugin
import ru.tensor.sbis.scanner.data.ScannerEventManager
import ru.tensor.sbis.scanner.ui.DocumentScannerActivity

internal const val FILES_PICKER_SCANNER_REQUEST_CODE = "files_picker_scanner_extra"

internal class ScannerTab : ViewModel(), SbisFilesPickerTabFeature<SbisFilesPickerTab.Scanner> {

    companion object {

        fun from(storeOwner: ViewModelStoreOwner): ScannerTab =
            ViewModelProvider(storeOwner)[ScannerTab::class.java]
    }

    private val scannerEventManager: ScannerEventManager
        get() = ScannerPlugin.singletonComponent.scannerEventManager

    override val tabPanelItem: TabPanelItem =
        DefaultTabPanelItem(
            id = "SbisFilesPickerTab.Scanner",
            icon = SbisMobileIcon.Icon.smi_Nomenclature,
            title = R.string.scanner_files_picker_tab_title,
            isUnmarked = true
        )

    override val clickAction: SbisFilesPickerTabClickAction =
        SbisFilesPickerTabClickAction.Custom {
            ScannerPlugin.analyticsUtil?.sendAnalytics(ScanningDocuments(ScannerTab::class.java.simpleName))
            scannerEventManager.getResult(FILES_PICKER_SCANNER_REQUEST_CODE)?.let {
                sendResult(it)
            }
                ?: kotlin.run {
                    scannerResultDisposable.set(
                        scannerEventManager.scannerResultObservable(
                            FILES_PICKER_SCANNER_REQUEST_CODE
                        )
                            .subscribe { result ->
                                if (result.requestCode == FILES_PICKER_SCANNER_REQUEST_CODE) {
                                    sendResult(result)
                                }
                            }
                    )
                    it.startActivity(
                        DocumentScannerActivity.getActivityIntent(
                            it.requireContext(),
                            FILES_PICKER_SCANNER_REQUEST_CODE
                        )
                    )
                }
        }

    private val _event = MutableSharedFlow<SbisFilesPickerTabEvent>()
    override val event: Flow<SbisFilesPickerTabEvent> by ::_event

    private val scannerResultDisposable = SerialDisposable()

    private fun sendResult(result: ScannerResult) {
        viewModelScope.launch {
            _event.emit(result.toEvent())
        }
    }

    private fun ScannerResult.toEvent(): SbisFilesPickerTabEvent =
        if (scannedUriList.isNotEmpty()) {
            SbisFilesPickerTabEvent.OnItemsSelected(scannedUriList.toLocalFiles(), pushRightNow = true)
        } else {
            SbisFilesPickerTabEvent.Cancel
        }

    private fun List<String>.toLocalFiles() =
        map {
            SbisPickedItem.LocalFile(it)
        }

    override fun onCleared() {
        scannerResultDisposable.dispose()
    }
}