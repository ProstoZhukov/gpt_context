package ru.tensor.sbis.design.buttons.base.models.style.providers

import android.content.Context
import android.content.res.ColorStateList
import ru.tensor.sbis.design.buttons.base.models.style.StyleConsumer

/**
 * Поставщик статично заданных данных о стиле текста и иконки в кнопке.
 *
 * @author ma.kolpakov
 */
internal data class StaticStyleProvider(
    val default: ColorStateList,
    val contrast: ColorStateList,
    val transparent: ColorStateList
) : StyleProvider {

    override fun loadStyle(context: Context, consumer: StyleConsumer) {
        consumer.onStyleLoaded(default, contrast, transparent)
    }
}