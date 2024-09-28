package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.di

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui.CRMAnotherOperatorController
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui.CRMAnotherOperatorView
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import java.util.UUID

/**
 * DI компонент модуля.
 * @author da.zhukov
 */
@CRMAnotherOperatorScope
@Component(
    modules = [CRMAnotherOperatorModule::class],
    dependencies = [CommonSingletonComponent::class]
)
internal interface CRMAnotherOperatorComponent {

    fun injector(): Injector

    val themedContext: SbisThemedContext

    val viewFactory: (View) -> CRMAnotherOperatorView

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance scope: LifecycleCoroutineScope,
            @BindsInstance viewModelStoreOwner: ViewModelStoreOwner,
            commonSingletonComponent: CommonSingletonComponent,
            @BindsInstance params: CRMAnotherOperatorParams,
        ): CRMAnotherOperatorComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(
            fragment: Fragment,
            viewFactory: (View) -> CRMAnotherOperatorView,
        ): CRMAnotherOperatorController
    }
}