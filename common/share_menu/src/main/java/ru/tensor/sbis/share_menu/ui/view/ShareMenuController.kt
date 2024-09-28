package ru.tensor.sbis.share_menu.ui.view

import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator
import ru.tensor.sbis.mvi_extension.subscribe
import ru.tensor.sbis.share_menu.ui.store.ShareMenuStore
import ru.tensor.sbis.share_menu.ui.store.domain.executor.Intent
import ru.tensor.sbis.share_menu.ui.store.domain.Label
import ru.tensor.sbis.share_menu.utils.ShareAnalyticsHelper
import ru.tensor.sbis.share_menu.utils.ShareMenuDelegateInjector

/**
 * MVI-Controller компонента меню для "поделиться".
 *
 * @param fragment фрагмент меню.
 * @param storeFactory фабрика для создания стора.
 * @param intentFactory фабрика для создания интентов.
 * @param analyticsHelper вспомогательная реализация для аналитики.
 * @property router роутер экрана.
 *
 * @author vv.chekurda
 */
internal class ShareMenuController @AssistedInject constructor(
    @Assisted fragment: Fragment,
    storeFactory: ShareMenuStore.Factory,
    intentFactory: Intent.Factory,
    analyticsHelper: ShareAnalyticsHelper,
    private val router: ShareMenuRouter
) {
    init {
        val store = fragment.provideStore { storeFactory.create() }
        val contentDelegate = store.menuContentDelegate

        router.attachNavigator(WeakLifecycleNavigator(entity = fragment))
        fragment.attachBinder(viewFactory = { view -> ShareMenuView(fragment, view) }) { view ->
            val environment = Label.Environment(view, router, contentDelegate, analyticsHelper)
            bind {
                view.events.map(intentFactory::create) bindTo store
                store.states.map { it.uiState } bindTo view
                store.labels bindTo { label -> label.perform(environment) }
            }
        }

        val menuDelegateInjector = ShareMenuDelegateInjector(contentDelegate)
        menuDelegateInjector.register(fragment)
        fragment.lifecycle.subscribe(
            onDestroy = { menuDelegateInjector.unregister(fragment) }
        )
    }

    /**
     * Обработать нажатие кнопки назад.
     */
    fun onBackPressed(): Boolean {
        val isHandled = router.onBackPressed()
        if (!isHandled) router.finishTask()
        return true
    }

    /**
     * Обработать закрытие шторки.
     */
    fun onCloseContent() {
        router.finishTask()
    }
}