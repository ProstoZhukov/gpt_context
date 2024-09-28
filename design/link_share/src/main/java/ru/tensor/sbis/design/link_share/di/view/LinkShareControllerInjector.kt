package ru.tensor.sbis.design.link_share.di.view

import android.view.View
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.design.link_share.presentation.controller.LinkShareController
import ru.tensor.sbis.design.link_share.presentation.view.LinkShareFragment
import ru.tensor.sbis.design.link_share.presentation.view.LinkShareView
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareParams

/**@SelfDocumented*/
@AssistedFactory
internal interface LinkShareControllerInjector {

    /**@SelfDocumented*/
    fun inject(
        fragment: LinkShareFragment,
        params: SbisLinkShareParams,
        viewFactory: (View) -> LinkShareView,
    ): LinkShareController
}