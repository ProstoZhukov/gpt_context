package ru.tensor.sbis.main_screen_common.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.main_screen_decl.MainScreenLifecycleDelegate

/**
 * Метод для автоматического управления виджетом главного экрана на основе жизненного цикла компонента.
 *
 * @param lifecycleOwner
 *
 * @author kv.martyshenko
 */
fun MainScreenLifecycleDelegate.manageBy(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            this@manageBy.setup()
        }

        override fun onStart(owner: LifecycleOwner) {
            this@manageBy.activate()
        }

        override fun onResume(owner: LifecycleOwner) {
            this@manageBy.resume()
        }

        override fun onPause(owner: LifecycleOwner) {
            this@manageBy.pause()
        }

        override fun onStop(owner: LifecycleOwner) {
            this@manageBy.deactivate()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            this@manageBy.reset()
            owner.lifecycle.removeObserver(this)
        }
    })
}
