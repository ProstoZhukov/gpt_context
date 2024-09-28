package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.di;

import static ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency;
import static ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.di.ConversationListDiNamesKt.FRAGMENT_CONTEXT;
import static ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.di.ConversationListDiNamesKt.INIT_AS_CHAT;
import static ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.di.ConversationListDiNamesKt.INIT_AS_SHARING;
import static ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.di.ConversationListDiNamesKt.METADATA_OBSERVABLE;
import static ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeFragmentKt.EXPECTED_VISIBLE_SWIPE_MENU_ITEM_COUNT;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import javax.inject.Named;
import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;
import ru.tensor.sbis.base_components.adapter.checkable.ObservableCheckItemsHelper;
import ru.tensor.sbis.base_components.adapter.checkable.impl.ObservableCheckItemsHelperImpl;
import ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker;
import ru.tensor.sbis.common.util.DeviceConfigurationUtils;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.common.util.ResourceProvider;
import ru.tensor.sbis.common.util.UUIDUtils;
import ru.tensor.sbis.common.util.di.PerActivity;
import ru.tensor.sbis.common.util.scroll.ScrollHelper;
import ru.tensor.sbis.communicator.common.crud.ThemeRepository;
import ru.tensor.sbis.communicator.common.data.model.NetworkAvailability;
import ru.tensor.sbis.communicator.common.data.theme.ConversationMapper;
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel;
import ru.tensor.sbis.communicator.common.dialog_selection.DialogSelectionResult;
import ru.tensor.sbis.communicator.common.push.MessagesPushManager;
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.ConversationItemsViewPool;
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer;
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper;
import ru.tensor.sbis.communicator.core.data.model.ServiceAvailability;
import ru.tensor.sbis.communicator.generated.ChatController;
import ru.tensor.sbis.communicator.generated.DialogController;
import ru.tensor.sbis.communicator.generated.DialogFilter;
import ru.tensor.sbis.communicator.generated.DialogFolderController;
import ru.tensor.sbis.communicator.generated.MessageController;
import ru.tensor.sbis.communicator.generated.UnreadCountersController;
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade;
import ru.tensor.sbis.communicator.declaration.counter.nav_counters.CommunicatorNavCounters;
import ru.tensor.sbis.communicator.themes_registry.data.mapper.DialogFolderMapperNew;
import ru.tensor.sbis.communicator.themes_registry.router.theme.ThemeRouter;
import ru.tensor.sbis.communicator.themes_registry.router.theme.ThemeRouterImpl;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationListAdapter;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationListInteractor;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationListInteractorImpl;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationSettings;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.folders.ThemeFoldersInteractor;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.folders.ThemeFoldersInteractorImpl;
import ru.tensor.sbis.communicator.common.util.SwipeMenuViewPoolLifecycleManager;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeListCache;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeListCommand;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeListFilter;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemePresenterImpl;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.UnreadFilterConversationItemKeeper;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.contract.ThemePresenter;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.scroll.ScrollToConversationDelegate;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.scroll.ScrollToConversationDelegateImpl;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.stubs.ThemeStubHelper;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.stubs.ThemeStubHelperImpl;
import ru.tensor.sbis.communicator.declaration.counter.CommunicatorCounterModel;
import ru.tensor.sbis.communicator.themes_registry.utils.counter.CommunicatorCounterRepository;
import ru.tensor.sbis.communicator.themes_registry.utils.counter.navigation.CommunicatorNavCountersImpl;
import ru.tensor.sbis.controller_utils.sync.AreaSyncStatusPublisher;
import ru.tensor.sbis.controller_utils.sync.ControllerAreaSyncStatusPublisher;
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider;
import ru.tensor.sbis.communicator.declaration.model.ChatType;
import ru.tensor.sbis.communicator.declaration.model.DialogType;
import ru.tensor.sbis.design.list_header.ListDateViewUpdater;
import ru.tensor.sbis.design.list_header.format.ListDateFormatter;
import ru.tensor.sbis.mvp.interactor.crudinterface.event.DefaultEventManagerServiceSubscriber;
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventManagerServiceSubscriber;
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager;
import ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager;
import ru.tensor.sbis.persons.ConversationRegistryItem;
import ru.tensor.sbis.platform.sync.generated.AreaSyncInformer;
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper;
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVmHelper;
import ru.tensor.sbis.swipeablelayout.util.SwipeableViewmodelsHolder;
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool;
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService;

