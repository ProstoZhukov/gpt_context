package ru.tensor.sbis.design.link_share.presentation.controller

import android.view.View
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.tensor.sbis.design.link_share.contract.internal.LinkShareRouter
import ru.tensor.sbis.design.link_share.presentation.view.LinkShareFragment
import ru.tensor.sbis.design.link_share.presentation.view.LinkShareView
import ru.tensor.sbis.design.link_share.store.LinkShareStoreFactory
import ru.tensor.sbis.design.link_share.utils.LinkShareParamsProviderImpl
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareParams
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator

/**@SelfDocumented*/
internal class LinkShareController @AssistedInject constructor(
    @Assisted private val fragment: LinkShareFragment,
    @Assisted private val params: SbisLinkShareParams,
    @Assisted private val viewFactory: (View) -> LinkShareView,
    private val storeFactory: LinkShareStoreFactory,
    private val router: LinkShareRouter,
) {
    private val store = fragment.provideStore {
        storeFactory.create()
    }

    init {
        router.attachNavigator(WeakLifecycleNavigator(fragment))
        fragment.attachBinder(
            BinderLifecycleMode.START_STOP,
            viewFactory
        ) { view ->
            bind {
                view.events bindTo store
                store.states bindTo view
                store.labels bindTo { label -> label.handle(router, LinkShareParamsProviderImpl(params)) }
            }
        }
    }
}