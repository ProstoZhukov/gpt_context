package ru.tensor.sbis.date_picker.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Вьюхолдер адаптера календаря
 *
 * @author us.bessonov
 */
internal sealed class DatePickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun recycle() = Unit
}