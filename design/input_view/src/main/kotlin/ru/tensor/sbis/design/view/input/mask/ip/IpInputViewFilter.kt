package ru.tensor.sbis.design.view.input.mask.ip

import android.text.InputFilter
import android.text.Spanned

internal class IpInputViewFilter : InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (source != null) {
            val destText = dest.toString()
            val resultText = destText.substring(0, dstart) + source.subSequence(start, end) + destText.substring(dend)
            val regex = "^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?".toRegex()
            if (!resultText.matches(regex)) { return "" }

            val splits = resultText.split(".").dropLastWhile { it.isEmpty() }
            if (splits.any { Integer.valueOf(it) > 255 }) { return "" }
        }
        return null
    }
}