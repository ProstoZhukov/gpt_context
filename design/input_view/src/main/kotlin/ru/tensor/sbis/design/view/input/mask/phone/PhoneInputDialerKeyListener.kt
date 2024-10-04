package ru.tensor.sbis.design.view.input.mask.phone

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.DialerKeyListener

/**
 * [DialerKeyListener] с ограниченным набором доступных для ввода символов.
 *
 * @author ps.smirnyh
 */
internal object PhoneInputDialerKeyListener : DialerKeyListener() {

    override fun getAcceptedChars() = CHARACTERS

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        val superResult = super.filter(source, start, end, dest, dstart, dend)
        if (source.isNullOrBlank()) {
            return superResult
        }
        val filteredText = SpannableStringBuilder(superResult ?: source)
        val isFilteredResult = filteredText.isPlusNotInBegin(dstart)
        return if (isFilteredResult) {
            SpannableStringBuilder(filteredText.filterNotFirstPlus(dstart))
        } else {
            superResult
        }
    }

    private val CHARACTERS = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+'
    )

    private fun CharSequence.indexOfLastPlus() = indexOfLast { it == '+' }

    private fun CharSequence.isPlusNotInBegin(startIndex: Int): Boolean {
        val indexOfLastPlus = indexOfLastPlus()
        return any { it == '+' && (startIndex != 0 || indexOfLastPlus != 0) }
    }

    private fun CharSequence.filterNotFirstPlus(startIndex: Int) =
        filterIndexed { index, char ->
            char != '+' || (index == 0 && startIndex == 0)
        }
}
