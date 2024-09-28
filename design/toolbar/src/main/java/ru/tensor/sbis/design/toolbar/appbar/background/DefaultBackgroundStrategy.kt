package ru.tensor.sbis.design.toolbar.appbar.background

import android.view.View
import ru.tensor.sbis.design.toolbar.appbar.model.ImageBackground

/**
 * Стратегия обновления фона по умолчанию
 *
 * @author ma.kolpakov
 * Создан 9/23/2019
 */
internal class DefaultBackgroundStrategy(view: View) : AbstractBackgroundStrategy<View>(view) {

    override fun setImageBackground(model: ImageBackground) {
        TODO("Реализация загрузки картинки и установки на фон. Использовать fresco контроллер")
    }
}