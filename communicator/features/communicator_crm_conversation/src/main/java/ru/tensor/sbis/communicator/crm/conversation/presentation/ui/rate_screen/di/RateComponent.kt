package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui.RateController
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui.RateView

/**
 * @author dv.baranov
 */

/** @SelfDocumented */
@RateScope
@Component(
    dependencies = [CommonSingletonComponent::class],
    modules = [RateModule::class],
)
internal interface RateComponent {

    fun injector(): Injector

    val viewFactory: RateView.Factory

    @Component.Factory
    interface Factory {
        fun create(
            commonSingletonComponent: CommonSingletonComponent,
            @BindsInstance viewFactory: RateView.Factory,
        ): RateComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: Fragment): RateController
    }
}
