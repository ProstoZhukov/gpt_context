package ru.tensor.sbis.recorder.decl

import android.app.Activity
import android.content.Context
import ru.tensor.sbis.design.swipeback.SwipeBackLayout
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Провайдер зависимости [RecorderViewDependency]
 *
 * @author ma.kolpakov
 */
interface RecorderViewDependencyProvider : Feature {

    /**
     * Получение зависимости аудиозаписи
     *
     * @param context конекст message panel
     * @param activity активити, в которой расположена message panel
     * @param swipeBackLayout от [activity]
     */
    fun getRecorderViewDependency(
        context:Context,
        activity: Activity,
        swipeBackLayout: SwipeBackLayout?
    ): RecorderViewDependency? = null
}
