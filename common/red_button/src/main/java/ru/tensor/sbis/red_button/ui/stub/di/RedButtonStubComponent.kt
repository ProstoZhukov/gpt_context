package ru.tensor.sbis.red_button.ui.stub.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import ru.tensor.sbis.red_button.di.RedButtonComponent
import ru.tensor.sbis.red_button.di.RedButtonModule
import ru.tensor.sbis.red_button.ui.stub.RedButtonStubActivity
import ru.tensor.sbis.red_button.ui.stub.RedButtonStubViewModel

/**
 * DI компонент для экрана заглушки красной кнопки
 *
 * @author ra.stepanov
 */
@StubScope
@Component(
    dependencies = [RedButtonComponent::class],
    modules = [AndroidInjectionModule::class, RedButtonModule::class, RedButtonStubModule::class]
)
interface RedButtonStubComponent {

    /** @SelfDocumented */
    fun inject(activity: RedButtonStubActivity)

    /** @SelfDocumented */
    val viewModel: RedButtonStubViewModel

    @Component.Factory
    interface Factory {

        /** @SelfDocumented */
        fun create(
            redButtonComponent: RedButtonComponent,
            @BindsInstance activity: RedButtonStubActivity
        ): RedButtonStubComponent

    }
}