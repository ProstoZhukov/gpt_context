package ru.tensor.sbis.mvvm

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import ru.tensor.sbis.mvvm.utils.BaseCommandRunner
import javax.inject.Inject

/**
 * Обертка над [BaseCommandRunner] связывающая исполнителя команд с жизненнымо циклом [fragment] при инъекции
 * Используется в модуле мотивации
 *
 * @author as.chadov
 */
@Deprecated(
    "Не рекомендуется использовать, возможна потеря контекста и утечки при перезапуске процесса приложения",
    ReplaceWith("ru.tensor.sbis.business.common.ui.base.BaseCommandRunner<*>.manageBy()")
)
class CommandRunnerHandler<FRAGMENT : Fragment> @Inject constructor(fragment: FRAGMENT) :
    BaseCommandRunner<FRAGMENT>() {

    init {
        fragment.lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            private fun register() {
                resume(fragment, fragment.childFragmentManager)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            private fun unregister() {
                pause()
            }
        })
    }
}