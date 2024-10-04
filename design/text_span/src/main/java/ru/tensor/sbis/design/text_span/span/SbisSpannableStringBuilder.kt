package ru.tensor.sbis.design.text_span.span

import android.text.SpannableStringBuilder
import android.text.Spanned

/** @SelfDocumented */
typealias SpansComparator = (firstSpan: Any, secondSpan: Any) -> Boolean

/**
 * SpannableStringBuilder с исправленным методом equals
 *
 * @property spansComparator    Сущность, сравнивающая спаны
 *
 * @author sa.nikitin
 */
@Suppress("EqualsOrHashCode") // Согласовано с автором класса Никитиным С.
class SbisSpannableStringBuilder(
    text: CharSequence = "",
    start: Int = 0,
    end: Int = text.length,
    private val spansComparator: SpansComparator = SpansPropertiesComparator()
) : SpannableStringBuilder(text, start, end) {

    /**
     * Исправленный equals
     * В оригинальном [SpannableStringBuilder.equals] у объекта, с которым сравнивается текущий,
     * спаны сортировались перед сравниванием внутри метода [Spanned.getSpans], а у текущего не сортировались
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

}