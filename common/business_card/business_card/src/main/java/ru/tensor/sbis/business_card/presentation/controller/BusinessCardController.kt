package ru.tensor.sbis.business_card.presentation.controller

import android.view.View
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.tensor.sbis.business_card.contract.internal.BusinessCardRouter
import ru.tensor.sbis.business_card.presentation.view.BusinessCardFragment
import ru.tensor.sbis.business_card.presentation.view.BusinessCardView
import ru.tensor.sbis.business_card.store.BusinessCardStoreFactory
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator

/** Контроллер карточки визитки */
internal class BusinessCardController @AssistedInject constructor(
    @Assisted private val fragment: BusinessCardFragment,
    @Assisted private val viewFactory: (View) -> BusinessCardView,
    private val storeFactory: BusinessCardStoreFactory,
    private val router: BusinessCardRouter
) {

    private val store = fragment.provideStore { storeFactory.create() }

    init {
        router.attachNavigator(WeakLifecycleNavigator(fragment))
        fragment.attachBinder(
            BinderLifecycleMode.START_STOP,
            viewFactory
        ) { view ->
            bind {
                view.events bindTo store
                store.states bindTo view
                store.labels bindTo { it.handle(router) }
            }
        }
    }
}