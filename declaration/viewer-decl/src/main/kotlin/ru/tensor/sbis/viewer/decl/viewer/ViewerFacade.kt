package ru.tensor.sbis.viewer.decl.viewer

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.common_views.document.thumbnail.ThumbnailDisplayParams
import ru.tensor.sbis.common_views.document.thumbnail.ThumbnailParams

/**
 * Фасад просмотрщика
 *
 * @author sa.nikitin
 */
interface ViewerFacade : Feature {

    /**
     * Оценить аргументы на возможность создания просмотрщика
     *
     * @return [ViewerContract], если аргументы подходят к данному просмотрщику, иначе - null
     */
    fun evaluateArgs(args: ViewerArgs): ViewerContract?
}

/**
 * Контракт на создание просмотрщика
 *
 * @author sa.nikitin
 */
interface ViewerContract {

    /**
     * Создать фрагмент просмотрщика
     */
    fun createViewer(): Fragment

    /**
     * Создать параметры миниатюры просмотрщика, отображаемой в барабане
     */
    fun createThumbnailParams(displayParams: ThumbnailDisplayParams): ThumbnailParams
}