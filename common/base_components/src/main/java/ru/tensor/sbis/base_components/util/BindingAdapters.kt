package ru.tensor.sbis.base_components.util

import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter

/**
 * Установка textAppearance - фикс краша для API 21.
 * https://online.sbis.ru/opendoc.html?guid=7906d00b-2cb1-4ac2-96ae-3f17c325c88a
 */
@BindingAdapter("textAppearance")
internal fun TextView.setTextAppearanceStyleId(@StyleRes styleId: Int) {
    TextViewCompat.setTextAppearance(this, styleId)
}