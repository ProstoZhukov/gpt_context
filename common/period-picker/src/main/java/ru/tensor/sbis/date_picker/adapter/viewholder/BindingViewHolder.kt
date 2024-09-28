package ru.tensor.sbis.date_picker.adapter.viewholder

import ru.tensor.sbis.base_components.adapter.vmadapter.ViewHolder
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter

/**
 * Вьюхолдер, используемый во [ViewModelAdapter]
 *
 * @author us.bessonov
 */
internal class BindingViewHolder(val vmAdapterHolder: ViewHolder) : DatePickerViewHolder(vmAdapterHolder.itemView) {

    override fun recycle() = vmAdapterHolder.onRecycled()
}