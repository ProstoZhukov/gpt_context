package ru.tensor.sbis.business_card_host.di.view

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.business_card_host.di.BusinessCardHostComponent
import ru.tensor.sbis.business_card_host.presentation.controller.BusinessCardHostController
import ru.tensor.sbis.business_card_host.presentation.view.BusinessCardHostFragment
import java.util.UUID

/**@SelfDocumented*/
@BusinessCardHostViewScope
@Component(
    modules = [(BusinessCardHostViewModule::class)],
    dependencies = [(BusinessCardHostComponent::class)]
)
internal interface BusinessCardHostViewComponent {

    /**@SelfDocumented*/
    fun injector(): Injector

    /**@SelfDocumented*/
    fun inject(fragment: BusinessCardHostFragment)

    /**@SelfDocumented*/
    @Component.Factory
    interface Factory {
        fun create(
            component: BusinessCardHostComponent,
            @BindsInstance viewModelStoreOwner: ViewModelStoreOwner,
            @BindsInstance scope: LifecycleCoroutineScope,
            @BindsInstance nestedContainerId: Int,
            @BindsInstance personUUID: UUID
        ): BusinessCardHostViewComponent
    }

    @AssistedFactory
    interface Injector {

        /**@SelfDocumented*/
        fun inject(
            fragment: BusinessCardHostFragment,
            viewFactory: (View) -> BusinessCardHostFragment
        ): BusinessCardHostController
    }
}