package ru.tensor.sbis.design.checkbox.utils

import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.checkbox.SbisCheckboxView
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxContent
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue

/**
 * Конвертация [Boolean] значения чекбокса в [SbisCheckboxValue].
 */
@BindingAdapter("value")
fun SbisCheckboxView.setValue(value: Boolean) {
    this.value = if (value) SbisCheckboxValue.CHECKED else SbisCheckboxValue.UNCHECKED
}

/**
 * Адаптер для установки текста контента для чекбокса.
 */
@BindingAdapter("text")
fun SbisCheckboxView.setText(text: String) {
    content = SbisCheckboxContent.TextContent(text)
}