/**
 * DI модуль реестра диалогов/чатов
 *
 * @author rv.krohalev
 */
@Module
class ConversationListModule {

    @Provides
    @PerActivity
    DependencyProvider<AreaSyncInformer> provideAreaSyncInformer() {
        return DependencyProvider.create(AreaSyncInformer::instance);
    }

    @Provides
    @PerActivity
    DependencyProvider<DialogFolderController> provideDialogFolderController() {
        return DependencyProvider.create(DialogFolderController::instance);
    }

    @Provides
    @PerActivity
    ThemePresenter provideThemeCrudFacade(
            @NotNull UnreadFilterConversationItemKeeper unreadFilterConversationItemKeeper,
            @NonNull ConversationListInteractor conversationListInteractor,
            @NonNull ConversationSettings conversationSettings,
            @NonNull CounterProvider<CommunicatorCounterModel> counterProvider,
            @NonNull NetworkUtils networkUtils,
            @Named(INIT_AS_CHAT) @NonNull Boolean initAsChat,
            @Named(INIT_AS_SHARING) @NonNull Boolean isSharingMode,
            @Nullable DialogType dialogType,
            @Nullable ChatType chatType,
            @NonNull MessagesPushManager messagesPushManager,
            @NonNull SelectionHelper<ConversationRegistryItem> selectionHelper,
            @NonNull ObservableCheckItemsHelper<ConversationRegistryItem> checkHelper,
            @NonNull ScrollHelper scrollHelper,
            @NotNull SubscriptionManager subscriptionManager,
            @NotNull ThemeListFilter themeListFilter,
            @NonNull MultiSelectionResultManager<DialogSelectionResult> dialogSelectionResultManager,
            @NonNull NetworkAvailability networkAvailability,
            @NonNull ServiceAvailability serviceAvailability,
            @NonNull AreaSyncStatusPublisher areaSyncStatusPublisher,
            @NonNull ThemeRouter router,
            @Named(METADATA_OBSERVABLE) @NonNull PublishSubject<Map<String, String>> metadataObservable,
            @NonNull ThemeStubHelper themeStubHelper,
            @NonNull ScrollToConversationDelegate scrollToConversationHelper,
            @NonNull ThemeFoldersInteractor foldersProvider,
            @NonNull AppLifecycleTracker appLifecycleTracker,
            @NonNull LocalFeatureToggleService localFeatureService
    ) {
        return new ThemePresenterImpl(
                unreadFilterConversationItemKeeper,
                themeListFilter,
                subscriptionManager,
                conversationSettings,
                networkUtils,
                scrollHelper,
                selectionHelper,
                conversationListInteractor,
                checkHelper,
                themesRegistryDependency.getRecipientSelectionResultManager(),
                dialogSelectionResultManager,
                messagesPushManager,
                networkAvailability,
                serviceAvailability,
                areaSyncStatusPublisher,
                router,
                themesRegistryDependency.getConversationEventsPublisher(),
                counterProvider,
                initAsChat,
                isSharingMode,
                dialogType,
                chatType,
                metadataObservable,
                themeStubHelper,
                scrollToConversationHelper,
                foldersProvider,
                appLifecycleTracker,
                localFeatureService
        );
    }

    @PerActivity
    @Provides
    ThemeListFilter provideListFilter() {
        return new ThemeListFilter();
    }

    @PerActivity
    @Provides
    @Named(METADATA_OBSERVABLE)
    PublishSubject<Map<String, String>> provideMetadataObservable() {
        return PublishSubject.create();
    }

