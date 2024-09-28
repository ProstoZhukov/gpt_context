package ru.tensor.sbis.business_card.di.view

import android.view.View
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.business_card.presentation.controller.BusinessCardController
import ru.tensor.sbis.business_card.presentation.view.BusinessCardFragment
import ru.tensor.sbis.business_card.presentation.view.BusinessCardView

/**@SelfDocumented*/
@AssistedFactory
internal interface BusinessCardInjector {

    /**@SelfDocumented*/
    fun inject(
        fragment: BusinessCardFragment,
        viewFactory: (View) -> BusinessCardView,
    ): BusinessCardController
}