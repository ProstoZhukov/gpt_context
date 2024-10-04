package ru.tensor.sbis.design.stubview.actionrange

import android.content.Context
import timber.log.Timber

/**
 * Провайдер диапазона кликабельного текста по подстроке
 *
 * @param detailsActionText кликабельный текст
 *
 * @author ma.kolpakov
 */
internal class StringActionRangeProvider(private val detailsActionText: String) : ActionRangeProvider {

    override fun getRange(context: Context, detailsText: String): IntRange {
        val rangeStart = detailsText.indexOf(detailsActionText)

        if (rangeStart == -1) {
            Timber.w("The string '$detailsActionText' is not present it text: '$detailsText'")
            return IntRange.EMPTY
        }

        val rangeEnd = rangeStart + detailsActionText.length
        return IntRange(rangeStart, rangeEnd)
    }
}