    @PerActivity
    @Provides
    ThemeListCommand provideListCommand(
            @NotNull ThemeRepository themeRepository,
            @NotNull ConversationMapper conversationMapper,
            @NotNull ThemeListFilter themeListFilter,
            @NotNull ThemeListCache themeListCache,
            @Named(METADATA_OBSERVABLE) @NonNull PublishSubject<Map<String, String>> metadataObservable,
            @NotNull CommunicatorActivityStatusSubscriptionInitializer activityStatusSubscriptionsInitializer
    ) {
        return new ThemeListCommand(
                themeRepository,
                conversationMapper,
                themeListFilter,
                metadataObservable,
                themeListCache,
                activityStatusSubscriptionsInitializer
        );
    }

    @PerActivity
    @Provides
    ThemeListCache provideThemeListCache() {
        return new ThemeListCache();
    }

    @PerActivity
    @Provides
    UnreadFilterConversationItemKeeper provideUnreadFilterConversationItemKeeper(ThemeListCommand superCommand) {
        return new UnreadFilterConversationItemKeeper(superCommand);
    }

    @NonNull
    @Provides
    @PerActivity
    ThemeStubHelper provideThemeSubHelper(@Named(INIT_AS_SHARING) @NonNull Boolean isSharingMode) {
        return new ThemeStubHelperImpl(isSharingMode);
    }

    @Provides
    SwipeableVmHelper provideSwipeableVmHelper() {
        return new SwipeableVmHelper();
    }

    @Provides
    SwipeableViewmodelsHolder provideSwipeableVmKeeper() {
        return new SwipeableViewmodelsHolder();
    }

    @PerActivity
    @Provides
    EventManagerServiceSubscriber provideEventManagerSubscriber(Context context) {
        return new DefaultEventManagerServiceSubscriber(context);
    }

    @PerActivity
    @Provides
    SubscriptionManager provideSubscriptionManager(EventManagerServiceSubscriber eventManagerServiceSubscriber) {
        return new SubscriptionManager(eventManagerServiceSubscriber);
    }

    @Provides
    @PerActivity
    ConversationListAdapter provideConversationListAdapter(
            @NonNull ConversationItemsViewPool itemsViewPool,
            @NonNull SwipeMenuViewPool swipeMenuViewPool,
            @NonNull ListDateViewUpdater dateUpdater,
            @Named(INIT_AS_SHARING) @NonNull Boolean isSharingMode
    ) {
        return new ConversationListAdapter(
                itemsViewPool,
                swipeMenuViewPool,
                dateUpdater,
                isSharingMode
        );
    }

    @Provides
    @PerActivity
    @NonNull
    ConversationItemsViewPool provideConversationItemsViewPool(
            @NonNull @Named(FRAGMENT_CONTEXT) Context context
    ) {
        return new ConversationItemsViewPool(context, true);
    }

    @Provides
    @PerActivity
    ListDateViewUpdater provideListDateViewUpdater(
            @NonNull ListDateFormatter.TimeForTodayAndShortDateElse formatter
    ) {
        return new ListDateViewUpdater(formatter);
    }

    @Provides
    @PerActivity
    ThemeRouter provideThemeRouter() {
        return new ThemeRouterImpl();
    }

    @Provides
    @PerActivity
    NetworkAvailability provideNetworkAvailability() {
        return new NetworkAvailability();
    }

    @Provides
    @PerActivity
    ServiceAvailability provideServiceAvailability() {
        return new ServiceAvailability();
    }

    @Provides
    @PerActivity
    ScrollToConversationDelegate provideScrollToConversationHelper(@NonNull SelectionHelper<ConversationRegistryItem> selectionHelper) {
        return new ScrollToConversationDelegateImpl(
                themesRegistryDependency.getConversationEventsPublisher(),
                selectionHelper
        );
    }

    @NonNull
    @Provides
    @PerActivity
    SelectionHelper<ConversationRegistryItem> provideSelectionHelper(@NonNull Context context) {
        return new SelectionHelper<>(
                DeviceConfigurationUtils.isTablet(context),
                (item1, item2) -> UUIDUtils.equals(((ConversationModel) item1).getCompareUuid(), ((ConversationModel) item2).getCompareUuid())
        );
    }

