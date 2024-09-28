package ru.tensor.sbis.base_components.activity.behaviour

import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.base_components.activity.swipeback.SwipeBackAware
import ru.tensor.sbis.base_components.activity.swipeback.SwipeBackController
import ru.tensor.sbis.base_components.activity.swipeback.findSwipeBackController
import ru.tensor.sbis.design.swipeback.R
import ru.tensor.sbis.design.swipeback.SwipeBackLayout.SwipeBackListener
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityBehaviour

/**
 * Реализация поведения добавляющего свайп назад.
 * Активность должна быть [SwipeBackAware] с параметром [SwipeBackAware.Strategy.ENABLED].
 *
 * @author kv.martyshenko
 */
class DefaultSwipeBackBehaviour<T>(
    private val swipeListenerProvider: (T, SwipeBackController) -> SwipeBackListener = { activity, swipeController ->
        DefaultSwipeBackListener(activity, swipeController)
    }
) : ActivityBehaviour<T>
        where T : AppCompatActivity, T : SwipeBackAware {

    override fun onCreate(activity: T) {
        super.onCreate(activity)

        if (activity.strategy == SwipeBackAware.Strategy.DISABLED) return

        val swipeBackController = activity.findSwipeBackController()
            ?: return

        swipeBackController.swipeBackLayout
            .setOnSwipeBackListener(swipeListenerProvider(activity, swipeBackController))
    }
}

/**
 * Слушатель свайпа по умолчанию
 *
 * @author kv.martyshenko
 */
class DefaultSwipeBackListener<T>(
    private val activity: T,
    private val swipeBackController: SwipeBackController
) : SwipeBackListener
        where T : AppCompatActivity, T : SwipeBackAware {

    override fun onViewPositionChanged(fractionAnchor: Float, fractionScreen: Float) {
        swipeBackController.animateSwipe(fractionScreen)
    }

    override fun onViewGoneBySwipe() {
        activity.finish()

        //R.anim.instant_fade_out - моментальное изменение прозрачности до 0
        //Требуется, чтобы окно активности не моргнуло после свайпа
        activity.overridePendingTransition(0, R.anim.swipeback_instant_fade_out)
    }
}