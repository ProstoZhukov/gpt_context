package ru.tensor.sbis.entrypoint_guard.activity

import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import ru.tensor.sbis.entrypoint_guard.activity.ActivityAssistant.RootContainer

/**
 * Дефолтная реализация [ActivityAssistant.ContainerFactory].
 *
 * @author kv.martyshenko
 */
object DefaultRootContainerFactory : ActivityAssistant.ContainerFactory {

    override fun createContainer(
        activity: ComponentActivity,
        @IdRes containerId: Int
    ): RootContainer {

        val frame = FrameLayout(activity).apply {
            id = containerId
        }
        return RootContainer(frame, frame)
    }
}