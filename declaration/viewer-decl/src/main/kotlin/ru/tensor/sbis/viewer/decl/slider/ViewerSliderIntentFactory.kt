package ru.tensor.sbis.viewer.decl.slider

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика для создания интента на запуск слайдера просмотрщиков
 *
 * @author sa.nikitin
 */
interface ViewerSliderIntentFactory : Feature {

    /**
     * Создать интент на запуск слайдера просмотрщиков
     */
    fun createViewerSliderIntent(context: Context, args: ViewerSliderArgs): Intent
}