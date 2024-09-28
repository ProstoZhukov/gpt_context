package ru.tensor.sbis.swipeablelayout.api.menu

import android.content.Context
import androidx.annotation.StringRes

/**
 * Текст, представленный либо строковым ресурсом, либо строкой.
 *
 * @author us.bessonov
 */
internal sealed interface SwipeText

/**
 * Текст в виде строки.
 */
internal class RawText(val text: String) : SwipeText

/**
 * Строковый ресурс.
 */
internal class TextRes(@StringRes val resId: Int) : SwipeText

/** @SelfDocumented */
internal fun SwipeText.getText(context: Context) = when (this) {
    is RawText -> text
    is TextRes -> context.getString(resId)
}