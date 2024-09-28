package ru.tensor.sbis.wizard.impl.di

import android.content.Context
import android.os.Bundle
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.wizard.decl.WizardSteps
import ru.tensor.sbis.wizard.impl.WizardFragment
import ru.tensor.sbis.wizard.impl.WizardPresenter

/**
 * DI компонент мастера
 *
 * @author sa.nikitin
 */
@WizardDIScope
@Component(
    dependencies = [],
    modules = [WizardDIModule::class]
)
internal interface WizardDIComponent {

    val presenter: WizardPresenter

    fun inject(wizardFragment: WizardFragment)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance appContext: Context,
            @BindsInstance steps: WizardSteps,
            @BindsInstance savedState: Bundle?,
            @BindsInstance wizardBackStackName: String?
        ): WizardDIComponent
    }
}