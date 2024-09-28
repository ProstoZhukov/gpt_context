package ru.tensor.sbis.message_panel.helper

import android.text.TextUtils
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat.ID_NULL

/**
 * Вспомогательные инструменты для EditText
 *
 * @author vv.chekurda
 * @since 7/11/2019
 */

internal const val MESSAGE_PANEL_NEW_DIALOG_MODE_MIN_LINES = 5
internal const val MESSAGE_PANEL_NEW_DIALOG_MODE_MIN_LINES_DEFAULT = 1

/**
 * Отметка о переводе поля ввода в режим ввода "нового сообщения"
 */
internal const val NEW_MESSAGE_MODE_MARKER = "NEW_MESSAGE_MODE_MARKER"

/**
 * Метод, переключающий отметку панели ввода о нахождении в режиме "Ввода нового сообщения"
 */
internal fun EditText.markNewMessageMode(enable: Boolean) {
    tag = if (enable) NEW_MESSAGE_MODE_MARKER else null
}

/**
 * Информация о том, находится ли панель ввода в режиме "нового сообщения"
 */
internal val EditText.isNewMessageMode get() = tag == NEW_MESSAGE_MODE_MARKER

/**@SelfDocumented */
internal fun EditText.applyTextParams(params: MessagePanelTextParams): Unit = params.let {
    ellipsize = if (it.ellipsizeEnd) TextUtils.TruncateAt.END else null
    gravity = it.gravity
    minLines = it.minLines
    when (it.maxHeightParams) {
        is MaxHeight -> maxHeight = it.maxHeightParams.height
        is MaxLines  -> maxLines = it.maxHeightParams.lines
    }
    if (it.hint != ID_NULL) {
        setHint(it.hint)
    } else {
        hint = null
    }
}