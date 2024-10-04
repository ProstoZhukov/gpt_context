package ru.tensor.sbis.design.buttons.base.models.style.providers

import android.content.Context
import androidx.annotation.ColorRes
import ru.tensor.sbis.design.buttons.base.models.style.StyleConsumer
import ru.tensor.sbis.design.buttons.base.utils.style.loadColorStateList

/**
 * Поставщик данных о стиле текста и иконки в кнопке из ресурсов.
 *
 * @author ma.kolpakov
 */
internal data class ResourceStyleProvider(
    @ColorRes private val color: Int,
    @ColorRes private val colorPressed: Int,
    @ColorRes private val colorDisabled: Int,
    @ColorRes private val contrastColor: Int,
    @ColorRes private val contrastColorPressed: Int,
    @ColorRes private val contrastColorDisabled: Int,
    @ColorRes private val transparentColor: Int,
    @ColorRes private val transparentColorPressed: Int,
    @ColorRes private val transparentColorDisabled: Int
) : StyleProvider {

    override fun loadStyle(context: Context, consumer: StyleConsumer) {
        val default = context.loadColorStateList(color, colorPressed, colorDisabled)
        val contrast =
            context.loadColorStateList(contrastColor, contrastColorPressed, contrastColorDisabled)
        val transparent =
            context.loadColorStateList(
                transparentColor,
                transparentColorPressed,
                transparentColorDisabled
            )
        consumer.onStyleLoaded(default, contrast, transparent)
    }
}