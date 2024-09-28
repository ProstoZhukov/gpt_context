package ru.tensor.sbis.message_panel.recorder

import android.app.Activity
import android.content.Context
import ru.tensor.sbis.design.swipeback.SwipeBackLayout
import ru.tensor.sbis.message_panel.recorder.util.createRecorderDependency
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.recorder.decl.RecorderViewDependency
import ru.tensor.sbis.recorder.decl.RecorderViewDependencyProvider

/**
 * Плагин записи для панели сообщений
 *
 * @author kv.martyshenko
 */
object MessagePanelRecorderPlugin : BasePlugin<Unit>() {
    private val recorderViewDependencyProvider by lazy {
        object : RecorderViewDependencyProvider {
            override fun getRecorderViewDependency(
                context: Context,
                activity: Activity,
                swipeBackLayout: SwipeBackLayout?
            ): RecorderViewDependency? {
                return createRecorderDependency(context, activity, swipeBackLayout)
            }
        }
    }
    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(RecorderViewDependencyProvider::class.java) { recorderViewDependencyProvider }
    )

    override val dependency: Dependency = Dependency.Builder()
        .build()

    override val customizationOptions: Unit = Unit

}