@file:Suppress("unused")

package ru.tensor.sbis.communicator.themes_registry.router.theme.delegates

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorThemesRouter
import ru.tensor.sbis.communicator.common.util.fragment.BaseFragmentWrapper
import ru.tensor.sbis.communicator.common.util.fragment.BaseFragmentWrapperImpl
import ru.tensor.sbis.communicator.themes_registry.router.theme.ThemeRouterInitializer
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeFragment

/**
 * Базовый функционал делегата роутера реестра диалогов
 * @property fragmentWrapper обертка базового фрагмента для доступа к контексту
 *
 * @author vv.chekurda
 */
internal abstract class BaseThemeRouterDelegate private constructor(
    private val fragmentWrapper: BaseFragmentWrapper
) : BaseFragmentWrapper by fragmentWrapper,
    ThemeRouterInitializer,
    LifecycleObserver {

    constructor() : this(
        fragmentWrapper = BaseFragmentWrapperImpl()
    )

    /**
     * Делегат общей навигации модуля коммуникатора
     */
    protected var communicatorThemesRouter: CommunicatorThemesRouter? = null

    /**
     * Производит вызов с безопасным обращением к контексту фрагмента
     *
     * @param action действие, которое будет выполнено при наличии контекста
     */
    protected inline fun <reified T> safeContext(action: () -> T) {
        fragment ?: return
        action.invoke()
    }

    @CallSuper
    override fun initRouter(
        fragment: ThemeFragment
    ) {
        fragment.lifecycle.addObserver(this)
        fragmentWrapper.fragment = fragment
        this.communicatorThemesRouter = fragment
    }

    /**
     * Очистка зависимостей и ссылок при уничтожении активности
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        fragmentWrapper.clearReferences()
        communicatorThemesRouter = null
    }
}