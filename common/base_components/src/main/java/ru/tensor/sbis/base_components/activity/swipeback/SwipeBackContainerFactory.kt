package ru.tensor.sbis.base_components.activity.swipeback

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import ru.tensor.sbis.base_components.activity.swipeback.SwipeBackAware.Strategy
import ru.tensor.sbis.design.swipeback.SwipeBackLayout
import ru.tensor.sbis.design.swipeback.SwipeBackLayout.*
import ru.tensor.sbis.entrypoint_guard.activity.ActivityAssistant
import ru.tensor.sbis.entrypoint_guard.activity.ActivityAssistant.ContainerFactory
import ru.tensor.sbis.entrypoint_guard.activity.ActivityAssistant.RootContainer
import ru.tensor.sbis.entrypoint_guard.activity.DefaultRootContainerFactory
import ru.tensor.sbis.design.swipeback.R as R

/**
 * Реализация [ActivityAssistant.ContainerFactory] для добавления поведения swipe back.
 *
 * @author kv.martyshenko
 */
class SwipeBackContainerFactory(
    private val swipeBackInsensitiveContainerFactory: ContainerFactory = DefaultRootContainerFactory
) : ContainerFactory {

    override fun createContainer(
        activity: ComponentActivity,
        @IdRes containerId: Int
    ): RootContainer {
        return if (activity !is SwipeBackAware || activity.strategy == Strategy.DISABLED) {
            swipeBackInsensitiveContainerFactory.createContainer(activity, containerId)
        } else {
            val root = RelativeLayout(activity)
            val swipeBackLayout = SwipeBackLayout(activity)
            swipeBackLayout.setDragEdge(DragEdge.LEFT)
            val shadow = ImageView(activity)
            shadow.setBackgroundColor(
                activity.resources.getColor(R.color.swipe_back_dimm_background_color)
            )
            shadow.setAlpha(0f)
            val params = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
            root.addView(shadow, params)
            root.addView(swipeBackLayout)

            val frame = FrameLayout(activity).apply {
                id = containerId
            }
            swipeBackLayout.addView(frame)

            val swipeBackController = SwipeBackController(swipeBackLayout, shadow)
            keepUntilDestroy(swipeBackController, activity)

            return RootContainer(root, frame)
        }
    }

}