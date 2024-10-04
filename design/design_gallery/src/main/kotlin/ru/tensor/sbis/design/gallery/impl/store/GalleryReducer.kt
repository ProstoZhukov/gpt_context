package ru.tensor.sbis.design.gallery.impl.store

import com.arkivanov.mvikotlin.core.store.Reducer
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.design.gallery.impl.GalleryRepository
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryAlbumItem
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryBarConfig
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryMessage
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryState
import ru.tensor.sbis.design.gallery.impl.utils.logAttachProcess
import javax.inject.Inject

internal class GalleryReducer @Inject constructor() : Reducer<GalleryState, GalleryMessage> {
    override fun GalleryState.reduce(msg: GalleryMessage): GalleryState =
        when (msg) {
            is GalleryMessage.InitializeContent -> {
                if (msg.showStorageStub) {
                    GalleryState.Content(
                        albums = msg.albums,
                        type = GalleryState.Content.Type.Media(
                            items = listOf(),
                            albumId = GalleryRepository.ALL_MEDIA_ALBUM_ID
                        ),
                        barConfig = GalleryBarConfig(msg.barTitle ?: "", true),
                        selectedItemsIds = mutableMapOf(),
                        isEnabledAddButton = false,
                        cameraSnapshotUri = null,
                        cameraStubVisible = msg.showCameraStub,
                        storageStubVisible = true
                    )
                } else {
                    val allMediaAlbum = msg.albums[GalleryRepository.ALL_MEDIA_ALBUM_ID]
                    if (allMediaAlbum != null) {
                        val barConfig = GalleryBarConfig(title = allMediaAlbum.name, hasBackArrow = true)
                        if (this is GalleryState.Loading || this is GalleryState.Stub) {
                            GalleryState.Content(
                                albums = msg.albums,
                                type = GalleryState.Content.Type.Media(
                                    items = allMediaAlbum.items,
                                    albumId = GalleryRepository.ALL_MEDIA_ALBUM_ID
                                ),
                                barConfig = barConfig,
                                selectedItemsIds = mutableMapOf(),
                                isEnabledAddButton = false,
                                cameraSnapshotUri = null,
                                cameraStubVisible = msg.showCameraStub,
                                storageStubVisible = false
                            )
                        } else {
                            illegalState { "Unexpected state" }
                            this
                        }
                    } else {
                        illegalState { "All media album could be not null" }
                        this
                    }
                }
            }
            is GalleryMessage.UpdateContent ->
                ifContent {
                    it.copy(
                        cameraStubVisible = msg.showCameraStub,
                        storageStubVisible = msg.showStorageStub,
                        albums = msg.albums,
                        type =
                        when (it.type) {
                            is GalleryState.Content.Type.Media -> {
                                val currentAlbum = msg.albums[it.type.albumId]
                                if (currentAlbum != null) {
                                    val selectedItems = it.selectedItemsIds.getItemsForIds(currentAlbum)
                                    var selectionNumber = 1
                                    selectedItems.forEach { item ->
                                        item.selectionNumber.value = selectionNumber
                                        selectionNumber++
                                    }
                                    it.type.copy(items = currentAlbum.items)
                                } else {
                                    illegalState { "Current album can't be null" }
                                    it.type
                                }
                            }
                            is GalleryState.Content.Type.Albums -> it.type.copy(items = it.albums.values.toList())
                        }
                    )
                }
            is GalleryMessage.ShowAlbumsList ->
                ifContent {
                    it.copy(type = GalleryState.Content.Type.Albums(it.albums.values.toList()))
                }
            is GalleryMessage.ShowAlbumContent -> {
                ifContent {
                    val clickedAlbum: GalleryAlbumItem? = it.albums[msg.clickedAlbumId]
                    if (clickedAlbum != null) {
                        it.copy(
                            type = GalleryState.Content.Type.Media(clickedAlbum.items, clickedAlbum.id),
                            barConfig = GalleryBarConfig(
                                title = clickedAlbum.name,
                                hasBackArrow = true
                            )
                        )
                    } else {
                        illegalState { "Album could be not null" }
                        this
                    }
                }
            }
            is GalleryMessage.UpdateAddButtonStatus ->
                ifContent {
                    it.copy(isEnabledAddButton = msg.isEnabled)
                }
            is GalleryMessage.UpdateCameraSnapshotUri ->
                ifContent {
                    logAttachProcess("updateUri, uri - ${msg.uri}")
                    it.copy(cameraSnapshotUri = msg.uri)
                }
            is GalleryMessage.ShowStub -> GalleryState.Stub()
        }

    private fun GalleryState.ifContent(action: (GalleryState.Content) -> GalleryState): GalleryState =
        if (this !is GalleryState.Content) {
            illegalState { "Unexpected state" }
            this
        } else {
            action(this)
        }
}