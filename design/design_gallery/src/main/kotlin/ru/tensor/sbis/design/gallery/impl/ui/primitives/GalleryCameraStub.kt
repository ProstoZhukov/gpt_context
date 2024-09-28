package ru.tensor.sbis.design.gallery.impl.ui.primitives

import android.util.SparseArray
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem

private const val TYPE_ID = "GalleryCameraStubTypeId"

internal class GalleryCameraStub(
    private val adapterViewType: Int,
) : UniversalBindingItem(TYPE_ID) {

    override fun getViewType(): Int = adapterViewType

    override fun createBindingVariables(): SparseArray<Any> = SparseArray(0)
}