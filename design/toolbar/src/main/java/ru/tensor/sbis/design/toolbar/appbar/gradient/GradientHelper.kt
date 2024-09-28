package ru.tensor.sbis.design.toolbar.appbar.gradient

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px
import ru.tensor.sbis.design.toolbar.appbar.model.ColorModel
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.OnExpandedTitleLineCountChangeListener

/**
 * Инструмент для обновления градиента графической шапки
 *
 * @author us.bessonov
 */
internal interface GradientHelper : OnExpandedTitleLineCountChangeListener {

    /**
     * Метод вызывается для обновления параметров градиента
     */
    fun updateModel(model: ColorModel?) {
        updateGradient(model?.mainColor)
    }

    /**
     * Устанавливает градиент указанного цвета
     */
    fun updateGradient(@ColorInt color: Int? = null)

    /**
     * Задаёт высоту заливки под градиентом
     */
    fun setFillHeight(@Px fillHeight: Int)

    /**
     * Обновляет вид градиента, в зависимости от степени раскрытия шапки
     */
    fun update(@FloatRange(from = 0.0, to = 1.0) normalizedOffset: Float)
}