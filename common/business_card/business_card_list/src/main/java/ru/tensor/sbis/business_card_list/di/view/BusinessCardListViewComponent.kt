package ru.tensor.sbis.business_card_list.di.view

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.business_card_list.di.BusinessCardListComponent
import ru.tensor.sbis.business_card_list.presentation.controller.BusinessCardListController
import ru.tensor.sbis.business_card_list.presentation.view.BusinessCardListFragment
import ru.tensor.sbis.business_card_list.presentation.view.BusinessCardListView
import java.util.UUID

/**@SelfDocumented*/
@BusinessCardListViewScope
@Component(
    modules = [(BusinessCardListViewModule::class)],
    dependencies = [(BusinessCardListComponent::class)]
)
internal interface BusinessCardListViewComponent {

    /**@SelfDocumented*/
    fun injector(): Injector

    /**@SelfDocumented*/
    fun inject(fragment: BusinessCardListView)

    /**@SelfDocumented*/
    @Component.Factory
    interface Factory {
        fun create(
            component: BusinessCardListComponent,
            @BindsInstance viewModelStoreOwner: ViewModelStoreOwner,
            @BindsInstance scope: LifecycleCoroutineScope,
            @BindsInstance nestedContainerId: Int,
            @BindsInstance personUUID: UUID
        ): BusinessCardListViewComponent
    }

    @AssistedFactory
    interface Injector {

        /**@SelfDocumented*/
        fun inject(
            fragment: BusinessCardListFragment,
            viewFactory: (View) -> BusinessCardListView
        ): BusinessCardListController
    }
}