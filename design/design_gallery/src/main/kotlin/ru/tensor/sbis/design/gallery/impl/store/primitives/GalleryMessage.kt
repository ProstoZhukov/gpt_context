package ru.tensor.sbis.design.gallery.impl.store.primitives

import android.net.Uri

/**
 * Реализация mvi-сущности Message
 */
internal sealed interface GalleryMessage {

    /** @SelfDocumented */
    data class InitializeContent(
        val albums: Map<Int, GalleryAlbumItem>,
        val showCameraStub: Boolean = false,
        val showStorageStub: Boolean = false,
        val barTitle: String?
    ) : GalleryMessage

    /** @SelfDocumented */
    data class UpdateContent(
        val albums: Map<Int, GalleryAlbumItem>,
        val showCameraStub: Boolean,
        val showStorageStub: Boolean
    ) : GalleryMessage

    /** Обновление uri для снимка с камеры */
    data class UpdateCameraSnapshotUri(val uri: Uri?) : GalleryMessage

    /** Отобразить содержание альбома */
    data class ShowAlbumContent(val clickedAlbumId: Int) : GalleryMessage

    /** Обновить видимость кнопок "Отменить" и "Добавить" */
    data class UpdateAddButtonStatus(val isEnabled: Boolean) : GalleryMessage

    /** Отобразить список альбомов */
    class ShowAlbumsList : GalleryMessage

    /** Отобразить заглушку */
    class ShowStub : GalleryMessage
}