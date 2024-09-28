package ru.tensor.sbis.base_components.activity.behaviour

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.base_components.util.updateFullScreen
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityBehaviour

/**
 * Реализация поведения, устанавливающего полноэкранный режим.
 *
 * @author kv.martyshenko
 */
class FullScreenBehaviour(
    private val updateFullScreen: (AppCompatActivity) -> Unit = { act -> act.updateFullScreen() }
) : ActivityBehaviour<AppCompatActivity> {

    override fun onCreate(activity: AppCompatActivity) {
        updateFullScreen(activity)

        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val onConfigChangeListener = Consumer<Configuration> {
                updateFullScreen(activity)
            }

            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                activity.addOnConfigurationChangedListener(onConfigChangeListener)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                activity.removeOnConfigurationChangedListener(onConfigChangeListener)
                owner.lifecycle.removeObserver(this)
            }
        })
    }
}