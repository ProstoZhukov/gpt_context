/**
 * @author mb.kruglova
 */
package ru.tensor.sbis.date_picker

import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design.view_ext.SimplifiedTextView

@BindingAdapter("textColorAttr")
fun setTextColorAttr(view: TextView, @AttrRes attrResId: Int) {
    view.context.getDataFromAttrOrNull(attrResId)
        ?.let(view::setTextColor)
}

fun setTextColorAttr(view: SimplifiedTextView, @AttrRes attrResId: Int) {
    view.context.getDataFromAttrOrNull(attrResId)
        ?.let(view::setTextColor)
}

@BindingAdapter("calendarData")
fun setCalendarData(recyclerView: DatePickerRecyclerView, data: List<Any>?) {
    data?.let { recyclerView.showData(data) }
}