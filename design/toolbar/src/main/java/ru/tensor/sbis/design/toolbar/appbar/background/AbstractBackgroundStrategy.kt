package ru.tensor.sbis.design.toolbar.appbar.background

import android.view.View
import ru.tensor.sbis.design.toolbar.appbar.model.BackgroundModel
import ru.tensor.sbis.design.toolbar.appbar.model.ColorBackground
import ru.tensor.sbis.design.toolbar.appbar.model.ImageBackground
import ru.tensor.sbis.design.toolbar.appbar.model.UndefinedBackground

/**
 * Реализация [BackgroundStrategy], которая предоставляет базовый функционал
 *
 * @author ma.kolpakov
 * Создан 9/25/2019
 */
internal abstract class AbstractBackgroundStrategy<out ViewType : View>(
    protected val view: ViewType
) : BackgroundStrategy {

    override fun setModel(model: BackgroundModel) = when (model) {
        UndefinedBackground -> clearBackground()
        is ColorBackground -> setColorBackground(model)
        is ImageBackground -> setImageBackground(model)
    }

    /**
     * Метод очистки фона при установке
     */
    protected open fun clearBackground() {
        view.setBackgroundResource(0)
    }

    /**
     * Установка фона по цветовой модели [model]
     */
    protected open fun setColorBackground(model: ColorBackground) {
        view.setBackgroundColor(model.color)
    }

    /**
     * Установка фоновой картинки по модели [model]
     */
    protected abstract fun setImageBackground(model: ImageBackground)
}