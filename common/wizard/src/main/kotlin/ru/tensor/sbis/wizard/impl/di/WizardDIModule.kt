package ru.tensor.sbis.wizard.impl.di

import android.os.Bundle
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.wizard.decl.step.StepHolder
import ru.tensor.sbis.wizard.impl.BackNavigationEventHandler
import ru.tensor.sbis.wizard.impl.WizardPresenter
import ru.tensor.sbis.wizard.impl.router.WizardRouter
import ru.tensor.sbis.wizard.impl.router.WizardRouterImpl
import ru.tensor.sbis.wizard.impl.state.StateCapsule
import ru.tensor.sbis.wizard.impl.state.StateProviderRegistry
import ru.tensor.sbis.wizard.impl.state.StateRestorer
import ru.tensor.sbis.wizard.impl.state.StateSaver
import ru.tensor.sbis.wizard.impl.state.WizardPresenterState

/**
 * DI модуль мастера
 *
 * @author sa.nikitin
 */
@Module(includes = [WizardDIModule.BindsDIModule::class])
internal class WizardDIModule {

    @Provides
    @WizardDIScope
    fun stateCapsule(savedState: Bundle?): StateCapsule<WizardPresenterState> =
        StateCapsule(WizardPresenterState::class.java.simpleName, savedState)

    @Suppress("unused")
    @Module
    interface BindsDIModule {

        @Binds
        @WizardDIScope
        fun asRouter(instance: WizardRouterImpl): WizardRouter

        @Binds
        @WizardDIScope
        fun asStateProviderRegistry(instance: StateCapsule<WizardPresenterState>): StateProviderRegistry<WizardPresenterState>

        @Binds
        @WizardDIScope
        fun asStateSaver(instance: StateCapsule<WizardPresenterState>): StateSaver

        @Binds
        @WizardDIScope
        fun asStateRestorer(instance: StateCapsule<WizardPresenterState>): StateRestorer<WizardPresenterState>

        @Binds
        @WizardDIScope
        fun asStepHolder(instance: WizardPresenter): StepHolder

        @Binds
        @WizardDIScope
        fun asBackNavigationEventHandler(instance: WizardPresenter): BackNavigationEventHandler
    }
}