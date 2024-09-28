package ru.tensor.sbis.design.buttons.base.models.style.providers

import android.content.Context
import ru.tensor.sbis.design.buttons.base.models.style.StyleConsumer

/**
 * Модель для загрузки стиля текста и иконок в кнопках.
 *
 * @author ma.kolpakov
 */
internal interface StyleProvider {

    fun loadStyle(context: Context, consumer: StyleConsumer)
}