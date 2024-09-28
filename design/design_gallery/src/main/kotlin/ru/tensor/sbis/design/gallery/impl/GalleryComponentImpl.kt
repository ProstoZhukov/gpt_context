package ru.tensor.sbis.design.gallery.impl

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.MenuItemState
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.files_picker.decl.AddButtonClickAction
import ru.tensor.sbis.design.files_picker.decl.GallerySelectionMode
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabClickAction
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabEvent
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeature
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabSettings
import ru.tensor.sbis.design.gallery.R
import ru.tensor.sbis.design.gallery.decl.GalleryComponent
import ru.tensor.sbis.design.gallery.decl.GalleryConfig
import ru.tensor.sbis.design.gallery.decl.GalleryEvent
import ru.tensor.sbis.design.gallery.decl.GalleryMode
import ru.tensor.sbis.design.gallery.impl.ui.GalleryFragment
import ru.tensor.sbis.design.gallery.impl.utils.logAttachProcess
import ru.tensor.sbis.design.tab_panel.DefaultTabPanelItem
import ru.tensor.sbis.design.tab_panel.TabPanelItem

internal class GalleryComponentImpl(
    tab: SbisFilesPickerTab.Gallery
) :
    SbisFilesPickerTabFeature<SbisFilesPickerTab.Gallery>, GalleryComponent, ViewModel() {

    companion object {

        fun from(tab: SbisFilesPickerTab.Gallery, storeOwner: ViewModelStoreOwner): GalleryComponentImpl =
            ViewModelProvider(
                owner = storeOwner,
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        GalleryComponentImpl(tab) as T
                }
            )[GalleryComponentImpl::class.java]
    }

    val addButtonEvent: MutableSharedFlow<Unit> = MutableSharedFlow()

    override val tabPanelItem: TabPanelItem =
        DefaultTabPanelItem(
            id = "SbisFilesPickerTab.Gallery",
            icon = SbisMobileIcon.Icon.smi_gallery,
            title = R.string.design_gallery_tab_title
        )

    override val clickAction: SbisFilesPickerTabClickAction =
        SbisFilesPickerTabClickAction.ShowFragment {
            createFragment(
                GalleryConfig(
                    mode = GalleryMode.AllMedia(),
                    selectionMode = tab.selectionMode,
                    cameraType = tab.cameraType,
                    sizeInMBytesLimit = tab.fileSizeInMBytesLimit,
                    needOnlyImages = tab.needOnlyImages,
                    isNeedBottomPadding = true
                )
            )
        }

    override val addButtonCustomClickAction: AddButtonClickAction? =
        if ((tab.selectionMode as? GallerySelectionMode.Single)?.imageCropParams != null) {
            { viewModelScope.launch { addButtonEvent.emit(Unit) } }
        } else {
            null
        }

    private val _events = MutableSharedFlow<GalleryEvent>()
    override val events: Flow<GalleryEvent> by ::_events

    private val _event = MutableSharedFlow<SbisFilesPickerTabEvent>(replay = 1)
    override val event: Flow<SbisFilesPickerTabEvent> by ::_event

    private val menuItems = mutableListOf<MenuItem>()
    private val withCompressMenuItem =
        MenuItem(
            title = GalleryPlugin.application.getString(R.string.design_gallery_compress_menu_item_compress),
            state = MenuItemState.ON,
            handler = {
                val withCompressMenuItem = menuItems[0]
                val withoutCompressMenuItem = menuItems[1]
                if (withCompressMenuItem.state == MenuItemState.ON) {
                    withCompressMenuItem.state = MenuItemState.MIXED
                    withoutCompressMenuItem.state = MenuItemState.ON
                } else {
                    withCompressMenuItem.state = MenuItemState.ON
                    withoutCompressMenuItem.state = MenuItemState.MIXED
                }
                sendLastOnItemsSelectedEvent()
            }
        )
    private val withoutCompressMenuItem =
        MenuItem(
            title = GalleryPlugin.application.getString(R.string.design_gallery_compress_menu_item_no_compress),
            state = MenuItemState.MIXED,
            handler = {
                val withCompressMenuItem = menuItems[0]
                val withoutCompressMenuItem = menuItems[1]
                if (withoutCompressMenuItem.state == MenuItemState.ON) {
                    withoutCompressMenuItem.state = MenuItemState.MIXED
                    withCompressMenuItem.state = MenuItemState.ON
                } else {
                    withoutCompressMenuItem.state = MenuItemState.ON
                    withCompressMenuItem.state = MenuItemState.MIXED
                }
                sendLastOnItemsSelectedEvent()
            }
        )
    override val tabSettings: SbisFilesPickerTabSettings =
        SbisFilesPickerTabSettings(menu = SbisMenu(children = menuItems))
    private var lastOnItemsSelectedEvent: SbisFilesPickerTabEvent.OnItemsSelected? = null

    init {
        menuItems.add(withCompressMenuItem)
        menuItems.add(withoutCompressMenuItem)
    }

    override fun createFragment(config: GalleryConfig): Fragment = GalleryFragment.newInstance(config)

    fun sendEvent(event: GalleryEvent) {
        viewModelScope.launch { _events.emit(event) }
        when (event) {
            is GalleryEvent.OnFilesSelected ->
                sendOnItemsSelectedEvent(
                    SbisFilesPickerTabEvent.OnItemsSelected(
                        selectedItems = event.selectedFiles,
                        compressImages = isNeedCompressImages(),
                        pushRightNow = false
                    )
                )
            is GalleryEvent.OnCameraSnapshotSuccess -> {
                logAttachProcess("sendOnItemsSelectedEvent, uri - ${event.snapshot.uri}")
                sendOnItemsSelectedEvent(
                    SbisFilesPickerTabEvent.OnItemsSelected(
                        selectedItems = listOf(event.snapshot),
                        compressImages = isNeedCompressImages(),
                        pushRightNow = true
                    )
                )
            }
            is GalleryEvent.OnAddButtonClick ->
                sendOnItemsSelectedEvent(
                    SbisFilesPickerTabEvent.OnItemsSelected(
                        selectedItems = event.selectedFiles,
                        compressImages = isNeedCompressImages(),
                        pushRightNow = true
                    )
                )
            is GalleryEvent.OnCancelButtonClick -> Unit
            is GalleryEvent.OnImageCropped ->
                sendOnItemsSelectedEvent(
                    SbisFilesPickerTabEvent.OnItemsSelected(
                        selectedItems = listOf(event.croppedImage),
                        compressImages = isNeedCompressImages(),
                        pushRightNow = true
                    )
                )
            is GalleryEvent.OnBarcodeScannerResult ->
                sendOnItemsSelectedEvent(
                    SbisFilesPickerTabEvent.OnItemsSelected(
                        selectedItems = listOf(event.barcode),
                        compressImages = isNeedCompressImages(),
                        pushRightNow = true
                    )
                )
        }
    }

    private fun isNeedCompressImages(): Boolean = withCompressMenuItem.state == MenuItemState.ON

    private fun sendOnItemsSelectedEvent(event: SbisFilesPickerTabEvent.OnItemsSelected) {
        lastOnItemsSelectedEvent = event
        viewModelScope.launch { _event.emit(event) }
    }

    private fun sendLastOnItemsSelectedEvent() {
        lastOnItemsSelectedEvent?.actualizeCompressImagesStatus()?.let(::sendOnItemsSelectedEvent)
    }

    private fun SbisFilesPickerTabEvent.OnItemsSelected.actualizeCompressImagesStatus() =
        SbisFilesPickerTabEvent.OnItemsSelected(
            selectedItems = selectedItems,
            compressImages = isNeedCompressImages(),
            pushRightNow = pushRightNow
        )
}