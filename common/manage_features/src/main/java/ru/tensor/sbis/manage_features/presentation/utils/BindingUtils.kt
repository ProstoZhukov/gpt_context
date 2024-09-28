package ru.tensor.sbis.manage_features.presentation.utils

import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter

/**
 * Безопасная установка строкового ресурса по id. Актуально для ресурсов, которые доставляются
 * отложенно и могут отсутствовать в момент "первого" биндинга
 *
 * @param textId идентификатор строкового ресурса. Значения `null` и `0` допустимы, вместо них
 * будет установлена пустая строка
 */
@BindingAdapter("textRes")
internal fun TextView.textRes(@StringRes textId: Int?) {
    text = when (textId) {
        null, 0 -> ""
        else    -> context.getString(textId)
    }
}