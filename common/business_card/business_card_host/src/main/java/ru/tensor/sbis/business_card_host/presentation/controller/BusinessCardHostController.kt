package ru.tensor.sbis.business_card_host.presentation.controller

import android.view.View
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.tensor.sbis.business_card_host.contract.internal.list.BusinessCardHostRouter
import ru.tensor.sbis.business_card_host.presentation.view.BusinessCardHostFragment
import ru.tensor.sbis.business_card_host.store.BusinessCardHostStoreFactory
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator

/** Контроллер хоста визиток */
internal class BusinessCardHostController @AssistedInject constructor(
    @Assisted private val fragment: BusinessCardHostFragment,
    @Assisted private val viewFactory: (View) -> BusinessCardHostFragment,
    private val storeFactory: BusinessCardHostStoreFactory,
    private val router: BusinessCardHostRouter
) {
   private val store = fragment.provideStore { storeFactory.create() }

    init {
        router.attachNavigator(WeakLifecycleNavigator(fragment))
        fragment.attachBinder(
            BinderLifecycleMode.START_STOP,
            viewFactory
        ) {
            bind {
                store.labels bindTo { it.handle(router) }
            }
        }
    }
}