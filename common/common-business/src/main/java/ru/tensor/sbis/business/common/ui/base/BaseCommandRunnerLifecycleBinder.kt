package ru.tensor.sbis.business.common.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.mvvm.utils.BaseCommandRunner

/**
 * Метод активирует автоматическое включение и отключение роутера на основании событий жизненного цикла [OWNER]
 *
 * @author as.chadov
 */
fun <OWNER : LifecycleOwner> BaseCommandRunner<OWNER>.manageBy(lifecycleOwner: OWNER) {
    if (hasLifecycleHandler) {
        return
    }
    lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onResume(owner: LifecycleOwner) {
            when (lifecycleOwner) {
                is AppCompatActivity -> this@manageBy.resume(
                    lifeCycleCallbackHolder = lifecycleOwner,
                    fragmentManager = lifecycleOwner.supportFragmentManager
                )

                is Fragment -> this@manageBy.resume(
                    lifeCycleCallbackHolder = lifecycleOwner,
                    fragmentManager = lifecycleOwner.childFragmentManager
                )
            }
        }

        override fun onPause(owner: LifecycleOwner) {
            this@manageBy.pause()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            lifecycleOwner.lifecycle.removeObserver(this)
            this@manageBy.hasLifecycleHandler = false
        }
    })
    /** Отмечаем что имеется поставщик событий жизненного цикла для предотвращения множества подписок */
    hasLifecycleHandler = true
}