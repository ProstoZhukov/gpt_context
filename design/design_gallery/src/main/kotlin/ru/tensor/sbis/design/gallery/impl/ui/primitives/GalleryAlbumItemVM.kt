package ru.tensor.sbis.design.gallery.impl.ui.primitives

import android.util.SparseArray
import com.facebook.imagepipeline.request.ImageRequest
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.design.gallery.BR

private const val TYPE_ID = "GalleryAlbumItemTypeId"

internal data class GalleryAlbumItemVM(
    val adapterViewType: Int,
    val id: Int,
    val name: String,
    val itemsCount: String,
    val imageRequest: ImageRequest?,
    val selectedItemsCount: Int
) : UniversalBindingItem(TYPE_ID) {

    override fun getViewType(): Int = adapterViewType

    override fun createBindingVariables(): SparseArray<Any> =
        SparseArray<Any>(1).apply { put(BR.viewModel, this@GalleryAlbumItemVM) }
}