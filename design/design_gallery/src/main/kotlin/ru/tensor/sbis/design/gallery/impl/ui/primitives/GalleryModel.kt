package ru.tensor.sbis.design.gallery.impl.ui.primitives

import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryBarConfig

/**
 * Реализация mvi-сущности Model
 */
internal sealed interface GalleryModel {

    sealed class Content : GalleryModel {

        class Media(
            val items: List<UniversalBindingItem>,
            val barConfig: GalleryBarConfig,
            val isEnabledAddButton: Boolean
        ) : Content() {
            val hasNoStubs: Boolean = items.none { it is GalleryStorageStub || it is GalleryCameraStub }
            val hasStorageStub: Boolean = items.any { it is GalleryStorageStub }
            val hasNoItems: Boolean = items.none { it is GalleryItemVM }
        }

        class Albums(
            val items: List<GalleryAlbumItemVM>,
            val isEnabledAddButton: Boolean
        ) : Content()
    }

    object Loading : GalleryModel

    object Stub : GalleryModel
}