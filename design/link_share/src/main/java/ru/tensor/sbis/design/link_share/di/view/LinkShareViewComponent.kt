package ru.tensor.sbis.design.link_share.di.view

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.android_ext_decl.AndroidComponent
import ru.tensor.sbis.design.link_share.di.LinkShareComponent
import ru.tensor.sbis.design.link_share.presentation.view.LinkShareFragment
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareParams

/**@SelfDocumented*/
@LinkShareViewScope
@Component(
    modules = [(LinkShareViewModule::class)],
    dependencies = [(LinkShareComponent::class)]
)
internal interface LinkShareViewComponent {

    /**@SelfDocumented*/
    fun inject(fragment: LinkShareFragment)

    /**@SelfDocumented*/
    @Component.Factory
    interface Factory {
        fun create(
            linkShareComponent: LinkShareComponent,
            @BindsInstance androidComponent: AndroidComponent,
            @BindsInstance params: SbisLinkShareParams
        ): LinkShareViewComponent
    }
}