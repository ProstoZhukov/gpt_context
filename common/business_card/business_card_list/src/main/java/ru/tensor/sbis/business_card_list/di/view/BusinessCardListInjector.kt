package ru.tensor.sbis.business_card_list.di.view

import android.view.View
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.business_card_list.presentation.controller.BusinessCardListController
import ru.tensor.sbis.business_card_list.presentation.view.BusinessCardListFragment
import ru.tensor.sbis.business_card_list.presentation.view.BusinessCardListView

/**@SelfDocumented*/
@AssistedFactory
internal interface BusinessCardListInjector {

    /**@SelfDocumented*/
    fun inject(
        fragment: BusinessCardListFragment,
        viewFactory: (View) -> BusinessCardListView,
    ): BusinessCardListController
}