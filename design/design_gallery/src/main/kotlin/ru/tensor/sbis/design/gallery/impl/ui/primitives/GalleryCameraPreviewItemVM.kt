package ru.tensor.sbis.design.gallery.impl.ui.primitives

import android.util.SparseArray
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.design.gallery.BR

private const val TYPE_ID = "CameraPreviewItemTypeId"

internal data class GalleryCameraPreviewItemVM(
    val adapterViewType: Int,
    val isSmall: Boolean
) : UniversalBindingItem(TYPE_ID) {

    override fun getViewType(): Int = adapterViewType

    override fun createBindingVariables(): SparseArray<Any> =
        SparseArray<Any>(1).apply { put(BR.viewModel, this@GalleryCameraPreviewItemVM) }
}
