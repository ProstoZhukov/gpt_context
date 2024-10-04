package ru.tensor.sbis.design.gallery.impl.ui.primitives

import android.net.Uri
import android.util.SparseArray
import com.facebook.imagepipeline.request.ImageRequest
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.design.gallery.BR

private const val TYPE_ID = "GalleryMediaItemTypeId"

internal data class GalleryItemVM(
    val adapterViewType: Int,
    val id: Int,
    val uri: Uri,
    val imageRequest: ImageRequest,
    val isVideo: Boolean,
    val duration: CharSequence,
    val selectionNumber: StateFlow<Int?>
) : UniversalBindingItem(TYPE_ID) {

    override fun getViewType(): Int = adapterViewType

    override fun createBindingVariables(): SparseArray<Any> =
        SparseArray<Any>(1).apply { put(BR.viewModel, this@GalleryItemVM) }
}