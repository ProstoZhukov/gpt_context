package ru.tensor.sbis.business_card_host.di.view

import android.view.View
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.business_card_host.presentation.controller.BusinessCardHostController
import ru.tensor.sbis.business_card_host.presentation.view.BusinessCardHostFragment

/**@SelfDocumented*/
@AssistedFactory
internal interface BusinessCardHostInjector {

    /**@SelfDocumented*/
    fun inject(
        fragment: BusinessCardHostFragment,
        viewFactory: (View) -> BusinessCardHostFragment
    ): BusinessCardHostController
}