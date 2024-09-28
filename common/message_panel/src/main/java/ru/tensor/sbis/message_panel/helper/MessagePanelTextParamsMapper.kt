package ru.tensor.sbis.message_panel.helper

import android.view.Gravity
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.common.rx.RxContainer

internal const val NO_MESSAGE_MAX_LINES = 1

/**
 * Функция преобразования элементов состояния панели ввода в набор фактически применяемых параметров текста
 *
 * @author us.bessonov
 */
internal class MessagePanelTextParamsMapper :
    Function5<Boolean, Int, RxContainer<String>, Int, Int, MessagePanelTextParams> {

    override fun invoke(
        newDialogModeEnabled: Boolean,
        @Px
        maxHeight: Int,
        messageText: RxContainer<String>,
        @StringRes hint: Int,
        minLines: Int
    ): MessagePanelTextParams {
        val resultMinLines = if (newDialogModeEnabled) {
            MESSAGE_PANEL_NEW_DIALOG_MODE_MIN_LINES
        } else {
            MESSAGE_PANEL_NEW_DIALOG_MODE_MIN_LINES_DEFAULT
        }.coerceAtLeast(minLines)
        val isTextEmpty = messageText.value.isNullOrEmpty()
        val maxHeightParams = if (isTextEmpty && !newDialogModeEnabled) {
            MaxLines(NO_MESSAGE_MAX_LINES)
        } else {
            MaxHeight(maxHeight)
        }
        return MessagePanelTextParams(
            resultMinLines,
            maxHeightParams,
            ellipsizeEnd = isTextEmpty,
            gravity = if (resultMinLines > NO_MESSAGE_MAX_LINES) Gravity.TOP else Gravity.CENTER_VERTICAL,
            hint = hint.takeIf { isTextEmpty } ?: ID_NULL
        )
    }

}