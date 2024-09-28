package ru.tensor.sbis.base_components.activity.behaviour

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.base_components.keyboard.KeyboardAware
import ru.tensor.sbis.base_components.keyboard.KeyboardDetector
import ru.tensor.sbis.base_components.keyboard.createDispatcherToNestedFragment
import ru.tensor.sbis.base_components.keyboard.createViewHeightResizer
import ru.tensor.sbis.base_components.keyboard.keyboardDetector
import ru.tensor.sbis.base_components.keyboard.manageBy
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityBehaviour


/**
 * Реализация поведения AdjustResize.
 * Активность должна декларировать windowSoftInputMode="adjustResize".
 *
 * @author kv.martyshenko
 */
class AdjustResizeBehaviour<in A>(
    private val keyboardDetectorFactory: (A, FrameLayout) -> KeyboardDetector = { activity, container ->
        activity.keyboardDetector(
            rootViewProvider = { container },
            delegate = activity.createDispatcherToNestedFragment(
                container.id,
                activity.createViewHeightResizer(viewProvider = { container })
            )
        )
    }
)  : ActivityBehaviour<A>
        where A: AppCompatActivity, A: KeyboardAware {

    override fun onReady(activity: A, contentView: FrameLayout, savedState: Bundle?) {
        keyboardDetectorFactory(activity, contentView)
            .manageBy(activity.lifecycle)
    }

}