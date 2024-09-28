package ru.tensor.sbis.red_button.ui.host.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.red_button.RedButtonDependency
import ru.tensor.sbis.red_button.di.RedButtonComponent
import ru.tensor.sbis.red_button.repository.RedButtonRepository
import ru.tensor.sbis.red_button.ui.host.HostFragment

/**
 * Компонент для фрагментов "Красной Кнопки", включает в себя модули фрагментов
 *
 * @author ra.stepanov
 */
@HostScope
@Component(
    dependencies = [RedButtonComponent::class],
    modules = [HostModule::class]
)
internal interface HostFragmentComponent {

    /**@SelfDocumented */
    fun inject(fragment: HostFragment)

    /**@SelfDocumented */
    val redButtonRepository: RedButtonRepository

    /**@SelfDocumented */
    val dependency: RedButtonDependency

    @Component.Factory
    interface Factory {

        /** @SelfDocumented */
        fun create(
            redButtonComponent: RedButtonComponent,
            @BindsInstance fragment: HostFragment
        ): HostFragmentComponent
    }
}