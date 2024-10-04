package ru.tensor.sbis.design.buttons.base.api

import androidx.annotation.StringRes

/**
 * API для точечного обновления текста в кнопке. Для случаев, когда кроме текста не не меняются
 * параметры.
 *
 * @author ma.kolpakov
 */
interface SbisButtonTitleApi {

    /**
     * Установить текст в кнопку.
     */
    fun setTitle(title: CharSequence?)

    /**
     * Установить текст в кнопку из строкового ресурса.
     */
    fun setTitleRes(@StringRes titleRes: Int)

    /**
     * Установить флаг [scaleOn] для скаляции шрифта заголовка пропорционально системному размеру шрифта.
     */
    fun setTitleScaleOn(scaleOn: Boolean?)
}