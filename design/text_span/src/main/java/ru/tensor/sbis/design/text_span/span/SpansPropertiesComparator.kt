package ru.tensor.sbis.design.text_span.span

import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.URLSpan

/**
 * Сущность, сранивающая спаны по их свойствам
 * Следует постепенно расширять [invoke] новыми классами спанов
 *
 * @author sa.nikitin
 */
internal class SpansPropertiesComparator : SpansComparator {

    override fun invoke(firstSpan: Any, secondSpan: Any): Boolean =
        when (firstSpan) {
            is ForegroundColorSpan -> firstSpan.compareWith(secondSpan) { foregroundColor == it.foregroundColor }
            is AbsoluteSizeSpan -> firstSpan.compareWith(secondSpan) { size == it.size && dip == it.dip }
            is RelativeSizeSpan -> firstSpan.compareWith(secondSpan) { sizeChange == it.sizeChange }
            is URLSpan -> firstSpan.compareWith(secondSpan) { true } // URLSpan не имеет особых параметров, поэтому true
            is TextGravitySpan -> firstSpan.compareWith(secondSpan) { shift == it.shift }
            else -> firstSpan == secondSpan
        }

    private inline fun <reified T : Any> T.compareWith(secondSpan: Any, compare: T.(secondSpan: T) -> Boolean) =
        when {
            this === secondSpan -> true
            secondSpan is T -> compare(secondSpan)
            else -> false
        }
}