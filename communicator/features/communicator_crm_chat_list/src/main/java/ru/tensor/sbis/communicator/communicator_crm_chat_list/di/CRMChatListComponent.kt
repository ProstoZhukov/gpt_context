package ru.tensor.sbis.communicator.communicator_crm_chat_list.di

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communication_decl.crm.CRMChatListParams
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListController
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListNotificationHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListView
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.CRMDeeplinkActionHandler
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import java.util.UUID

/**
 * DI компонент модуля.
 * @author da.zhukov
 */
@CRMChatListScope
@Component(
    modules = [CRMChatListModule::class],
    dependencies = [CommonSingletonComponent::class]
)
internal interface CRMChatListComponent {

    fun injector(): Injector

    val themedContext: SbisThemedContext

    val viewFactory: (View) -> CRMChatListView

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance scope: LifecycleCoroutineScope,
            @BindsInstance listDateFormatter: ListDateFormatter.DateTimeWithoutTodayStandard,
            @BindsInstance viewModelStoreOwner: ViewModelStoreOwner,
            @BindsInstance crmChatListNotificationHelper: CRMChatListNotificationHelper,
            @BindsInstance crmChatListParams: CRMChatListParams,
            commonSingletonComponent: CommonSingletonComponent
        ): CRMChatListComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(
            fragment: Fragment,
            viewFactory: (View) -> CRMChatListView,
            deeplinkActionHandler: CRMDeeplinkActionHandler,
            isHistoryMode: Boolean,
            consultationUuid: UUID?
        ): CRMChatListController
    }
}
