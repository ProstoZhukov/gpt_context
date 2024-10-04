package ru.tensor.sbis.design.stubview.actionrange

import android.content.Context
import androidx.annotation.StringRes

/**
 * Провайдер диапазона кликабельного текста по строковому ресурсу
 *
 * @param detailsActionTextRes строковый ресурс кликабельного текста
 *
 * @author ma.kolpakov
 */
internal class ResourceActionRangeProvider(@StringRes private val detailsActionTextRes: Int) : ActionRangeProvider {

    override fun getRange(context: Context, detailsText: String): IntRange {
        val detailsActionText = context.getString(detailsActionTextRes)
        return StringActionRangeProvider(detailsActionText).getRange(context, detailsText)
    }
}
