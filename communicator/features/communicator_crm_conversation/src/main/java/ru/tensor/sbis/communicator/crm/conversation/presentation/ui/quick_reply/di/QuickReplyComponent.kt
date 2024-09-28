package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.di

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.QuickReplyController
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.QuickReplyView
import ru.tensor.sbis.communicator.declaration.crm.model.QuickReplyParams

/**
 * @author dv.baranov
 */

/** @SelfDocumented */
@QuickReplyScope
@Component(
    dependencies = [CommonSingletonComponent::class],
    modules = [QuickReplyModule::class],
)
internal interface QuickReplyComponent {

    fun injector(): Injector

    val viewFactory: (View) -> QuickReplyView

    @Component.Factory
    interface Factory {
        fun create(
            commonSingletonComponent: CommonSingletonComponent,
            @BindsInstance viewModelStoreOwner: ViewModelStoreOwner,
            @BindsInstance quickReplyParams: QuickReplyParams,
            @BindsInstance scope: LifecycleCoroutineScope,
        ): QuickReplyComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(
            fragment: Fragment,
            viewFactory: (View) -> QuickReplyView,
        ): QuickReplyController
    }
}
