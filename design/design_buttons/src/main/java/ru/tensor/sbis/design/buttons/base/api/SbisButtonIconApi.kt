package ru.tensor.sbis.design.buttons.base.api

import androidx.annotation.DrawableRes
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * API для точечного обновления иконки в кнопке. Для случаев, когда кроме иконки не меняются параметры.
 *
 * @author ma.kolpakov
 */
interface SbisButtonIconApi {

    /**
     * Установить шрифтовую иконку.
     */
    fun setIcon(icon: SbisMobileIcon.Icon?)

    /**
     * Установить символ в качестве иконки.
     */
    fun setIconChar(icon: Char?)

    /**
     * Установить изображение в качестве иконки.
     */
    fun setIconDrawable(@DrawableRes iconRes: Int)

    /**
     * Установить маркированную строку в качестве иконки.
     */
    fun setIconSpannable(icon: CharSequence?)

    /**
     * Установить флаг [scaleOn] для скаляции шрифта иконки пропорционально системному размеру шрифта..
     */
    fun setIconScaleOn(scaleOn: Boolean?)
}