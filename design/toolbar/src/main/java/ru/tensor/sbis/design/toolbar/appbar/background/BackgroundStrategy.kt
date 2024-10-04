package ru.tensor.sbis.design.toolbar.appbar.background

import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.model.BackgroundModel

/**
 * Стратегия установки модели фона [BackgroundModel] в графическую шапку [SbisAppBarLayout]
 *
 * @author ma.kolpakov
 * Создан 9/23/2019
 */
interface BackgroundStrategy {

    /**
     * Метод вызывается при изменении модели фона в графической шапке
     */
    fun setModel(model: BackgroundModel)
}