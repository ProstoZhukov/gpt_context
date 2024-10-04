package ru.tensor.sbis.design.gallery.impl.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.base_components.adapter.universal.UniversalViewHolder
import ru.tensor.sbis.design.gallery.BR
import ru.tensor.sbis.design.gallery.R
import ru.tensor.sbis.design.gallery.impl.ui.GalleryClickHandler

internal class GalleryItemsAdapter(
    private val galleryClickHandler: GalleryClickHandler,
    private val lifecycleOwner: LifecycleOwner
) : GalleryAdapter() {

    companion object {
        const val CAMERA_VIEW_TYPE = 0
        const val GALLERY_ITEM_VIEW_TYPE = 1
        const val GALLERY_STORAGE_STUB_VIEW_TYPE = 2
        const val GALLERY_CAMERA_STUB_VIEW_TYPE = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversalViewHolder<UniversalBindingItem> {
        fun universalGalleryVH(@LayoutRes layoutId: Int): UniversalViewHolder<UniversalBindingItem> =
            UniversalViewHolder(
                createBinding(layoutId, parent).also { it.lifecycleOwner = lifecycleOwner },
                BR.clickHandler,
                galleryClickHandler
            )
        return when (viewType) {
            CAMERA_VIEW_TYPE ->
                GalleryCameraPreviewVH(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.design_gallery_camera_preview_item,
                        parent,
                        false
                    ),
                    BR.clickHandler,
                    galleryClickHandler,
                    ProcessCameraProvider.getInstance(parent.context),
                    lifecycleOwner
                )
            GALLERY_ITEM_VIEW_TYPE -> universalGalleryVH(R.layout.design_gallery_media_item)
            GALLERY_STORAGE_STUB_VIEW_TYPE -> universalGalleryVH(R.layout.design_gallery_storage_stub)
            GALLERY_CAMERA_STUB_VIEW_TYPE -> universalGalleryVH(R.layout.design_gallery_camera_stub)
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }
}
