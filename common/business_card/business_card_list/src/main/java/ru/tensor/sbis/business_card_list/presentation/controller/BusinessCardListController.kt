package ru.tensor.sbis.business_card_list.presentation.controller

import android.view.View
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.tensor.sbis.business_card_list.contract.internal.list.BusinessCardListRouter
import ru.tensor.sbis.business_card_list.presentation.view.BusinessCardListFragment
import ru.tensor.sbis.business_card_list.presentation.view.BusinessCardListView
import ru.tensor.sbis.business_card_list.store.BusinessCardListStoreFactory
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator

/** Контроллер реестра визиток */
internal class BusinessCardListController @AssistedInject constructor(
    @Assisted private val fragment: BusinessCardListFragment,
    @Assisted private val viewFactory: (View) -> BusinessCardListView,
    private val storeFactory: BusinessCardListStoreFactory,
    private val router: BusinessCardListRouter
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