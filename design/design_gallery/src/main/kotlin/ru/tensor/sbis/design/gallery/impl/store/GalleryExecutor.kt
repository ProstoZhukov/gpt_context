package ru.tensor.sbis.design.gallery.impl.store

import android.net.Uri
import androidx.core.net.toUri
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.barcode_decl.barcodereader.Barcode
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.design.files_picker.decl.CropParams
import ru.tensor.sbis.design.files_picker.decl.GallerySelectionMode
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design.gallery.decl.GalleryConfig
import ru.tensor.sbis.design.gallery.decl.GalleryMode
import ru.tensor.sbis.design.gallery.impl.GalleryRepository
import ru.tensor.sbis.design.gallery.impl.GalleryRepository.Companion.ALL_MEDIA_ALBUM_ID
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryAction
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryAlbumItem
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryIntent
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryItem
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryLabel
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryMessage
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryState
import javax.inject.Inject
import ru.tensor.sbis.design.gallery.R
import ru.tensor.sbis.design.gallery.impl.utils.logAttachProcess

/**
 * Реализация mvi-сущности Executor
 *
 * @author ia.nikitin
 */
internal class GalleryExecutor @Inject constructor(
    private val repository: GalleryRepository,
    private val config: GalleryConfig,
    private val fileUriUtil: FileUriUtil,
    private val resourcesProvider: ResourceProvider,
) : CoroutineExecutor<GalleryIntent, GalleryAction, GalleryState, GalleryMessage, GalleryLabel>() {

    companion object {
        const val TO_BYTES_COEFFICIENT = 1024 * 1024
    }

    private var mainJob: Job? = null

    override fun executeAction(action: GalleryAction, getState: () -> GalleryState) {
        when (action) {
            is GalleryAction.CheckPermissions -> safePublish(GalleryLabel.CheckPermissions)
        }
    }

    override fun executeIntent(
        intent: GalleryIntent,
        getState: () -> GalleryState
    ) {
        val currentState = getState()
        when (intent) {
            is GalleryIntent.StoragePermissionDenied ->
                subscribe(getState, showCameraStub = false, showStorageStub = true)

            is GalleryIntent.CameraPermissionDenied ->
                subscribe(getState, showCameraStub = true, showStorageStub = false)

            is GalleryIntent.LoadItems -> subscribe(getState, showCameraStub = false, showStorageStub = false)
            is GalleryIntent.AllPermissionsDenied -> dispatch(GalleryMessage.ShowStub())
            is GalleryIntent.BackPressed -> dispatch(GalleryMessage.ShowAlbumsList())
            is GalleryIntent.AlbumClicked -> dispatch(GalleryMessage.ShowAlbumContent(intent.id))
            is GalleryIntent.ItemClicked -> createViewerSliderShowLabel(currentState, intent.id)
            is GalleryIntent.CloseViewer -> applyViewerSliderSelectionResult(currentState, intent.ids)
            is GalleryIntent.ClearSelection -> clearSelectionForCurrentAlbum(currentState)
            is GalleryIntent.SelectionConfirmed -> onSelectionConfirmed(currentState, intent.itemsIds)
            is GalleryIntent.ItemCheckboxClicked -> onItemCheckboxClicked(currentState, intent.id)
            is GalleryIntent.UpdateCameraSnapshotUri -> dispatch(GalleryMessage.UpdateCameraSnapshotUri(intent.uri))
            is GalleryIntent.OpenCamera -> openCamera()
            is GalleryIntent.SnapshotTaken -> onSnapshotTaken(currentState)
            is GalleryIntent.OnBarcodeScannerResult -> onBarcodeScannerResult(currentState, intent)
            is GalleryIntent.CancelButtonClicked -> cancelSelection(currentState)
            is GalleryIntent.RequestCameraPermission -> safePublish(GalleryLabel.CameraStubClicked)
            is GalleryIntent.RequestStoragePermission -> safePublish(GalleryLabel.StorageStubClicked)
            is GalleryIntent.RequestPermissions -> safePublish(GalleryLabel.MainStubClicked)
            is GalleryIntent.FilesPickerAddButtonClicked -> prepareCropImage(currentState)
        }
    }

    private fun subscribe(
        getState: () -> GalleryState,
        showCameraStub: Boolean,
        showStorageStub: Boolean
    ) {
        mainJob?.cancel()

        mainJob = scope.launch {
            if (showStorageStub) {
                dispatch(
                    GalleryMessage.InitializeContent(
                        albums = mapOf(),
                        showStorageStub = true,
                        barTitle = resourcesProvider.getString(R.string.design_gallery_album_all_media)
                    )
                )
            } else {
                repository.getGalleryItems().collect {
                    when (val currentState = getState()) {
                        is GalleryState.Loading,
                        is GalleryState.Stub -> {
                            when (config.mode) {
                                is GalleryMode.AllMedia ->
                                    dispatch(
                                        GalleryMessage.InitializeContent(
                                            albums = it,
                                            showCameraStub = showCameraStub,
                                            barTitle = null
                                        )
                                    )

                                is GalleryMode.ByAlbums ->
                                    dispatch(
                                        GalleryMessage.InitializeContent(
                                            albums = it,
                                            barTitle = resourcesProvider.getString(
                                                R.string.design_gallery_album_all_media
                                            )
                                        )
                                    )
                            }
                        }

                        is GalleryState.Content -> {
                            val allMedia = it[ALL_MEDIA_ALBUM_ID]
                            if (allMedia != null) {
                                safePublish(
                                    GalleryLabel.ItemsSelected(
                                        currentState.selectedItemsIds.getItemsForIds(allMedia)
                                    )
                                )
                            } else {
                                illegalState { "All media album can't be null" }
                            }
                            dispatch(
                                GalleryMessage.UpdateContent(
                                    albums = it,
                                    showCameraStub = showCameraStub,
                                    showStorageStub = false,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onItemCheckboxClicked(currentState: GalleryState, id: Int) {
        when (config.selectionMode) {
            is GallerySelectionMode.Multiple -> selectItemForMultipleMode(currentState, id)
            is GallerySelectionMode.Single -> selectItemForSingleMode(currentState, id)
        }
    }

    private fun selectItemForSingleMode(currentState: GalleryState, id: Int) {
        if (currentState is GalleryState.Content) {
            if (currentState.type is GalleryState.Content.Type.Media) {
                val selectedItemsIds = currentState.selectedItemsIds
                val indexOfClickedItem = currentState.type.items.indexOfFirst { it.id == id }
                if (indexOfClickedItem != -1) {
                    if (selectedItemsIds.isEmpty()) {
                        selectedItemsIds[id] = currentState.type.albumId
                        currentState.type.items[indexOfClickedItem].selectionNumber.value = 1
                    } else if (selectedItemsIds.keys.contains(id)) {
                        currentState.type.items[indexOfClickedItem].selectionNumber.value = 0
                        selectedItemsIds.clear()
                    } else {
                        val indexOfSelectedItem =
                            currentState.type.items.indexOfFirst { it.id == selectedItemsIds.keys.first() }
                        currentState.type.items[indexOfSelectedItem].selectionNumber.value = 0
                        selectedItemsIds.clear()
                        selectedItemsIds[id] = currentState.type.albumId
                        currentState.type.items[indexOfClickedItem].selectionNumber.value = 1
                    }
                    handleItems(currentState, selectedItemsIds.keys.toList()) { items ->
                        safePublish(GalleryLabel.ItemsSelected(items))
                    }
                    updateAddButtonState(currentState)
                } else {
                    illegalState { "Unexpected clicked item with id- $id" }
                }
            }
        }
    }

    private fun selectItemForMultipleMode(currentState: GalleryState, id: Int) {
        if (currentState is GalleryState.Content) {
            val selectedItemsIds = currentState.selectedItemsIds
            var removed = false
            if (selectedItemsIds.remove(id) != null) {
                removed = true
            } else {
                if (currentState.type is GalleryState.Content.Type.Media) {
                    val limit = config.selectionMode.limit
                    val itemFileSizeLimit = config.sizeInMBytesLimit
                    if (selectedItemsIds.size >= limit) {
                        safePublish(GalleryLabel.SelectionLimit(limit))
                        return
                    }
                    if (itemFileSizeLimit != null) {
                        val itemFileSize = currentState.type.getFileSizeForId(id)
                        if (itemFileSize != null && itemFileSize > itemFileSizeLimit * TO_BYTES_COEFFICIENT) {
                            safePublish(GalleryLabel.SizeLimit(itemFileSizeLimit))
                            return
                        }
                    }
                    selectedItemsIds[id] = currentState.type.albumId
                }
            }

            if (currentState.type is GalleryState.Content.Type.Media) {
                if (removed) {
                    val indexOfUnselectedItem = currentState.type.items.indexOfFirst { it.id == id }
                    if (indexOfUnselectedItem != -1) {
                        currentState.type.items[indexOfUnselectedItem].selectionNumber.value = null
                    } else {
                        illegalState { "Unexpected id - $id" }
                    }
                }
                selectedItemsIds.keys.forEachIndexed { index, itemId ->
                    val indexOfSelectedItem = currentState.type.items.indexOfFirst { it.id == itemId }
                    if (indexOfSelectedItem != -1) {
                        currentState.type.items[indexOfSelectedItem].selectionNumber.value = index + 1
                    }
                }
            }

            handleItems(currentState, selectedItemsIds.keys.toList()) { items ->
                safePublish(GalleryLabel.ItemsSelected(items))
            }

            updateAddButtonState(currentState)
        } else {
            illegalState { "Unexpected state - $currentState" }
        }
    }

    private fun updateAddButtonState(currentState: GalleryState.Content) {
        if (config.mode is GalleryMode.ByAlbums) {
            if (currentState.selectedItemsIds.isNotEmpty()) {
                dispatch(GalleryMessage.UpdateAddButtonStatus(true))
            } else {
                dispatch(GalleryMessage.UpdateAddButtonStatus(false))
            }
        }
    }

    private fun onSelectionConfirmed(currentState: GalleryState, itemsIds: List<Int>?) {
        if (currentState is GalleryState.Content) {
            if ((config.selectionMode as? GallerySelectionMode.Single?)?.imageCropParams != null) {
                prepareCropImage(currentState, itemsIds)
            } else {
                handleItems(currentState, itemsIds ?: currentState.selectedItemsIds.keys.toList()) { items ->
                    safePublish(GalleryLabel.AddButtonClicked(items))
                }
            }
        }
    }

    private fun handleItems(
        currentState: GalleryState,
        itemsIds: List<Int>,
        action: (items: List<GalleryItem>) -> Unit
    ) {
        if (currentState is GalleryState.Content) {
            val allMedia = currentState.albums[ALL_MEDIA_ALBUM_ID]
            if (allMedia != null) {
                val items: List<GalleryItem> = itemsIds.getItemsForIds(allMedia)
                action.invoke(items)
            }
        } else {
            illegalState { "Unexpected state - $currentState" }
        }
    }

    private fun cancelSelection(currentState: GalleryState) {
        clearSelection(currentState)
        safePublish(GalleryLabel.CancelButtonClicked)
    }

    private fun clearSelection(currentState: GalleryState) {
        with(currentState) {
            if (this is GalleryState.Content) {
                val allMedia = albums[ALL_MEDIA_ALBUM_ID]
                if (allMedia != null) {
                    selectedItemsIds.getItemsForIds(allMedia).forEach { item ->
                        item.selectionNumber.value = null
                    }
                    selectedItemsIds.clear()
                    dispatch(GalleryMessage.ShowAlbumContent(allMedia.id))
                }
            }
        }
    }

    private fun clearSelectionForCurrentAlbum(currentState: GalleryState) {
        with(currentState) {
            if (this is GalleryState.Content) {
                if (type is GalleryState.Content.Type.Media) {
                    albums[type.albumId]?.let {
                        selectedItemsIds.getItemsForIds(it).forEach { item ->
                            item.selectionNumber.value = null
                        }
                    }
                    selectedItemsIds.clear()
                    safePublish(GalleryLabel.ItemsSelected(listOf()))
                }
            }
        }
    }

    private fun openCamera() {
        val cameraSnapshotUri = fileUriUtil.generateSnapshotUri()
        logAttachProcess("openCamera, uri - $cameraSnapshotUri")
        dispatch(GalleryMessage.UpdateCameraSnapshotUri(cameraSnapshotUri.toUri()))
        safePublish(GalleryLabel.OpenCamera)
    }

    private fun onSnapshotTaken(currentState: GalleryState) {
        logAttachProcess("onSnapshotTaken, uri - ${(currentState as? GalleryState.Content)?.cameraSnapshotUri}")
        if (currentState is GalleryState.Content && currentState.cameraSnapshotUri != null) {
            if (cropParams != null) {
                safePublish(GalleryLabel.CropImage(uri = currentState.cameraSnapshotUri, cropParams = cropParams))
            } else {
                safePublish(
                    GalleryLabel.SnapshotTaken(SbisPickedItem.LocalFile(currentState.cameraSnapshotUri.toString()))
                )
            }
            dispatch(GalleryMessage.UpdateCameraSnapshotUri(null))
        } else {
            illegalState { "Unexpected state - $currentState" }
        }
    }

    private fun onBarcodeScannerResult(currentState: GalleryState, intent: GalleryIntent.OnBarcodeScannerResult) {
        if (currentState is GalleryState.Content) {
            val barcodeValue = intent.barcodeValue
            val barcodeSymbology = intent.barcodeSymbology
            val uri = intent.fileUri
            if (barcodeValue != null && barcodeSymbology != null) {
                safePublish(
                    GalleryLabel.OnBarcodeScannerResult(
                        SbisPickedItem.Barcode(
                            Barcode(barcodeValue, barcodeSymbology), uri
                        )
                    )
                )
            } else {
                safePublish(GalleryLabel.SnapshotTaken(SbisPickedItem.LocalFile(uri)))
            }
        }
    }

    private fun createViewerSliderShowLabel(currentState: GalleryState, id: Int) {
        if (currentState is GalleryState.Content) {
            if (currentState.type is GalleryState.Content.Type.Media) {
                safePublish(
                    GalleryLabel.ShowViewerSlider(
                        id,
                        currentState.type.items,
                        currentState.selectedItemsIds.keys.toList(),
                        config.selectionMode.limit
                    )
                )
            } else {
                illegalState { "Unexpected state - $currentState" }
            }
        } else {
            illegalState { "Unexpected state - $currentState" }
        }
    }

    private fun applyViewerSliderSelectionResult(currentState: GalleryState, ids: List<Int>) {
        if (currentState is GalleryState.Content) {
            if (currentState.type is GalleryState.Content.Type.Media) {
                val changedItems =
                    currentState.selectedItemsIds.keys.filterNot { id -> id in ids } +
                        ids.filterNot { id -> id in currentState.selectedItemsIds.keys }
                changedItems.forEach { id -> onItemCheckboxClicked(currentState, id) }
            }
        }
    }

    private fun prepareCropImage(currentState: GalleryState, itemsIds: List<Int>? = null) {
        if (currentState is GalleryState.Content) {
            val allMedia = currentState.albums[ALL_MEDIA_ALBUM_ID]
            if (allMedia != null) {
                val items: List<GalleryItem> = (itemsIds ?: currentState.selectedItemsIds.keys).getItemsForIds(allMedia)
                if (items.size == 1 && !items.first().isVideo && cropParams != null) {
                    publish(GalleryLabel.CropImage(uri = Uri.parse(items.first().uri), cropParams = cropParams))
                } else {
                    illegalState { "Unexpected item (${items.first()}) or cropParams ($cropParams)" }
                }
            }
        } else {
            illegalState { "Unexpected state - $currentState" }
        }
    }

    private val cropParams: CropParams? = (config.selectionMode as? GallerySelectionMode.Single)?.imageCropParams

    private val GallerySelectionMode.limit: Int
        get() = when (this) {
            is GallerySelectionMode.Single -> 1
            is GallerySelectionMode.Multiple -> selectionLimit
        }

    private fun safePublish(label: GalleryLabel) {
        scope.launch {
            withContext(Dispatchers.Main) {
                publish(label)
            }
        }
    }

    private fun GalleryState.Content.Type.Media.getFileSizeForId(id: Int): Long? {
        val indexOfItem = items.indexOfFirst { it.id == id }
        return if (indexOfItem != -1) {
            items[indexOfItem].size
        } else {
            illegalState { "Unexpected id - $id" }
            null
        }
    }
}

internal fun Map<Int, Int>.getItemsForIds(album: GalleryAlbumItem): List<GalleryItem> = this.keys.getItemsForIds(album)

internal fun Collection<Int>.getItemsForIds(album: GalleryAlbumItem): List<GalleryItem> {
    val items = mutableListOf<GalleryItem>()
    this.forEach { id ->
        album.items.find { it.id == id }?.let {
            items.add(it)
        }
    }
    return items
}