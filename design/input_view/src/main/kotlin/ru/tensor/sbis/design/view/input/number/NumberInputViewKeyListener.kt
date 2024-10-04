package ru.tensor.sbis.design.view.input.number

import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.widget.EditText

/**
 * [DigitsKeyListener] для [NumberInputView] с определенными символами и поддержкой фильтрации знаков "-" и ".".
 *
 * @author ps.smirnyh
 */
@Suppress("DEPRECATION")
internal class NumberInputViewKeyListener(internal var inputView: EditText?) : DigitsKeyListener(true, true) {

    internal var allowDot = true

    override fun getAcceptedChars() = if (allowDot) CHARACTERS_FULL else CHARACTERS_INTEGER

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val changeSelection = source?.toString() == "." && dest?.getOrNull(dstart) == '.'
        val out: CharSequence? = super.filter(source, start, end, dest, dstart, dend)
        if (allowDot) {
            // Если ввели разделитель и он там уже был, то сдвигаем курсор вправо
            if (changeSelection) inputView?.setSelection(dstart + 1)
            return out
        }
        return out?.filter { it != '.' } ?: source?.filter { it != '.' }
    }

    companion object {
        private val CHARACTERS_FULL = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '.'
        )
        private val CHARACTERS_INTEGER = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'
        )
    }

}