    @NonNull
    @Provides
    @PerActivity
    ObservableCheckItemsHelper<ConversationRegistryItem> provideCheckHelper() {
        return new ObservableCheckItemsHelperImpl<>(entity -> entity);
    }

    @Provides
    @PerActivity
    SwipeMenuViewPoolLifecycleManager provideItemViewPoolWithSwipeableLayoutLifecycleManager(
            @NonNull SwipeMenuViewPool itemViewPool) {
        return new SwipeMenuViewPoolLifecycleManager(itemViewPool);
    }

    @NonNull
    @Provides
    @PerActivity
    ThemeFoldersInteractor provideFoldersProvider(
            DependencyProvider<DialogFolderController> controllerProvider,
            DialogFolderMapperNew mapper
    ) {
        return new ThemeFoldersInteractorImpl(
                controllerProvider,
                mapper,
                DialogFilter.ALL
        );
    }

    @NonNull
    @Provides
    @PerActivity
    ConversationSettings provideConversationSettings(@NonNull SharedPreferences sharedPreferences) {
        return new ConversationSettings(sharedPreferences);
    }

    @NonNull
    @Provides
    @PerActivity
    ConversationListInteractor provideConversationListInteractor(
            @NonNull DependencyProvider<DialogController> dialogController,
            @NonNull DependencyProvider<MessageController> messageController,
            @NonNull DependencyProvider<ChatController> chatControllerProvider,
            @NonNull ContactsControllerWrapper contactsControllerWrapperProvider,
            @NonNull DependencyProvider<EmployeeProfileControllerWrapper> employeeProfileControllerWrapperProvider,
            @NonNull ConversationMapper conversationMapper,
            @NonNull CounterProvider<CommunicatorCounterModel> counterProvider,
            @NonNull CommunicatorActivityStatusSubscriptionInitializer activityStatusSubscriptionsInitializer
    ) {
        return new ConversationListInteractorImpl(
                dialogController,
                messageController,
                chatControllerProvider,
                contactsControllerWrapperProvider,
                employeeProfileControllerWrapperProvider,
                conversationMapper,
                counterProvider,
                activityStatusSubscriptionsInitializer
        );
    }

    @NonNull
    @Provides
    @PerActivity
    AreaSyncStatusPublisher provideAreaSyncStatusPublisher(
            @NonNull DependencyProvider<AreaSyncInformer> areaSyncInformer
    ) {
        return new ControllerAreaSyncStatusPublisher(areaSyncInformer);
    }

    @NonNull
    @Provides
    @PerActivity
    DialogFolderMapperNew provideDialogFolderMapperNew(
            ResourceProvider resourceProvider
    ) {
        return new DialogFolderMapperNew(resourceProvider);
    }

    @Provides
    @PerActivity
    SwipeMenuViewPool provideSwipeMenuViewPool(@NonNull Context context) {
        return SwipeMenuViewPool.createForItemsWithIcon(context, EXPECTED_VISIBLE_SWIPE_MENU_ITEM_COUNT);
    }

    @NonNull
    @Provides
    @PerActivity
    CommunicatorNavCounters provideCommunicatorNavCounters(CounterProvider<CommunicatorCounterModel> counterProvider) {
        return new CommunicatorNavCountersImpl(counterProvider);
    }

    @NonNull
    @Provides
    @PerActivity
    CounterProvider<CommunicatorCounterModel> provideCommunicatorCounterProvider() {
        return ThemesRegistryFacade.INSTANCE.getCommunicatorCounterProvider();
    }

    @NonNull
    @Provides
    @PerActivity
    CommunicatorCounterRepository provideCommunicatorCountersRepository(@NonNull DependencyProvider<UnreadCountersController> unreadCountersController) {
        return new CommunicatorCounterRepository(unreadCountersController);
    }

    @NonNull
    @Provides
    @PerActivity
    LocalFeatureToggleService provideLocalFeatureToggleService(
            Context context
    )  {
        return new LocalFeatureToggleService(context);
    }
}