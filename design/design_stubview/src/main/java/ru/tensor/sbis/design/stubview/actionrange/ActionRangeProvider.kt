package ru.tensor.sbis.design.stubview.actionrange

import android.content.Context

/**
 * Вспомогательный инструмент для получения диапазонов символов кликабельного текста для заглушек
 *
 * @author ma.kolpakov
 */
internal interface ActionRangeProvider {

    /**
     * Получение диапазона символов
     *
     * @param context контект для доступа к ресурсам
     * @param detailsText полный текст описания заглушки
     *
     * @return диапазон символов кликабельного текста в [detailsText]
     */
    fun getRange(context: Context, detailsText: String): IntRange
}
