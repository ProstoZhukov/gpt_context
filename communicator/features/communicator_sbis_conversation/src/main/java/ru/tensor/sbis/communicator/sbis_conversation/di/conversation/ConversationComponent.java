package ru.tensor.sbis.communicator.sbis_conversation.di.conversation;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStoreOwner;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.util.ClipboardManager;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer;
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper;
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper;
import ru.tensor.sbis.communicator.generated.DialogDocumentController;
import ru.tensor.sbis.communicator.sbis_conversation.contract.CommunicatorSbisConversationDependency;
import ru.tensor.sbis.communicator.common.conversation.ConversationToolbarEventManager;
import ru.tensor.sbis.communicator.common.crud.ThemeRepository;
import ru.tensor.sbis.communicator.generated.ChatController;
import ru.tensor.sbis.communicator.generated.DialogController;
import ru.tensor.sbis.communicator.generated.MessageController;
import ru.tensor.sbis.communicator.generated.ThemeParticipantsController;
import ru.tensor.sbis.communicator.sbis_conversation.data.CoreConversationInfo;
import ru.tensor.sbis.communicator.sbis_conversation.di.singleton.CommunicatorSbisConversationSingletonComponent;
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationContract;
import ru.tensor.sbis.communicator.sbis_conversation.ui.viewmodel.ConversationViewModel;
import ru.tensor.sbis.common.util.di.PerActivity;
import ru.tensor.sbis.design.list_header.ListDateViewUpdater;
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper;

/**
 * DI компонент экрана сообщений сбис.
 *
 * @author vv.chekurda
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
@ConversationScope
@Component(
        modules = {
                ConversationModule.class
        },
        dependencies = {
                CommunicatorSbisConversationSingletonComponent.class
        }
    )
@PerActivity
public interface ConversationComponent {

    ConversationContract.ConversationPresenter getConversationPresenter();
    NetworkUtils getNetworkUtils();
    ClipboardManager getClipboardManager();
    ThemeRepository getThemeRepository();
    DependencyProvider<DialogController> getDialogControllerDependencyProvider();
    DependencyProvider<DialogDocumentController> getDialogDocumentControllerDependencyProvider();
    DependencyProvider<ChatController> getChatControllerDependencyProvider();
    DependencyProvider<MessageController> getMessageControllerDependencyProvider();
    DependencyProvider<ThemeParticipantsController> getThemeParticipantsControllerDependencyProvider();
    ContactsControllerWrapper getContactsControllerWrapperDependency();
    DependencyProvider<EmployeeProfileControllerWrapper> getEmployeeProfileControllerWrapperDependencyProvider();
    Context getContext();
    ConversationToolbarEventManager getConversationToolbarEventManager();
    CommunicatorSbisConversationDependency getCommunicatorSbisConversationDependency();
    ListDateViewUpdater getListDateViewUpdater();
    CommunicatorActivityStatusSubscriptionInitializer getCommunicatorActivityStatusSubscriptionInitializer();

    @Named("CoreConversationInfo")
    CoreConversationInfo getInitialCoreConversationInfo();

    QuickShareHelper getDirectShareHelper();

    @Component.Builder
    interface Builder {

        @BindsInstance Builder viewModelStoreOwner(@NonNull ViewModelStoreOwner viewModelStoreOwner);
        @BindsInstance Builder conversationOpenData(@NonNull @Named("CoreConversationInfo") CoreConversationInfo coreConversationInfo);
        @BindsInstance Builder viewModel(@NonNull ConversationViewModel conversationViewModel);
        Builder sbisConversationSingletonComponent(@NonNull CommunicatorSbisConversationSingletonComponent component);

        ConversationComponent build();
    }
}
