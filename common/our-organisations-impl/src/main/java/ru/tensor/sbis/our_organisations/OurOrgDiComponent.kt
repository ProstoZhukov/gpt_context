package ru.tensor.sbis.our_organisations

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.our_organisations.feature.di.OurOrgComponent
import ru.tensor.sbis.our_organisations.presentation.list.di.OurOrgListModule
import javax.inject.Singleton
import ru.tensor.sbis.our_organisations.presentation.list.di.OurOrgListComponent as OurOrgListComponentNew

/**
 * Контракт для DI - компонента.
 *
 * @author mv.ilin
 */
@Singleton
@Component(modules = [OurOrgDiModule::class, OurOrgListModule::class])
abstract class OurOrgDiComponent : OurOrgComponent {

    @Component.Factory
    internal interface Factory {
        fun create(
            @BindsInstance commonSingletonComponent: CommonSingletonComponent
        ): OurOrgDiComponent
    }

    internal abstract fun ourOrgListModuleNew(): OurOrgListComponentNew.Factory
}
