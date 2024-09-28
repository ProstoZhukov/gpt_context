package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.di;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.util.di.PerActivity;
import ru.tensor.sbis.common.util.scroll.ScrollHelper;
import ru.tensor.sbis.common.util.uri.UriWrapper;
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent;
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper;
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.ConversationItemsViewPool;
import ru.tensor.sbis.communicator.themes_registry.contract.ThemesRegistryDependency;
import ru.tensor.sbis.communicator.declaration.counter.nav_counters.CommunicatorNavCounters;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationListAdapter;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationListInteractor;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationSettings;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeFragment;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.contract.ThemePresenter;
import ru.tensor.sbis.communicator.declaration.counter.CommunicatorCounterModel;
import ru.tensor.sbis.communicator.generated.DialogFolderController;
import ru.tensor.sbis.controller_utils.sync.AreaSyncStatusPublisher;
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider;
import ru.tensor.sbis.communicator.declaration.model.ChatType;
import ru.tensor.sbis.communicator.declaration.model.DialogType;
import ru.tensor.sbis.design.list_header.ListDateViewUpdater;
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper;

import static ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.di.ConversationListDiNamesKt.FRAGMENT_CONTEXT;

/**
 * DI компонент реестра диалогов/чатов
 *
 * @author rv.krohalev
 */
@PerActivity
@Component(
        modules = {ConversationListModule.class},
        dependencies = {
                CommunicatorCommonComponent.class,
                ThemesRegistryDependency.class
        }
)
public interface ConversationListComponent {

    void inject(ThemeFragment themeFragment);

    ConversationListAdapter getConversationListAdapter();

    ThemePresenter getThemePresenter();

    ListDateViewUpdater getListDateViewUpdater();

    ScrollHelper getScrollHelper();

    ConversationSettings getConversationettings();

    ConversationListInteractor getConversationListInteractor();

    AreaSyncStatusPublisher getAreaSyncStatusPublisher();

    UriWrapper getUriWrapper();

    @Nullable
    ImportContactsHelper.Provider getImportContactsHelperProvider();

    DependencyProvider<DialogFolderController> getDialogFolderController();

    CounterProvider<CommunicatorCounterModel> getCommunicatorCounterProvider();

    CommunicatorNavCounters getCommunicatorNavCounters();

    DependencyProvider<EmployeeProfileControllerWrapper> getEmployeeProfileControllerWrapper();

    ConversationItemsViewPool getItemsViewPool();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder context(@NonNull @Named(FRAGMENT_CONTEXT) Context context);

        @BindsInstance
        Builder dialogType(@Nullable DialogType dialogType);

        @BindsInstance
        Builder chatType(@Nullable ChatType chatType);

        @BindsInstance
        Builder initAsChat(@NonNull @Named(ConversationListDiNamesKt.INIT_AS_CHAT) Boolean initAsChat);

        @BindsInstance
        Builder initAsSharing(@NonNull @Named(ConversationListDiNamesKt.INIT_AS_SHARING) Boolean initAsSharing);

        @SuppressWarnings("NullableProblems")
        @BindsInstance
        Builder tablet(@NonNull @Named(ConversationListDiNamesKt.DIALOGS_TABLET) Boolean isTablet);

        @SuppressWarnings("NullableProblems")
        Builder communicatorCommonComponent(@NonNull CommunicatorCommonComponent component);

        @SuppressWarnings("NullableProblems")
        Builder communicatorDialogChatDependency(@NonNull ThemesRegistryDependency dependency);

        ConversationListComponent build();

    }
}