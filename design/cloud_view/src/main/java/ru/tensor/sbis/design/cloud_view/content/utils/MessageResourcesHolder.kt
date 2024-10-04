package ru.tensor.sbis.design.cloud_view.content.utils

import androidx.annotation.ColorInt

/**
 * Интерфейс источника прикладных цветов, используемых в ячейке-облаке.
 *
 * @author ma.kolpakov
 */
interface MessageResourcesHolder {

    /**
     * Цвет текста, соответствующий заданному типу [CloudViewTextColorType].
     */
    @ColorInt
    fun getTextColor(@CloudViewTextColorType type: Int): Int

    /**
     * Цвет иконки сертификата подписи.
     * @param mine true если требуется цвет иконки подписи текущим пользователем.
     */
    @ColorInt
    fun getCertificateBadgeColor(mine: Boolean): Int

    /**
     * Цвет текста сертификата подписи.
     * @param mine true если требуется цвет текста подписи текущим пользователем.
     */
    @ColorInt
    fun getOwnerInfoColor(mine: Boolean): Int
}