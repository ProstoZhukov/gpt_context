package ru.tensor.sbis.design.toolbar.appbar.color

import android.view.View
import ru.tensor.sbis.design.toolbar.appbar.model.ColorModel

/**
 * Интерфейс обработчика обновления цветовой схемы
 *
 * @author ma.kolpakov
 * Создан 9/27/2019
 */
internal interface ColorUpdateFunction<in ViewType : View> {

    /**
     * Метод обновления цвета у [view]. При получении `model == null` нужно установить расцветку "по умолчанию" для [view]
     */
    fun updateColorModel(view: ViewType, model: ColorModel?)
}