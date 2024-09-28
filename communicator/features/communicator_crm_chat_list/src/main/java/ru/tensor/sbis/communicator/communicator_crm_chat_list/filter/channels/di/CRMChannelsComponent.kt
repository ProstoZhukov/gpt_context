package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.di

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CRMChannelsController
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CRMChannelsView
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CrmChannelsListSectionClickDelegate
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListNotificationHelper
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import java.util.UUID

/**
 * DI компонент модуля.
 * @author da.zhukov
 */
@CRMChannelsScope
@Component(
    modules = [CRMChannelsModule::class],
    dependencies = [CommonSingletonComponent::class]
)
internal interface CRMChannelsComponent {

    fun injector(): Injector

    val themedContext: SbisThemedContext

    val viewFactory: (View) -> CRMChannelsView

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance scope: LifecycleCoroutineScope,
            @BindsInstance viewModelStoreOwner: ViewModelStoreOwner,
            commonSingletonComponent: CommonSingletonComponent,
            @BindsInstance listCase: CrmChannelListCase,
            @BindsInstance clickDelegate: CrmChannelsListSectionClickDelegate,
            @BindsInstance selectedItems: MutableLiveData<List<UUID>>,
            @BindsInstance crmChatListNotificationHelper: CRMChatListNotificationHelper
        ): CRMChannelsComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(
            fragment: Fragment,
            viewFactory: (View) -> CRMChannelsView
        ): CRMChannelsController
    }
}
