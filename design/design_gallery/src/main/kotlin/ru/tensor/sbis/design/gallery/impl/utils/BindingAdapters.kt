package ru.tensor.sbis.design.gallery.impl.utils

import android.view.View
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * @SelfDocumented
 */
@BindingAdapter("textOrInvisible")
fun SbisTextView.textOrInvisible(text: CharSequence?) {
    if (text.isNullOrBlank()) {
        visibility = View.INVISIBLE
    } else {
        visibility = View.VISIBLE
        this.text = text
    }
}