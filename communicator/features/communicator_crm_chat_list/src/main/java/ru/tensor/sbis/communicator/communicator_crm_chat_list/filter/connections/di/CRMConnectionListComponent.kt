package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.di

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.CRMConnectionListFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui.CRMConnectionListController
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui.CRMConnectionListView
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import java.util.UUID
import javax.inject.Scope

/**
 * DI scope модуля.
 * @author da.zhukov
 */
@Scope
@Retention
internal annotation class CRMConnectionListScope

/**
 * DI компонент модуля.
 * @author da.zhukov
 */
@CRMConnectionListScope
@Component(
    modules = [CRMConnectionListModule::class],
    dependencies = [CommonSingletonComponent::class]
)
internal interface CRMConnectionListComponent {

    fun injector(): Injector

    val viewFactory: (View) -> CRMConnectionListView

    val themedContext: SbisThemedContext

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance fragment: CRMConnectionListFragment,
            @BindsInstance viewModelStoreOwner: ViewModelStoreOwner,
            @BindsInstance scope: LifecycleCoroutineScope,
            commonSingletonComponent: CommonSingletonComponent,
            @BindsInstance selectedItems: MutableLiveData<List<UUID>>
        ): CRMConnectionListComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(
            fragment: CRMConnectionListFragment,
            viewFactory: (View) -> CRMConnectionListView
        ): CRMConnectionListController
    }
}        