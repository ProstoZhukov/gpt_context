package ru.tensor.sbis.common_filters.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.common_filters.FilterWindowHeaderItem
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

const val NO_RES_ID = 0

@BindingAdapter(value = ["titleResOrText"])
internal fun TextView.setTitleResourceOrText(item: FilterWindowHeaderItem) = with(item) {
    if (titleText != null || titleRes == NO_RES_ID) {
        text = titleText
    } else {
        setText(titleRes)
    }
}

@BindingAdapter(value = ["titleResOrText"])
internal fun SbisTextView.setTitleResourceOrText(item: FilterWindowHeaderItem) = with(item) {
    if (titleText != null || titleRes == NO_RES_ID) {
        text = titleText
    } else {
        setText(titleRes)
    }
}