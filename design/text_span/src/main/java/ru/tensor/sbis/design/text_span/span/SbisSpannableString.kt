package ru.tensor.sbis.design.text_span.span

import android.text.SpannableString
import android.text.Spanned

/**
 * SpannableString с исправленным методом equals, аналогично [SbisSpannableStringBuilder].
 *
 * @author rv.krohalev
 */
@Suppress("EqualsOrHashCode")
class SbisSpannableString(text: CharSequence = "") : SpannableString(text) {

    /**
     * Исправленный equals, аналогично [SbisSpannableStringBuilder].
     */
    override fun equals(other: Any?): Boolean {
        if (other is Spanned && toString() == other.toString()) {
            val spans = getSpans(0, length, Any::class.java) // эта строчка фиксит
            val otherSpans = other.getSpans(0, other.length, Any::class.java)
            if (spans.size == otherSpans.size) {
                for (i in spans.indices) {
                    val thisSpan = spans[i]
                    val otherSpan = otherSpans[i]
                    if (thisSpan === this) {
                        if (other !== otherSpan ||
                            getSpanStart(thisSpan) != other.getSpanStart(otherSpan) ||
                            getSpanEnd(thisSpan) != other.getSpanEnd(otherSpan) ||
                            getSpanFlags(thisSpan) != other.getSpanFlags(otherSpan)
                        ) {
                            return false
                        }
                    } else if (
                        !spansComparator.invoke(thisSpan, otherSpan) ||
                        getSpanStart(thisSpan) != other.getSpanStart(otherSpan) ||
                        getSpanEnd(thisSpan) != other.getSpanEnd(otherSpan) ||
                        getSpanFlags(thisSpan) != other.getSpanFlags(otherSpan)
                    ) {
                        return false
                    }
                }
                return true
            }
        }
        return false
    }

    private companion object {
        private val spansComparator: SpansComparator = SpansPropertiesComparator()
    }
}