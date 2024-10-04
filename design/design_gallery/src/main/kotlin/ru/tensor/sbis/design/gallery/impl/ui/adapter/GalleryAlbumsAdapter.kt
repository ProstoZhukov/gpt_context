package ru.tensor.sbis.design.gallery.impl.ui.adapter

import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.base_components.adapter.universal.UniversalViewHolder
import ru.tensor.sbis.design.gallery.BR
import ru.tensor.sbis.design.gallery.impl.ui.GalleryClickHandler
import ru.tensor.sbis.design.gallery.R

internal class GalleryAlbumsAdapter(
    private val galleryClickHandler: GalleryClickHandler
) : GalleryAdapter() {

    companion object {
        const val GALLERY_ALBUM_VIEW_TYPE = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversalViewHolder<UniversalBindingItem> =
        when (viewType) {
            GALLERY_ALBUM_VIEW_TYPE -> UniversalViewHolder(
                createBinding(R.layout.design_gallery_album_item, parent),
                BR.clickHandler,
                galleryClickHandler
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
}