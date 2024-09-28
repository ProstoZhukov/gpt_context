package ru.tensor.sbis.design.buttons.base.models.style

import android.content.res.ColorStateList

/**
 * Функциональный интерфейс для применения загруженных цветов.
 *
 * @author ma.kolpakov
 */
internal fun interface StyleConsumer {

    /**
     * Функция вызывается после загрузки стилей.
     *
     * [default] - Стандартный набор цветов,
     * [contrast] - набор цветов для контрастного стиля,
     * [transparent] - набор цветов для прозрачного стиля.
     */
    fun onStyleLoaded(
        default: ColorStateList,
        contrast: ColorStateList,
        transparent: ColorStateList
    )
}