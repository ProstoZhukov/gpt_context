package ru.tensor.sbis.design.buttons.base.models.style

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import ru.tensor.sbis.design.buttons.base.models.style.providers.StaticStyleProvider
import ru.tensor.sbis.design.buttons.base.models.style.providers.StyleProvider

/**
 * Модель для раскраски иконок в кнопках.
 *
 * @author ma.kolpakov
 */
data class SbisButtonIconStyle internal constructor(
    internal val styleProvider: StyleProvider
) {

    constructor(colors: ColorStateList) : this(StaticStyleProvider(colors, colors, colors))

    companion object {

        internal const val NO_TINT = Color.TRANSPARENT

        /**
         * Стиль иконки, для которой не нужно применять оттенок.
         */
        val NoTintStyle = SbisButtonIconStyle(ColorStateList.valueOf(NO_TINT))
    }

    internal fun loadStyle(context: Context, consumer: StyleConsumer) =
        styleProvider.loadStyle(context, consumer)
}