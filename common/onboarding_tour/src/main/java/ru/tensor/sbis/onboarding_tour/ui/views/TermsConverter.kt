package ru.tensor.sbis.onboarding_tour.ui.views

import android.content.Context
import androidx.annotation.StringRes

/** @SelfDocumented */
internal class TermsConverter {

    /** @SelfDocumented */
    internal fun calculateCaptionAndLinkedText(
        context: Context,
        @StringRes captionResId: Int,
        links: List<String>
    ): Pair<String, Map<String, String>> {
        var caption = context.getString(captionResId)
        val delimiterCount = caption.count { it == CHART_DELIMITER } / 2
        require(delimiterCount % 2 == 0) {
            "The caption must contain even number of delimeters: $SHARP_DELIMITER"
        }

        val markersList = mutableListOf<Int>()
        while (markersList.size != delimiterCount) {
            val startIndex = markersList.lastOrNull() ?: 0
            val index = caption.indexOf(SHARP_DELIMITER, startIndex = startIndex)
            caption = caption.removeRange(IntRange(index, index + 1))
            markersList.add(index)
        }

        val res = mutableListOf<String>()
        while (markersList.isNotEmpty()) {
            val positions = markersList.take(2)
            val linkedString = caption.substring(positions.component1(), positions.component2())
            res.add(linkedString)

            markersList.removeFirst()
            markersList.removeFirst()
        }

        return caption to res.zip(links).toMap()
    }

    companion object {

        /** Символ используемый как разделитель, ASCII Character номер 37 %. */
        private val CHART_DELIMITER = Char(37)

        /** Разделитель %%. */
        private val SHARP_DELIMITER = "$CHART_DELIMITER$CHART_DELIMITER"
    }
}