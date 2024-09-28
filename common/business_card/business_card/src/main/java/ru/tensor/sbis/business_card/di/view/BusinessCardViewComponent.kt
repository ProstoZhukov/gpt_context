package ru.tensor.sbis.business_card.di.view

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.android_ext_decl.AndroidComponent
import ru.tensor.sbis.business_card.di.BusinessCardComponent
import ru.tensor.sbis.business_card.presentation.view.BusinessCardFragment

/**@SelfDocumented*/
@BusinessCardViewScope
@Component(
    modules = [(BusinessCardViewModule::class)],
    dependencies = [(BusinessCardComponent::class)]
)
internal interface BusinessCardViewComponent {

    /**@SelfDocumented*/
    fun inject(fragment: BusinessCardFragment)

    /**@SelfDocumented*/
    @Component.Factory
    interface Factory {
        fun create(
            component: BusinessCardComponent,
            @BindsInstance androidComponent: AndroidComponent,
            @BindsInstance nestedContainerId: Int
        ): BusinessCardViewComponent
    }
}