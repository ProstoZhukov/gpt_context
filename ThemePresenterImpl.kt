package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.MotionEvent
import androidx.tracing.trace
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.attachments.decl.AllowedActionResolver
import ru.tensor.sbis.attachments.decl.isFolder
import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListComponentConfig
import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListEntity
import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListParams
import ru.tensor.sbis.base_components.adapter.checkable.ObservableCheckItemsHelper
import ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.rx.scheduler.TensorSchedulers
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.common.util.logAndIgnoreError
import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.base_folders.ROOT_FOLDER_UUID
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.ChangeChatsFilter
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.ChangeDialogsFilter
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.ChooseFromPersonSuggestView
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.DialogsChooseMassOperation
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.GoToFoldersDialogs
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.MassOperationReadTheme
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.MassOperationRemoveTheme
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.MassOperationUnreadTheme
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.MoveDialogsToFoldersByMassOperation
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.MoveDialogsToFoldersBySwipe
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.OpenedDialogsFolders
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.RecoverTheme
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.SearchChats
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.SearchDialogs
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.SwipeReadTheme
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.SwipeRemoveTheme
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.SwipeUnreadTheme
import ru.tensor.sbis.communicator.common.analytics.ThemeAnalyticsEvent.SwitchMessageRee
import ru.tensor.sbis.communicator.common.conversation.ConversationEventsPublisher
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.ThemeConversationPreviewMenuAction
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.BaseConversationPreviewMenuAction.Delete
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.BaseConversationPreviewMenuAction.Go
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.ThemeConversationPreviewMenuAction.MarkDialog
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.ThemeConversationPreviewMenuAction.MarkGroupAsRead
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.ThemeConversationPreviewMenuAction.MarkGroupAsUnread
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.ThemeConversationPreviewMenuAction.MoveGroupToFolder
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.ThemeConversationPreviewMenuAction.Pin
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.BaseConversationPreviewMenuAction.Report
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.ThemeConversationPreviewMenuAction.Restore
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.BaseConversationPreviewMenuAction.Unpin
import ru.tensor.sbis.communicator.common.data.ConversationDetailsParams
import ru.tensor.sbis.communicator.common.data.model.NetworkAvailability
import ru.tensor.sbis.communicator.common.data.theme.ConversationButton
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.data.theme.FoldersConversationRegistryItem
import ru.tensor.sbis.communicator.common.data.theme.StubConversationRegistryItem
import ru.tensor.sbis.communicator.common.dialog_selection.CancelDialogSelectionResult
import ru.tensor.sbis.communicator.common.dialog_selection.DialogSelectionResult
import ru.tensor.sbis.communicator.common.dialog_selection.SelectedDialogResult
import ru.tensor.sbis.communicator.common.dialog_selection.SelectedParticipantsResult
import ru.tensor.sbis.communicator.common.navigation.data.CommunicatorArticleDiscussionParams
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.common.push.ThemeSubscribeFromNotification
import ru.tensor.sbis.communicator.common.push.ThemeUnsubscribeFromNotification
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.core.data.model.ServiceAvailability
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsDispatcher
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsType.FIREBASE_DELETE_DIALOG
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsType.FIREBASE_READ_DIALOG_BY_SWIPE_MENU
import ru.tensor.sbis.communicator.core.utils.MessageUtils
import ru.tensor.sbis.communicator.declaration.counter.CommunicatorCounterModel
import ru.tensor.sbis.communicator.declaration.model.ChatType
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.declaration.model.DialogType
import ru.tensor.sbis.communicator.declaration.model.EntitledItem
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryPlugin.customizationOptions
import ru.tensor.sbis.communicator.themes_registry.router.theme.ThemeRouter
import ru.tensor.sbis.communicator.themes_registry.router.theme.ThemeRouterInitializer
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.ThemeConversationParams
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.isConsultation
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.isConversation
import ru.tensor.sbis.communicator.themes_registry.ui.communication.ConversationRecipientSelectionFragmentCreator.Companion.TABLET_NEW_CONVERSATION_RECIPIENT_SELECTION
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationListInteractor
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationSettings
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationSettings.CHATS_FILTER_ALL
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationSettings.CHATS_FILTER_HIDDEN
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationSettings.CHATS_FILTER_NOT_READ
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationSettings.DIALOGS_FILTER_ALL
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationSettings.DIALOGS_FILTER_DELETED
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationSettings.DIALOGS_FILTER_INCOME
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationSettings.DIALOGS_FILTER_NOT_READ
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationSettings.DIALOGS_FILTER_NO_ANSWER
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.DialogsStubModel
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.folders.ThemeFoldersInteractorImpl
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.PagingLoadingStatus.ERROR
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.PagingLoadingStatus.LOADING
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.PagingLoadingStatus.NOT_LOADING
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeBottomCheckAction.DELETE_GROUP
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeBottomCheckAction.MARK_GROUP_AS_READ
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeBottomCheckAction.MARK_GROUP_AS_UNREAD
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeBottomCheckAction.MOVE_GROUP
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.contract.ThemePresenter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.contract.ThemeView
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.ConversationPrefetchDelegate
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.scroll.ScrollToConversationActions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.scroll.ScrollToConversationDelegate
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.stubs.ThemeStubHelper
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.stubs.ThemeStubHelperImpl
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.utils.toDialogFilter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.filters.ConversationFilterConfiguration
import ru.tensor.sbis.communicator.themes_registry.utils.counter.CommunicatorCounterProvider
import ru.tensor.sbis.controller_utils.sync.AreaSyncStatusPublisher
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.HandlePushNotificationDeeplinkAction
import ru.tensor.sbis.deeplink.OpenConversationDeeplinkAction
import ru.tensor.sbis.deeplink.OpenNewsDeepLinkAction
import ru.tensor.sbis.deeplink.OpenProfileDeeplinkAction
import ru.tensor.sbis.deeplink.OpenWebViewDeeplinkAction
import ru.tensor.sbis.deeplink.SwitchThemeTabDeeplinkAction
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.support.FoldersProvider
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.localfeaturetoggle.data.FeatureSet
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseSearchableListAbstractTwoWayPaginationPresenter
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.ConversationRegistryItem
import ru.tensor.sbis.platform.sync.generated.AreaStatus
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.stubview.R as RStubView
import ru.tensor.sbis.edo_decl.document.DocumentType as DeclarationDocumentType

internal const val LIST_KEY = "list"
internal const val HAS_MORE_KEY = "has_more"
private const val DEBOUNCE_HANDLER_VALUE_FOR_ANALYTICS_SEND = 3000L
private const val READ_NOTIFICATION_DELAY = 2000L
private const val MAX_ITEMS_FOR_SHARING = 20

/**
 * Презентер, реализующий управление фрагментом реестра диалогов/чатов [ThemeFragment].
 *
 * @author rv.krohalev
 */
internal class ThemePresenterImpl(
    private val listCommandAndKeeper: UnreadFilterConversationItemKeeper,
    private val listFilter: ThemeListFilter,
    subscriptionManager: SubscriptionManager,
    private val conversationSettings: ConversationSettings,
    networkUtils: NetworkUtils,
    scrollHelper: ScrollHelper,
    private val selectionHelper: SelectionHelper<ConversationRegistryItem>,
    private val conversationListInteractor: ConversationListInteractor,
    private val checkHelper: ObservableCheckItemsHelper<ConversationRegistryItem>,
    private val recipientSelectionManager: RecipientSelectionResultManager,
    private val dialogSelectionResultManager: MultiSelectionResultManager<DialogSelectionResult>,
    private val messagesPushManager: MessagesPushManager,
    private val networkAvailability: NetworkAvailability,
    private val serviceAvailability: ServiceAvailability,
    private val areaSyncStatusPublisher: AreaSyncStatusPublisher,
    private val router: ThemeRouter,
    private val conversationEventsPublisher: ConversationEventsPublisher,
    unreadCounterProvider: CounterProvider<CommunicatorCounterModel>,
    private val initAsChat: Boolean,
    private val isSharingMode: Boolean,
    presetDialogType: DialogType?,
    presetChatType: ChatType?,
    metadataObservable: Observable<Map<String, String>>,
    themeStubHelper: ThemeStubHelper,
    scrollToConversationDelegate: ScrollToConversationDelegate,
    private val foldersProvider: FoldersProvider,
    private val appLifecycleTracker: AppLifecycleTracker,
    private val localFeatureService: LocalFeatureToggleService? = null,
    searchMessagesThemeUuid: UUID? = null
) : BaseSearchableListAbstractTwoWayPaginationPresenter<
        ThemeView, ConversationRegistryItem, ThemeListFilter, ThemeFilter,
        DataRefreshedThemeControllerCallback>(listFilter, subscriptionManager, networkUtils, scrollHelper),
    ThemePresenter,
    ScrollToConversationDelegate by scrollToConversationDelegate,
    ThemeRouterInitializer by router,
    ThemeStubHelper by themeStubHelper {

    companion object {
        private const val DELAY_FOR_CLOSING_SWIPE = 600L
        private const val TABLET_DIALOG_SELECTION_DEBOUNCE = 200L
        private const val MAX_SEARCH_CONTACTS_COUNT_TO_DISPLAY = 20
        private const val DIALOG_SYNC_AREA = "dialog"
        private const val CHAT_SYNC_AREA = "chat"
        private const val ITEMS_RESERVE = 15
        private const val NO_RESOURCE = -1

        // События
        private const val NETWORK_EVENT_KEY = "network"
        private const val OTHER_EVENT_KEY = "other"
        private const val AVAILABLE_EVENT_VALUE = "available"
        private const val ERROR_EVENT_KEY = "error"
        private const val REGISTRY_CHANGED_EVENT_KEY = "registry"
        private const val REGISTRY_CHANGED_EVENT_VALUE = "changed"
        private const val REGISTRY_SEARCH_EVENT_KEY = "search"
        private const val REGISTRY_SEARCH_EVENT_ACTIVE_VALUE = "active"
        private const val REGISTRY_SEARCH_EVENT_PAUSED_VALUE = "paused"
        private const val REGISTRY_SEARCH_EVENT_FINISHED_VALUE = "finished"
        private const val OLDER_ADDED_EVENT_VALUE = "tail_added"
        private const val AFFECTED_THEMES_EVENT_KEY = "affected_themes"
        private const val AFFECTED_THEMES_EVENT_VALUE_NONE = "none"
        private const val THEME_EVENT_KEY = "theme"
        private const val THEME_TYPE_EVENT_KEY = "theme_type"
        private const val THEME_TYPE_EVENT_CHAT_VALUE = "chat"
        private const val SYNC_TYPE_EVENT_KEY = "sync_type"
        private const val SYNC_TYPE_PARTIAL_EVENT_VALUE = "partial"
        private const val SEARCH_REQUEST_ID_KEY = "search_request_id"
        private const val SEARCH_MESSAGES_TOTAL_LIST_COUNT = "total_list_count"
    }

    private var dialogType: DialogType
        get() = listFilter.filterConfiguration?.dialogType ?: DialogType.ALL
        set(value) {
            listFilter.filterConfiguration = listFilter.filterConfiguration?.copy(dialogType = value)
        }

    private var chatType: ChatType
        get() = listFilter.filterConfiguration?.chatType ?: ChatType.ALL
        set(value) {
            listFilter.filterConfiguration = listFilter.filterConfiguration?.copy(chatType = value)
        }

    private var currentTabId: NavxIdDecl = NavxId.DIALOGS

    private val rootFolderUuid = ROOT_FOLDER_UUID.toString()
    private var selectedFolder: Folder? = null
    private var selectedFolderUuid = rootFolderUuid
    private var isFolderTitleChanged = false
    private var registryIsHidden = false

    private var unreadChatsCount: Int = 0
    private var unreadDialogsCount: Int = 0
    private var unviewedChats: Int = 0
    private var unviewedDialogs: Int = 0

    private var selectedChatForHide: ConversationModel? = null

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val dataUpdatingDisposable = SerialDisposable()

    private val itemSelectionSubject = PublishSubject.create<ConversationModel>()

    private val conversationList = mutableListOf<ConversationRegistryItem>()

    private var lastClickedSwipedConversation: ConversationRegistryItem? = null

    private var lastSwipedDismissedUuid: UUID = UUIDUtils.NIL_UUID

    private var contactList: List<ContactVM> = CopyOnWriteArrayList()
    private var searchPanelContactsList: List<ContactVM> = CopyOnWriteArrayList()
    private val contactsObservable: Observable<List<ContactVM>>
        get() {
            return conversationListInteractor.getRecipientList(mSearchQuery, MAX_SEARCH_CONTACTS_COUNT_TO_DISPLAY)
        }

    // Происходит ли первый вызов attach view. Нужно для определения инициализирующей загрузки данных.
    private var firstAttachView: Boolean = true

    private val isSearchMessages: Boolean = searchMessagesThemeUuid != null
    private val isDefaultMode = !isSharingMode && !isSearchMessages

    /**
     * Условие поиска контактов: есть текст в поисковой строке, реестр диалогов, нет выбранных контактов, корневая папка.
     */
    private val isNeedToSearchContacts
        get() = mSearchQuery.isNotBlank() &&
            !isChannelTab() &&
            !isContactSelectedInSearchPanel &&
            selectedFolderUuid == rootFolderUuid &&
            isDefaultMode

    /**
     * Условие показа панели контактов:
     * есть фокус в поиске, пустой поиск, реестр диалогов, нет выбранных контактов, корневая папка.
     */
    private val canShowContactsSearchPanel
        get() = mFocusInFilterPanel &&
            mSearchQuery.isBlank() &&
            !isChannelTab() &&
            !isContactSelectedInSearchPanel &&
            selectedFolderUuid == rootFolderUuid &&
            isDefaultMode

    private var isContactSelectedInSearchPanel = false
    @Volatile private var searchRequestId: String? = null
    @Volatile private var pendingRefreshCallback: HashMap<String, String>? = null

    private val singleDisposables = CompositeDisposable()
    private val selectionDisposable = SerialDisposable().apply { storeIn(singleDisposables) }
    private val dialogActionsSerialDisposable = SerialDisposable().apply { storeIn(singleDisposables) }
    private val moveDialogDisposable = SerialDisposable().apply { storeIn(singleDisposables) }
    private val deleteDialogDisposable = SerialDisposable().apply { storeIn(singleDisposables) }
    private val hideConversationDisposable = SerialDisposable().apply { storeIn(singleDisposables) }
    private val checkCountDisposable = SerialDisposable().apply { storeIn(singleDisposables) }
    private val checkModeStateDisposable = SerialDisposable().apply { storeIn(singleDisposables) }
    private val deleteOrHideByDismissDisposable = SerialDisposable().apply { storeIn(singleDisposables) }
    private val searchContactsDisposable = SerialDisposable().apply { storeIn(singleDisposables) }
    private val syncErrorDelayDisposable = SerialDisposable().apply { storeIn(singleDisposables) }
    private val scheduleMarkRegistryViewedDisposable = SerialDisposable().apply { storeIn(singleDisposables) }
    private var mOnlyRootFolderDisposable = SerialDisposable().apply { storeIn(singleDisposables) }

    private val isTablet: Boolean get() = selectionHelper.isTablet

    private val DialogType.isUnread get() = this == DialogType.UNREAD
    private val DialogType.isDeleted get() = this == DialogType.DELETED

    private val analyticsUtil: AnalyticsUtil? = themesRegistryDependency.analyticsUtilProvider?.getAnalyticsUtil()
    private val debounceActionHandler: DebounceActionHandler = DebounceActionHandler(DEBOUNCE_HANDLER_VALUE_FOR_ANALYTICS_SEND)

    private var currentSyncArea: String = EMPTY
    private var currentSyncAreaDisposable: CompositeDisposable? = null

    private var needRestoreKeyboard = false
    private var viewIsResumed = false
    private var isFirstResume = true

    private var isDialogsAvailable = true
    private var isChannelsAvailable = true

    // Флаг чтобы не показывать ошибку синхронизации при переключении табов, если нет соединения
    private val ignoreSyncError = AtomicBoolean(false)

    // Флаг чтобы понять что происходит переключение табов диалоги / чаты. Некоторые события надо при этом игнорировать.
    // Установка и сброс этого флага происходит синхронно в одном методе [onBranchTypeTabClick].
    private var isBranchTypeChanging: Boolean = false

    private var previousUpdateConversationType: ConversationType? = null
    private var previousSelectedFolderUuid: String? = null
    private var previousSelectedPersonUuid: UUID? = null

    private var isFoldersEnabled: Boolean = false
    private var subscribed: Boolean = false
    private var actionOnListUpdate: (() -> Unit)? = null
    private var conversationPrefetchDelegate = ConversationPrefetchDelegate(checkHelper, handler)

    private val pagingLoadingObservable = BehaviorSubject.create<PagingLoadingStatus>()

    private var isDestroyed: Boolean = false

    private val accountPersonId: String? by lazy {
        themesRegistryDependency.loginInterface.currentPersonId
    }

    private var waitingNoticeSyncDeeplink: HandlePushNotificationDeeplinkAction? = null

    private var personFilter: PersonSuggestData? = null
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) onSearchPersonSelected(value)
        }

    private var conversationForPreview: ConversationModel? = null

    private val importContactsHelper
        get() = themesRegistryDependency.importContactsHelperProvider?.importContactsHelper

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val _suggestedPersons: MutableStateFlow<List<PersonSuggestData>> = MutableStateFlow(emptyList())
    private val _foundMessages: MutableStateFlow<List<UUID>> = MutableStateFlow(listOf(
        UUID.fromString("3657daa4-205b-4103-a43c-c22cd4cadfb3"),
        UUID.fromString("4130c3d4-66dc-49f2-987d-96f2a8ad0be3"),
        UUID.fromString("f21426dc-19f3-49ed-b477-48295a70f82e"),
        UUID.fromString("fd627690-9175-4f43-a69c-f4fc00b5d3db"),
    ))
    private val _totalFoundMessagesCount: MutableStateFlow<Int> = MutableStateFlow(0)

    private val filesTasksDialogFeatureOn: Boolean
        get() = themesRegistryDependency.sbisFeatureService?.isActive(FILES_TASKS_DIALOG_CLOUD_FEATURE) == true ||
            localFeatureService?.isFeatureActivated(FeatureSet.FILES_TASKS_DIALOG) == true

    init {
        onThemeTabBeforeOpen()

        mDataList = mutableListOf()
        subscribeOnItemSelection()
        subscribeOnMessageSentScroll()

        presetDialogType?.let {
            saveDialogsFilterState(it)
        }

        initSubscriptions(unreadCounterProvider)

        when {
            isSharingMode -> listFilter.limitItems = MAX_ITEMS_FOR_SHARING
            isSearchMessages -> {
                listFilter.searchMessagesThemeUuid = searchMessagesThemeUuid
                getSuggestPersons()
            }
            else -> collectTabSelection()
        }

        listFilter.filterConfiguration = ConversationFilterConfiguration(
            presetDialogType ?: getDialogTypeBySpinnerState(conversationSettings.getDialogsFilterState(accountPersonId)),
            presetChatType ?: getChatTypeBySpinnerState(conversationSettings.getChatsFilterState(accountPersonId)),
            null
        )

        singleDisposables += metadataObservable
            .doOnNext {
                if (it[LIST_KEY]?.toBoolean() == true) {
                    searchRequestId = it[SEARCH_REQUEST_ID_KEY]
                    // TODO https://online.sbis.ru/opendoc.html?guid=36cb3104-249f-4ba6-9f5f-25486242e8d5&client=3
                    if (searchRequestId?.equals(pendingRefreshCallback?.get(SEARCH_REQUEST_ID_KEY)) == true) {
                        onRefreshCallback(pendingRefreshCallback)
                        pendingRefreshCallback = null
                    }
                }
            }
            .observeOn(TensorSchedulers.androidUiScheduler)
            .subscribe(::handleMetaDataResult)

        if (initAsChat) {
            onBranchTypeTabClick(NavxId.CHATS)
        }
        startInitialLoading()

        subscribeOnRouteCallbacks()

        subscribeOnNotViewedRegistryItemsCounter()
        subscribeOnConversationClosedEvents()
        scope.launch {
            subscribeOnAppForegroundEvents()
        }

        setupComplainServiceCallback()
        // По умолчанию считаем папки доступными
        setFoldersEnabled(true)
    }

    private fun saveDialogsFilterState(dialogType: DialogType) {
        scope.launch(Dispatchers.IO) {
            conversationSettings.saveDialogsFilterState(getSpinnerStateByDialogType(dialogType), accountPersonId)
        }
    }

    private fun saveChatsFilterState(chatType: ChatType) {
        scope.launch(Dispatchers.IO) {
            conversationSettings.saveChatsFilterState(getSpinnerStateByChatType(chatType), accountPersonId)
        }
    }

    private fun startInitialLoading() {
        mSubscriptionManager.addConsumer(::onEvent)
        val batch: SubscriptionManager.Batch = mSubscriptionManager.batch()
        batch.manage(null, refreshCallbackSubscription, false)
        configureSubscriptions(batch)
        batch.doAfterSubscribing {
            mPrepared = true
        }
        batch.subscribe()
        subscribed = true
        updateDataList(true, initialLoading = true)
    }

    private fun collectTabSelection() {
        scope.launch {
            themesRegistryDependency.tabSelectionFlow
                .filterNotNull()
                .collect { tab ->
                    withContext(Dispatchers.Main) {
                        onBranchTypeTabClick(tab)
                    }
                }
        }
    }

    private fun String.toNavxID(): NavxId = when {
        NavxId.DIALOGS.matches(this) -> NavxId.DIALOGS
        NavxId.CHATS.matches(this) -> NavxId.CHATS
        else -> NavxId.DIALOGS
    }

    private fun handleMetaDataResult(metadata: Map<String, String>) {
        if (metadata.containsKey(ThemeStubHelperImpl.DUMMY_CODE) || currentStub != null) {
            mView?.showStub(createStub(metadata, isChannelTab()))
        }
        if (requiredExpandToolbar) mView?.showSearchPanel()
    }

    /**
     * Условие необходимости показа поисковой строки с фильтром из-под тулбара для сценраия перехода в пустой реестр,
     * где только заглушка и нет возможности сделать скролл для показа и смены фильтра
     * вернет true, если список пуст, нет выбранной папки, фильтр пустой, для реестров диалогов и чатов соответственно
     */
    private val requiredExpandToolbar
        get() = (mDataList.lastOrNull() !is ConversationModel && mDataList.lastOrNull() !is ContactVM && currentStub != null)
            && (chatType == ChatType.ALL && isChannelTab() || !isChannelTab())
            && (selectedFolderUuid == rootFolderUuid || isChannelTab())

    private fun subscribeOnRouteCallbacks() {
        router.routeCallback
            .subscribe(::handleRouteCallback)
            .storeIn(singleDisposables)

        router.closeCallback
            .subscribe {
                showBottomNavigation()
                if (it == ThemeRouteType.CONVERSATION && !isTablet) {
                    hideNewMessagePushes()
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                        mView?.hideKeyboard()
                    }
                }
            }.storeIn(singleDisposables)
    }

    private fun setupComplainServiceCallback() {
        themesRegistryDependency.complainServiceProvider?.also {
            it.getComplainService().getBlockedListChangedObservable()
                .subscribe { updateDataList(false) }
                .storeIn(singleDisposables)
        }
    }

    private fun handleRouteCallback(type: ThemeRouteType) {
        when (type) {
            ThemeRouteType.CONVERSATION,
            ThemeRouteType.CONSULTATION -> {
                when {
                    isTablet -> mView?.hideKeyboard()
                    else -> {
                        showNewMessagePushes()
                    }
                }
            }
            else -> Unit
        }
    }

    private fun subscribeOnItemSelection() {
        itemSelectionSubject.filter { viewIsResumed }
            .let {
                if (isTablet) {
                    it
                        .debounce(TABLET_DIALOG_SELECTION_DEBOUNCE, TimeUnit.MILLISECONDS, Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                } else {
                    it
                }
            }.subscribe(::handleItemSelection)
            .storeIn(singleDisposables)
    }

    /**
     * Подписка для планшета на скролл к выбранным в реестре перепискам, в которых пользователь отпарвляет сообщения
     */
    private fun subscribeOnMessageSentScroll() {
        subscribeOnScrollToConversation(
            object : ScrollToConversationActions {
                override val getDataList: () -> List<ConversationRegistryItem> = ::getDataList
                override val getListOffset: () -> Int = ::mDataListOffset
                override val getSearchQuery: () -> String = ::mSearchQuery
                override val scrollToTop: () -> Unit = ::onScrollToTopPressed
                override val scrollToPosition: (Int) -> Unit = { mView?.scrollToPosition(it) }
            }
        ).storeIn(singleDisposables)
    }

    override fun isDeleted(): Boolean = dialogType.isDeleted

    override fun onViewVisibilityChanged(isInvisible: Boolean) {
        if (customizationOptions.splittingChannelsAndDialogsEnabled) {
            registryIsHidden = isInvisible
            if (isInvisible) {
                mSubscriptionManager.pause()
                showNewMessagePushes()
            } else {
                checkAndScheduleMarkAllRegistryItemsAsViewed()
                mSubscriptionManager.resume()
                hideNewMessagePushes()
                updateDataList(true)
            }
        }
    }

    override fun sendAnalyticSwitchRee() {
        val typeRee = if (isChannelTab()) ConversationType.CHAT else ConversationType.DIALOG
        analyticsUtil?.sendAnalytics(SwitchMessageRee(getSimpleNameForAnalytic(), typeRee))
    }

    override fun sendAnalyticOpenedDialogsFolders() {
        analyticsUtil?.sendAnalytics(OpenedDialogsFolders(getSimpleNameForAnalytic()))
    }

    override fun collectAvailableTabs() {
        scope.launch {
            themesRegistryDependency.navigationServiceFeature?.getAvailableItemsFlow()
                ?.collect { tabs ->
                    updateViewByTabsAvailabilityChanges(tabs.map { Pair(it.itemId, it.isVisible) })
                }
        }
    }

    private suspend fun updateViewByTabsAvailabilityChanges(availableTabsIds: List<Pair<String, Boolean>>) {
        val isChannelsAvailable = availableTabsIds.containsId(NavxId.CHATS) || availableTabsIds.isEmpty()
        val isDialogsAvailable = availableTabsIds.containsId(NavxId.DIALOGS) || availableTabsIds.isEmpty()

        val onlyDialogsTabAvailable = !isChannelsAvailable && isDialogsAvailable
        if (!customizationOptions.splittingChannelsAndDialogsEnabled) {
            changeDialogsRegistryTabTitle(onlyDialogsTabAvailable)
        }
        val updateTabsAvailability = (isDialogsAvailable != this.isDialogsAvailable) ||
            (isChannelsAvailable != this.isChannelsAvailable)

        this.isChannelsAvailable = isChannelsAvailable
        this.isDialogsAvailable = isDialogsAvailable

        withContext(Dispatchers.Main) {
            if (updateTabsAvailability) {
                mView?.changeHeaderByTabsAvailabilityChanges(isDialogsAvailable, isChannelsAvailable)
                mView?.setUnreadAndUnviewedCounters(
                    unreadChatsCount,
                    unreadDialogsCount,
                    unviewedChats,
                    unviewedDialogs
                )
            }
            if (onlyDialogsTabAvailable) {
                themesRegistryDependency.setSelectedTabValue(NavxId.DIALOGS)
            }
        }
    }

    private fun List<Pair<String, Boolean>>.containsId(navxId: NavxId): Boolean =
        find { navxId.matches(it.first) }?.second ?: false

    private fun changeDialogsRegistryTabTitle(onlyDialogsTabAvailable: Boolean) {
        customizationOptions.dialogsRegistryTabTitle = if (onlyDialogsTabAvailable) {
            RCommunicatorDesign.string.communicator_conversation_tab_messages
        } else {
            RCommunicatorDesign.string.communicator_conversation_tab_dialogs
        }
    }

    override fun isImportContactsAllowed(): Boolean = isDefaultMode && importContactsHelper != null &&
        themesRegistryDependency.importContactsConfirmationFragmentFactory != null

    /**
     * Геттер для делегата по фильтру непрочитанных
     * @return [UnreadFilterConversationItemKeeper]
     */
    override fun getListObservableCommand() = listCommandAndKeeper

    // todo костыль (убрать после завершения https://online.sbis.ru/opendoc.html?guid=2be0a724-d19c-4d3f-a567-763054ad6f15)
    override fun isNeedAutoLoadingNextPage() = false

    /** @SelfDocumented */
    override fun swapDataList(dataList: MutableList<ConversationRegistryItem>) {
        changeFolderTitle()
        updateListItemsLayoutRules()

        if (dialogType.isUnread && !isChannelTab()) clearOutgoingDialogsFromUnreadFilter(dataList)

        // В методе searchContacts() мы вызываем swapDataList, передавая dataList, в котором могут быть контакты от предыдущего запроса
        // Поэтому нужно отфильтровать список, оставив только диалоги, а затем добавить в него новые контакты
        val filteredDataList: MutableList<ConversationRegistryItem> = dataList.filterIsInstance<ConversationModel>().toMutableList()

        if (isSearchMessages) {
            scope.launch {
                _foundMessages.emit(filteredDataList.mapNotNull { it.castTo<ConversationModel>()?.messageUuid })
            }
        }

        // Результаты с облака по контактам долетают позже, а на UI, например, мы уже стёрли строку поиска.
        // Поэтому очищаем список контактов, если в текущий момент строка поиска пустая.
        // Данный кейс актуален при быстром вводе и стирании символов
        if (mSearchQuery.isEmpty()) clearContactsList()
        // в начало списка диалогов добавляются найденные при поиске контакты, если таковые найдены
        if (contactList.isNotEmpty() && mDataListOffset == 0) {
            filteredDataList.addAll(0, contactList)
            mView?.setContentToAdapter(filteredDataList)
        }
        if (!isChannelTab()) {
            // Добавляем маркер необходимости показа компонента выбора папок на первой позиции, если необходимо
            if (isFoldersEnabled
                && listFilter.filterConfiguration?.folderUuid == rootFolderUuid
                && mDataListOffset == 0
                && (filteredDataList.isEmpty() || filteredDataList[0] != FoldersConversationRegistryItem)) {
                filteredDataList.add(0, FoldersConversationRegistryItem)
            }
            updateStubInList(filteredDataList)
        }
        conversationList.clear()
        conversationList.addAll(filteredDataList)
        super.swapDataList(filteredDataList)
    }

    private fun updateStubInList(list: MutableList<ConversationRegistryItem>) {
        // нужна ли заглушка вместе с компонентом папок
        if (mDataListOffset == 0 && list.size == 1 && currentStub != null && list.first() == FoldersConversationRegistryItem) {
            val actions = mapOf(RStubView.string.design_stub_view_no_connection_details_clickable to { onRefresh() })
            currentStub.toStubCaseContent(actions).let {
                list.add(DialogsStubModel(it))
            }
            return
        }

        // нужно ли убрать заглушку из списка
        if (currentStub == null || mDataListOffset > 0 || list.size > 2 || list.firstOrNull() != FoldersConversationRegistryItem) {
            for (i in 0 until min(2, list.size)) {
                val item = list[i]
                if (item is StubConversationRegistryItem) {
                    list.removeAt(i)
                    break
                }
            }
        }
    }

    private fun updateListItemsLayoutRules() {
        when {
            isSharingMode -> return
            isSearchMessages -> mView?.updateListItemsLayoutRules(
                showHeaderDate = false,
                showItemsCollages = true
            )
            isChannelTab() -> mView?.updateListItemsLayoutRules(
                showHeaderDate = true,
                showItemsCollages = true
            )
            else -> mView?.updateListItemsLayoutRules(
                showHeaderDate = contactList.isEmpty(),
                showItemsCollages = personFilter == null
            )
        }
    }

    private fun clearOutgoingDialogsFromUnreadFilter(dataList: MutableList<ConversationRegistryItem>) {
        // метод для удаления исходящих диалогов из списка UnreadFilterConversationItemKeeper, удерживаемых для фильтра "непрочитанные"
        // https://online.sbis.ru/opendoc.html?guid=0d78d93f-7f64-4bf5-979d-c91bafdc4f87
        val outGoingAndReadList = dataList.filterIsInstance<ConversationModel>()
            .filter { with(it) { !canBeMarkedUnread && isOutgoing && !isForMe } }
            .map { it.uuid }
        if (outGoingAndReadList.isNotEmpty()) listCommandAndKeeper.doNotKeep(outGoingAndReadList)
    }

    /** @SelfDocumented */
    override fun updateDataList(fromPullToRefresh: Boolean) {
        updateDataList(fromPullToRefresh, isReturningToTop = false)
    }

    /**
     * Переопределенный метод [updateDataList] с дополнительным параметром [isReturningToTop] для индикации возвращения
     * к началу списка после пагинаций.
     * Этот параметр необходим чтобы изменить направление обновления когда снова загружена первая страница.
     * // TODO при возможности отказаться от этого механизма в будущем
     */
    private fun updateDataList(fromPullToRefresh: Boolean, initialLoading: Boolean = false, isReturningToTop: Boolean = false) {
        mShowLoadingProcess = fromPullToRefresh
        mLoadingState = LoadingState.UPDATE
        mNeedUpdateDataList = false
        if (mShowLoadingProcess && mView != null) {
            mView!!.showLoading()
        }

        val conversationTypeChanged = checkConversationTypeChanged()
        val currentDataListSize = if (!conversationTypeChanged) dataList.size else 0
        val lastEntity: ConversationRegistryItem? = if (!conversationTypeChanged && !isReturningToTop) lastEntityForUpdate else null

        val requestItemsCount: Int =
            calculateItemsCount(conversationTypeChanged, lastEntity, currentDataListSize, fromPullToRefresh)

        val selectedFolderChanged = checkSelectedFolderChanged()
        val selectedPersonChanged = checkSelectedPersonChanged()

        val notifyDataSetChanged = conversationTypeChanged || selectedFolderChanged || selectedPersonChanged
        val isPullToRefresh = fromPullToRefresh || initialLoading
        val disposable = getUpdatingListByLastEntityObservable(lastEntity, requestItemsCount, isPullToRefresh)
            .doAfterTerminate {
                resetLoadingStatus()
                finalProcessUpdating()
                if (initialLoading) {
                    loadChannelsToCache()
                    onThemeTabAfterOpen()
                    syncContacts()
                }
            }
            .subscribe(
                { processUpdatingDataListResult(it, lastEntity != null, (initialLoading && dataList.isEmpty()) || !initialLoading && notifyDataSetChanged) },
                { processUpdatingDataListError(it) }
            )
        if (initialLoading) {
            disposable.storeIn(singleDisposables)
        } else {
            disposable.storeIn(dataUpdatingDisposable)
        }
    }

    /**
     * Синхронизировать реестр контактов.
     *
     * Костыль необходим для фикса тупящего выбора получателей исключительно на андроиде:
     * https://online.sbis.ru/opendoc.html?guid=c559924a-35cd-4145-b55c-95a67032acac
     * Синхронизацию можно будет выпилить после организации постраничной загрузки получателей:
     * https://online.sbis.ru/opendoc.html?guid=fb3f6a4f-d82e-45f7-86d7-9d9eedc23f42
     * P.S. синхронизация контактов из реестра диалогов была всегда, но в процессе всех оптимизаций ее спилили,
     * и получателям стало хуже. IOS ошибка не касается, поэтому лечим только на андроиде.
     */
    private fun syncContacts() {
        conversationListInteractor.syncContacts()
            .subscribe()
            .storeIn(singleDisposables)
    }

    private fun loadChannelsToCache() {
        val themeListCommand = listCommandAndKeeper.themeListCommand
        if (themeListCommand.getCache(forChannels = true) != null) return
        // Запускаем загрузку каналов когда главному потоку будет нечем заняться
        Looper.myQueue().addIdleHandler {
            val themeFilter = ThemeFilter().apply {
                themeType = ConversationType.CHAT
                chatFilter = this@ThemePresenterImpl.chatType.asControllerChatFilter()
                direction = QueryDirection.TO_OLDER
                count = listFilter.minItemsCount + itemsReserve + 1
                fromFavoriteTimestamp = 0
                inclusive = true
                needSync = true
            }
            // Вызываем refresh для каналов, по итогу данные попадут в кэш [themeListCommand.getCache()]
            themeListCommand.list(themeFilter)
                .subscribe({}) { Timber.e(it) }
                .storeIn(singleDisposables)
            // выполняем действие только один раз
            false
        }
    }

    override fun hasPartialPage(currentDataListSize: Int): Boolean {
        return if (currentDataListSize > 0 && dataList.firstOrNull() is FoldersConversationRegistryItem) {
            super.hasPartialPage(currentDataListSize - 1)
        } else {
            super.hasPartialPage(currentDataListSize)
        }
    }

    private fun checkSelectedFolderChanged(): Boolean {
        val selectedFolderUuid = listFilter.filterConfiguration?.folderUuid
        val selectedFolderChanged = previousSelectedFolderUuid != selectedFolderUuid
        previousSelectedFolderUuid = selectedFolderUuid
        return selectedFolderChanged
    }

    private fun checkConversationTypeChanged(): Boolean {
        val conversationType = listFilter.conversationType
        val conversationTypeChanged = previousUpdateConversationType != conversationType
        previousUpdateConversationType = conversationType
        return conversationTypeChanged
    }

    private fun checkSelectedPersonChanged(): Boolean {
        val selectedPersonUuid = listFilter.personUuid
        val selectedPersonUuidChanged = previousSelectedPersonUuid != selectedPersonUuid
        previousSelectedPersonUuid = selectedPersonUuid
        return selectedPersonUuidChanged
    }

    private fun calculateItemsCount(isInitial: Boolean, anchor: ConversationRegistryItem?, dataListSize: Int, fromPullToRefresh: Boolean) = when {
        // первоначальная загрузка - запрашиваем минимально возможное количество чтобы не тригернуть пагинацию
        isInitial                                         -> listFilter.minItemsCount + itemsReserve + 1
        anchor == null && fromPullToRefresh               -> pageSize
        // перезапрашиваем все
        anchor == null && dataListSize >= maxDataListSize -> maxDataListSize
        anchor == null                                    -> {
            val loadedPages = dataListSize / pageSize
            val addition = if (!hasData(dataListSize) || hasPartialPage(dataListSize)) 1 else 0
            val count = (loadedPages + addition) * pageSize
            // Бывает так что вызов рефреш приходит когда данных еще нет (например в офлайне по событиям AreaStatus)
            // Тогда возращаем такое же количество как при первоначальной загрузке вместо 0
            count.takeUnless { it == 0 } ?: (listFilter.minItemsCount + itemsReserve + 1)
        }
        // для реестра каналов есть закрепленные каналы, которые будут исчезать при обновлении списка в сторону to_newer,
        // если появляется новый канал, поэтому необходимо обновляться с запасом, если текущий offset == 0
        isChannelTab() && mDataListOffset == 0            -> dataListSize + pageSize
        else                                              -> dataListSize
    }

    private fun processUpdatingDataListResult(pagedListResult: PagedListResult<ConversationRegistryItem>, updatingFromTail: Boolean, notifyDataSetChanged: Boolean) {
        mView?.changeContactsSearchPanelVisibility(canShowContactsSearchPanel)
        restartProgressOnEmptySearchResult(pagedListResult)

        // Перестраховка. Нет 100% уверенности что данные о заглушках по шине [metadataObservable] будут всегда приходить раньше
        if (stubFromMetadata(pagedListResult.metaData, isChannelTab()) != currentStub) {
            createStub(pagedListResult.metaData, isChannelTab())
        }

        val oldList = ArrayList(mDataList)
        // TODO вызывается дважды https://online.sbis.ru/opendoc.html?guid=cf7fe9d3-7f3f-4856-9e16-c80f21fa1f13
        swapDataList(pagedListResult.dataList)

        pagedListResult.dataList = conversationList
        if (!isChannelTab() && needPreparePersonSuggest(oldList, conversationList)) {
            searchPanelContacts()
        }

        // код продублирован из super реализации
        val errorCode = pagedListResult.commandStatus?.errorCode
        val dataList = pagedListResult.dataList
        processUpdatingDataListResultWithoutViewUpdating(pagedListResult, updatingFromTail, errorCode)
        mView?.let { view ->
            // дублирование ради изменений в этой строке
            view.updateDataList(oldList, dataList, mDataListOffset, notifyDataSetChanged)
            view.showOlderLoadingProgress(mShowOlderProgress)
            view.showNewerLoadingProgress(mHasNewerPage)
            if (errorCode == ErrorCode.NETWORK_ERROR) {
                showEmptyViewIfNeeded(view, dataList, emptyViewErrorId, RCommon.string.common_no_network_available_check_connection)
            } else {
                showEmptyViewIfNeeded(view, dataList, emptyViewErrorId)
            }
        }

        onDataListUpdated()
        tryInvokeOnListUpdateAction()
    }

    private fun needPreparePersonSuggest(oldList: List<ConversationRegistryItem>, newList: List<ConversationRegistryItem>): Boolean {
        fun List<ConversationRegistryItem>.isNotEmptyAndContainsConversationModel(): Boolean {
            return this.isNotEmpty() && (this.firstOrNull { it is ConversationModel } as? ConversationModel) != null
        }
        val oldListEmptyOrContainsOnlyFolder = !oldList.isNotEmptyAndContainsConversationModel()
        val newListEmptyOrContainsOnlyFolder = !newList.isNotEmptyAndContainsConversationModel()

        fun List<ConversationRegistryItem>.firstConversationModel() =
            (this.first { it is ConversationModel } as ConversationModel)

        return when {
            oldListEmptyOrContainsOnlyFolder -> true
            newListEmptyOrContainsOnlyFolder -> false
            else -> oldList.firstConversationModel() != newList.firstConversationModel()
        }
    }

    /** @SelfDocumented */
    override fun processUpdatingDataListResult(pagedListResult: PagedListResult<ConversationRegistryItem>, updatingFromTail: Boolean) {
        processUpdatingDataListResult(pagedListResult, updatingFromTail, false)
    }

    override fun isNeedLoadNewerPage(firstVisibleItemPosition: Int): Boolean {
        return super.isNeedLoadNewerPage(firstVisibleItemPosition) && !isSharingMode
    }

    override fun loadNewerPage() {
        mLoadingState = LoadingState.TO_NEWER
        mLoadingPageSubscription.set(
            getLoadingNewerDataObservable(
                dataList.firstOrNull(),
                // метод переопределен чтобы всегда запрашивалось pageSize элементов
                pageSize
            )
                .doAfterTerminate { resetLoadingStatus() }
                .subscribe(
                    { pagedListResult ->
                        processLoadingNewerPageResult(pagedListResult)
                        finalProcessNewerPageLoading()
                    },
                    { throwable: Throwable ->
                        processLoadingNextPageError(throwable)
                        finalProcessNewerPageLoading()
                    }
                )
        )
    }

    override fun getLoadingNewerDataObservable(
        dataModel: ConversationRegistryItem?,
        itemsCount: Int
    ): Observable<out PagedListResult<ConversationRegistryItem>> {
        val builder = mFilter.queryBuilder().castTo<ThemeListFilter.ThemeListFilterBuilder>()!!
        builder.anchorModel(dataModel)
            // метод переопределен чтобы убрать дополнительную логику, изменяющую количество запрашиваемых элементов
            .itemsCount(itemsCount)
            .direction(QueryDirection.TO_NEWER)
            .inclusive(false)
        configureListQuery(builder)
        // TODO Проверить и убрать по задаче https://online.sbis.ru/opendoc.html?guid=2be0a724-d19c-4d3f-a567-763054ad6f15
        return if (!isNeedAutoLoadingNextPage) {
            if (mRefreshNewerPage) {
                listObservableCommand.refresh(builder.build())
            } else {
                mWaitingForOlderRefreshCallback = true
                listObservableCommand.list(builder.build())
            }
        } else {
            listObservableCommand.list(builder.build())
        }
    }

    override fun processLoadingNewerPageResult(pagedListResult: PagedListResult<ConversationRegistryItem>) {
        val oldDataOffset = mDataListOffset
        super.processLoadingNewerPageResult(pagedListResult)
        conversationList.clear()
        conversationList.addAll(dataList)

        // нужно снова показать папки и список контактов при возвращении к началу списка
        if (!isChannelTab() && mDataListOffset == 0 && oldDataOffset > 0) {
            resetPagination(false)
            updateDataList(false, isReturningToTop = true)
        }
    }

    override fun processLoadingOlderPageResult(pagedListResult: PagedListResult<ConversationRegistryItem>) {
        handleLastUnreadFilterItemIssue(pagedListResult)

        super.processLoadingOlderPageResult(pagedListResult)
        conversationList.clear()
        conversationList.addAll(dataList)
    }

    /**
     * В реестре непрочитанных своя логика, позволяющая сохранить в реестре только что прочитанный элемент.
     * Это реализовано передачей в методы контроллера списка additionalIds, которые должны подклеиться к результату,
     * см [UnreadFilterConversationItemKeeper].
     *
     * Но есть проблема с последним элементом в фильтре непрочитанных. После прочтения приходит рефреш по которому
     * запрашиваются данные от этого элемента в обе стороны. И в обоих результатах будет присутствовать последний элемент,
     * что приводит к его задваиванию. Для решения этой проблемы добалвлен этот метод, который уберет лишний элемент.
     */
    private fun handleLastUnreadFilterItemIssue(pagedListResult: PagedListResult<ConversationRegistryItem>) {
        val requestFilter = pagedListResult.castTo<ThemePagedListResult>()?.requestFilter ?: return
        if (requestFilter.isUnread() && !requestFilter.inclusive) {
            val lastCurrentUuid = dataList.lastOrNull()?.castTo<ConversationModel>()?.uuid ?: return
            if (requestFilter.additionalIds.contains(lastCurrentUuid)) {
                val firstLoaded = pagedListResult.dataList.firstOrNull()?.castTo<ConversationModel>() ?: return
                if (lastCurrentUuid == firstLoaded.uuid) {
                    // убираем дубль
                    pagedListResult.dataList.removeFirst()
                }
            }
        }
    }

    /**
     * Перезапуск прогресса загрузки на переходе от пустого списка к крутилке во время поиска.
     * Отложенный показ крутилки после очистки списка необходим, тк крутилка выравнивается
     * по центру контейнера списка, и из-за координатора - скролящийся ресайклер имеет размеры бОльшие, чем видимая
     * область экрана, поэтому в момент очистки списка также изменяется и размер контейнера, от этого скачет прогресс.
     */
    private fun restartProgressOnEmptySearchResult(pagedListResult: PagedListResult<ConversationRegistryItem>) {
        if (mSearchQuery.isNotEmpty()
            && dataList.isNotEmpty()
            && pagedListResult.dataList.isEmpty()
            && pagedListResult.hasMore()
            && stubFromMetadata(pagedListResult.metaData, isChannelTab()) == null) {
            mView?.restartProgress()
        }
    }

    override fun isNeedLoadOlderPage(lastVisibleItemPosition: Int): Boolean =
        !isSharingMode && super.isNeedLoadOlderPage(lastVisibleItemPosition)
            .also { if (it) mRefreshOlderPage = false }

    /** @SelfDocumented */
    override fun updateDataListAfterNetworkConnected() {
        if (mDataListOffset == 0 && mSearchQuery.isEmpty() && !isNeedLoadOlderPage(mLastVisibleItem)) {
            updateDataList(true)
        } else {
            loadOlderPageAutomatically()
        }
    }

    /** @SelfDocumented */
    override fun makeSearchRequest() {
        if (contactList.isNotEmpty()) clearContactsList()
        super.makeSearchRequest()

        checkAndScheduleMarkAllRegistryItemsAsViewed()
    }

    override fun onSearchButtonClicked() {
        mView?.hideKeyboard()
    }

    override fun getItemsReserve(): Int {
        return ITEMS_RESERVE
    }

    private fun clearContactsList() {
        contactList = CopyOnWriteArrayList()
    }

    override fun getUpdatingListByLastEntityObservable(dataModel: ConversationRegistryItem?, itemsCount: Int, fromPullToRefresh: Boolean): Observable<out PagedListResult<ConversationRegistryItem>> =
        super.getUpdatingListByLastEntityObservable(dataModel, itemsCount, fromPullToRefresh).let {
            // Проверка на dataModel == null необходима для загрузки контактов только при запросе сверху-вниз от начала списка
            if (dataModel == null && isNeedToSearchContacts) {
                contactsObservable.zipWith(it) { contacts: List<ContactVM>, dialogs: PagedListResult<ConversationRegistryItem> ->
                    contactList = contactList.union(contacts).toList()
                    dialogs
                }
            }
            else it
        }

    private fun searchPanelContacts() {
        // Поиск для панели контактов должен осуществляться только при пустом поисковом фильтре
        if (mSearchQuery.isNotBlank() || selectedFolderUuid != rootFolderUuid) return
        // Загрузка по возможности, когда UI полностью свободен и отрисован
        Looper.myQueue().addIdleHandler {
            conversationListInteractor.getRecipientList(EMPTY, MAX_SEARCH_CONTACTS_COUNT_TO_DISPLAY, refresh = true)
                .subscribe(::setDataToContactsSearchPanel)
                .storeIn(searchContactsDisposable)
            false
        }
    }

    private fun setDataToContactsSearchPanel(contactsList: List<ContactVM>) {
        if (contactsList.isNotEmpty() && searchPanelContactsList != contactsList) {
            searchPanelContactsList = contactsList
            updatePersonSuggestData()
        }
    }

    private fun updatePersonSuggestData() {
        mView?.setPersonSuggestData(searchPanelContactsList.map { it.suggestData })
    }

    override fun onFilterPanelFocusStateChanged(hasFocus: Boolean) {
        super.onFilterPanelFocusStateChanged(hasFocus)
        mView?.changeContactsSearchPanelVisibility(canShowContactsSearchPanel)
        showKeyboardOnFocusChanged(hasFocus)
    }

    private fun showKeyboardOnFocusChanged(hasFocus: Boolean) {
        if (hasFocus && (!mKeyboardIsVisible || needRestoreKeyboard)) {
            needRestoreKeyboard = false
            mView?.showKeyboard()
        }
    }

    override fun onPersonSuggestClick(person: PersonSuggestData) {
        personFilter = person
        makeSearchRequest()
    }

    /**
     * Обновить список для показа релевантной заглушки списка.
     */
    private fun updateDataListForStub() {
        val containsAnyConversation = conversationList.find { it is ConversationModel } != null
        if (mLoadingState == LoadingState.NOT_LOADING && (!containsAnyConversation || mSearchQuery.isNotBlank())) {
            updateDataList(false)
        }
    }

    override fun onRefreshCallback(params: HashMap<String, String>?) {
        handleSyncStatusWithRefreshCallback(params)
        waitingNoticeSyncDeeplink?.also(::openNotificationListByDeeplink)
        if (isSearchMessages) { params?.let { handleRefreshForMessageSearch(it) } }

        // нужна ли заглушка
        if (params != null && (
                params[ERROR_EVENT_KEY] != null || (
                    params[REGISTRY_SEARCH_EVENT_KEY] == REGISTRY_SEARCH_EVENT_FINISHED_VALUE &&
                        params[SEARCH_REQUEST_ID_KEY] == searchRequestId &&
                        params[REGISTRY_CHANGED_EVENT_KEY] != OLDER_ADDED_EVENT_VALUE
                    )
                )
        ) {
            runOnUiThread { updateDataListForStub() }
        }

        // в случае недоступности сервиса сообщений не реагируем на остальные события
        if (params?.get(ERROR_EVENT_KEY) == OTHER_EVENT_KEY) {
            serviceAvailability.off()
            runOnUiThread {
                mView?.showSyncErrorNotification(isNetworkError = false)
                notifySyncFailed(-1, false)
            }
            return
        } else if (!serviceAvailability.get()) {
            serviceAvailability.on()
        }

        params?.let {
            if (it[SEARCH_REQUEST_ID_KEY]?.equals(searchRequestId) == false) {
                pendingRefreshCallback = it
                return
            }
            when {
                it[AFFECTED_THEMES_EVENT_KEY] != AFFECTED_THEMES_EVENT_VALUE_NONE -> Unit
                // Точечный фикс ошибки к релизу, на ios не воспроизводится
                pagingLoadingObservable.value == LOADING &&
                    it[THEME_TYPE_EVENT_KEY] == THEME_TYPE_EVENT_CHAT_VALUE &&
                    it[REGISTRY_CHANGED_EVENT_KEY] == REGISTRY_CHANGED_EVENT_VALUE -> {
                    pagingLoadingObservable.onNext(NOT_LOADING)
                }
            }

            it.checkToUpdate(params)

            when {
                it[NETWORK_EVENT_KEY] == AVAILABLE_EVENT_VALUE -> {
                    networkAvailability.on()
                    runOnUiThread {
                        mView?.resetPagingLoadingIndicator()
                        onNetworkConnected()
                    }
                }
                it[ERROR_EVENT_KEY] == NETWORK_EVENT_KEY       -> {
                    if (!ignoreSyncError.get()) {
                        if (networkAvailability.get() && serviceAvailability.get()) {
                            runOnUiThread {
                                mView?.showSyncErrorNotification()
                                notifySyncFailed(-1, true)
                            }
                        }
                        networkAvailability.off()
                    }
                }
            }
        } ?: super.onRefreshCallback(null)
    }

    private fun handleRefreshForMessageSearch(params: HashMap<String, String>) {
        if (params[SEARCH_REQUEST_ID_KEY] == searchRequestId) {
            scope.launch {
                runCatching {
                    _totalFoundMessagesCount.emit(params[SEARCH_MESSAGES_TOTAL_LIST_COUNT]?.toInt() ?: 0)
                }.onFailure {
                    Timber.e("ThemePresenterImpl - handleRefreshForMessageSearch $it")
                }
            }
        }
    }

    private fun HashMap<String, String>.checkToUpdate(params: HashMap<String, String>?) {
        get(REGISTRY_CHANGED_EVENT_KEY)?.let {
            when {
                it == OLDER_ADDED_EVENT_VALUE -> {
                    runOnUiThread {
                        mRefreshOlderPage = true
                        mWaitingForOlderRefreshCallback = false
                        if (mLastVisibleItem + itemsReserve >= dataList.size + mDataListOffset) {
                            loadOlderPage()
                        }
                    }
                }

                REGISTRY_CHANGED_EVENT_VALUE == it ||
                    get(REGISTRY_SEARCH_EVENT_KEY) != null -> super.onRefreshCallback(this)
            }
            return
        }
        get(THEME_EVENT_KEY)?.let { super.onRefreshCallback(params) }
    }

    override fun getEmptyViewErrorId(): Int = when {
        !networkAvailability.get()                                            ->
            RCommon.string.common_no_network_available_check_connection
        !serviceAvailability.get()                                            ->
            RCommon.string.common_data_loading_error
        mSearchQuery.isNotEmpty()
                || (mSearchQuery.isEmpty() && isContactSelectedInSearchPanel) ->
            RDesign.string.design_empty_search_error_string
        isChannelTab()                                                        -> when (chatType) {
            ChatType.ALL    -> RCommunicatorDesign.string.communicator_no_channels_to_display
            ChatType.UNREAD -> RCommunicatorDesign.string.communicator_no_unread_channels_to_display
            ChatType.HIDDEN -> RCommunicatorDesign.string.communicator_no_hidden_channels_to_display
        }
        conversationSettings.getDialogsFilterState(accountPersonId) != DIALOGS_FILTER_ALL
                || selectedFolderUuid != rootFolderUuid                       ->
            RCommunicatorDesign.string.communicator_no_filter_dialogs_to_display
        else                                                                  -> RCommunicatorDesign.string.communicator_no_dialogs_to_display
    }

    override fun attachView(view: ThemeView) = with(view) {
        superAttachView(view)
        selectionHelper.attachAdapter(adapter, view.isTablet)
        checkHelper.attachToAdapter(adapter)
        conversationPrefetchDelegate.attachView(view)

        if (isDefaultMode) {
            subscribeToCheckModeState()

            onBranchTypeTabSelected()
            setUnreadAndUnviewedCounters(
                unreadChatsCount,
                unreadDialogsCount,
                unviewedChats,
                unviewedDialogs
            )
            restoreFolderTitle()
            updatePersonSuggestData()
            updateListItemsLayoutRules()
            if (customizationOptions.splittingChannelsAndDialogsEnabled) {
                setBranchTypeTitle(currentTabId)
            }
        } else {
            observeAreaSyncStatus()
        }

        if (firstAttachView && !dataList.any { it !is FoldersConversationRegistryItem }) {
            listCommandAndKeeper.themeListCommand.getCache(isChannelTab())?.let {
                trace("ThemePresenterImpl.processUpdatingDataListResult on attach view") {
                    processUpdatingDataListResult(it, updatingFromTail = false, notifyDataSetChanged = false)
                }
            }
        } else {
            adapter.setData(dataList, mDataListOffset)
        }
        view.showStub(currentStub)
        firstAttachView = false
    }

    override fun prepareThemeBottomCheckAction(): List<ThemeBottomCheckAction> {
        return buildList {
            if (!isDeleted()) {
                add(
                    MARK_GROUP_AS_READ.withAction {
                        markDialogsAsRead(true)
                    }
                )
                add(
                    MARK_GROUP_AS_UNREAD.withAction {
                        markDialogsAsRead(false)
                    }
                )
                add(
                    MOVE_GROUP.withAction {
                        onMoveGroupOperationClicked()
                    }
                )
            }
            add(
                DELETE_GROUP.withAction {
                    deleteDialogsByPanel()
                }
            )
        }
    }

    /**
     * Копипаста super.AttachView() чтобы убрать лишний вызов list на контроллер
     */
    private fun superAttachView(view: ThemeView) {
        mView = view

        if (isNeedToDisplayViewState) {
            displayViewState(view)
        }

        if (isNeedToRestoreScrollPosition) {
            view.scrollToPosition(mFirstVisibleItem)
        }

        if (mShowLoadingProcess || mShowSyncProcess) {
            view.showLoading()
        } else {
            view.hideLoading()
        }

        view.showOlderLoadingProgress(mShowOlderProgress)
        view.showNewerLoadingProgress(mHasNewerPage)

        if (mMissedErrorResId != NO_RESOURCE) {
            view.showLoadingError(mMissedErrorResId)
            mMissedErrorResId = NO_RESOURCE
        }

        mSubscriptionManager.resume()
        if (!firstAttachView) {
            updateDataList(true)
        }
    }

    override fun detachView() {
        selectionHelper.detachAdapter()
        checkHelper.detachFromAdapter()
        conversationPrefetchDelegate.detachView()
        super.detachView()
    }

    override fun viewIsResumed() {
        viewIsResumed = true
        super.viewIsResumed()
        when {
            isSearchMessages -> return
            isSharingMode -> mView?.setFabVisible(!checkHelper.isCheckModeEnabled)
        }
        showBottomNavigation()
        hideNewMessagePushes()
        if (!isChannelTab()) {
            checkAndScheduleMarkAllRegistryItemsAsViewed()
        } else {
            if (customizationOptions.splittingChannelsAndDialogsEnabled) checkAndScheduleMarkAllRegistryItemsAsViewed()
        }
        if (!isFirstResume) {
            restoreFolderTitle()
        }
        isFirstResume = false
    }

    override fun viewIsPaused() {
        viewIsResumed = false
        showNewMessagePushes()
        needRestoreKeyboard = mFocusInFilterPanel
        super.viewIsPaused()
    }

    override fun onDestroy() {
        isDestroyed = true
        if (isDefaultMode) {
            themesRegistryDependency.themeTabHistory.saveLastSelectedTab(currentTabId.id)
        }
        completeDialogDismissing(isTabChanging = false)
        singleDisposables.dispose()
        conversationPrefetchDelegate.onDestroy()
        scope.cancel()
        super.onDestroy()
    }

    private fun showBottomNavigation() {
        mScrollHelper.sendFakeScrollEvent(
            if (checkHelper.isCheckModeEnabled) ScrollEvent.SCROLL_DOWN_FAKE
            else ScrollEvent.SCROLL_UP_FAKE
        )
    }

    override fun isChannelTab(): Boolean =
        currentTabId == NavxId.CHATS

    override fun getContactListSize() =
        contactList.size

    override fun setDialogItemsMinCount(count: Int) {
        listFilter.minItemsCount = count
    }

    private fun switchReadState(dialog: ConversationModel, markAsRead: Boolean) {
        addItemToReadManager(dialog.uuid)
        conversationListInteractor.markDialogs(listOf(dialog.uuid), markAsRead)
            .doOnComplete { MetricsDispatcher.stopTrace(FIREBASE_READ_DIALOG_BY_SWIPE_MENU) }
            .subscribe(
                {
                    if (!markAsRead) resetSelectionIfNeeded(listOf(dialog.uuid))
                    with(dialog) {
                        decrementUnreadCount()
                        formattedUnreadCount = conversationListInteractor.formatUnreadCount(unreadCount)
                        isRead = unreadCount == 0
                    }
                    val dialogPosition = conversationList.indexOf(dialog)
                    if (dialogPosition > -1) {
                        val conversationModel = conversationList[dialogPosition].castTo<ConversationModel>()
                        val resultModel = it?.first()?.castTo<ConversationModel>()
                        conversationModel?.run {
                            isRead = resultModel?.isRead == true
                            isReadByMe = resultModel?.isReadByMe == true
                            unreadCount = resultModel?.unreadCount ?: 0
                        }
                        mView!!.notifyItemsChanged(
                            mDataListOffset + dialogPosition,
                            1
                        )
                    } else {
                        val markedDialog = listOf(dialog)
                        updateChangedItems(conversationList.castToConversationModelList(), markedDialog)
                    }
                },
                { throwable ->
                    Timber.e(throwable, "Error on mark dialogs as read/unread cause: ")
                }
            ).storeIn(dialogActionsSerialDisposable)
    }

    override fun markDialogsAsRead(read: Boolean) {
        var conversationModels = conversationForPreview?.let { listOf(it) } ?: checkHelper.checkedItems.map {
            it as ConversationModel
        }
        if (!read) conversationModels = conversationModels.filter { !it.isNotice }
        val uuids = getUuids(conversationModels, true)

        if (!read) {
            resetSelectionIfNeeded(uuids)
            removeItemsFromReadManagerForUnreadType(uuids)
        } else if (dialogType === DialogType.UNREAD) {
            uuids.forEach(listCommandAndKeeper::notifyItemMightLeave)
        }
        analyticsUtil?.sendAnalytics(
            if (read) {
                MassOperationReadTheme(getSimpleNameForAnalytic())
            } else {
                MassOperationUnreadTheme(getSimpleNameForAnalytic())
            }
        )
        conversationListInteractor.markDialogs(uuids, read).subscribe(
            { markedDialogs ->
                updateChangedItems(
                    conversationList.castToConversationModelList(),
                    markedDialogs.castToConversationModelList()
                )
                hideCheckModeInternal()
            },
            { Timber.e(it, "Error on mark dialogs as read cause: ") }
        ).storeIn(dialogActionsSerialDisposable)
    }

    /**
     * Сменить статус прочитанности диалога(пуша)
     * @param dialogUuid индентификатор диалогов
     * @param messageId индентификатор сообщений
     */
    private fun setSocialRequireReadDialogUuid(dialogUuid: UUID?, messageId: UUID?) {
        if (dialogUuid != null && messageId != null) {
            conversationListInteractor.markReadPush(dialogUuid, messageId)
        }
    }

    override fun onMoveGroupOperationClicked() {
        val conversationModels = conversationForPreview?.let { listOf(it) } ?: checkHelper.checkedItems.map {
            it as ConversationModel
        }
        // Если один диалог выбрали массовой операцией - отметим маркером папку, где он лежит,
        // иначе - не помечаем ничего (т.к. диалогов несколько)
        if (conversationModels.size == 1) {
            val conversation = conversationModels[0]
            // По умолчанию переписка принадлежит корневой папке
            val folderUuid = conversation.folderUuid ?: ROOT_FOLDER_UUID
            showPickFolderSelectionScreen(folderUuid)
        } else {
            showPickFolderSelectionScreen()
        }
    }

    override fun deleteDialogsByPanel() {
        if (mView == null) return
        when {
            dialogType.isDeleted -> {
                mView?.showMassDeletingConfirmationDialogForAll()
            }
            checkHelper.checkedItems.any { it.castTo<ConversationModel>()?.isNotice == true } -> {
                mView?.showMassDeletingDialogsAndNoticeConfirmationDialog()
            }
            else -> {
                deleteDialogs()
            }
        }
    }

    override fun onCheckModeCancelClicked() {
        hideCheckModeInternal()
    }

    override fun onBranchTypeTabClick(navxId: NavxIdDecl) {
        if (currentTabId == navxId || (navxId != NavxId.CHATS && !isChannelTab())) return

        // Сохраняем состояние заглушки до переключения
        cacheCurrentStub(forChannels = isChannelTab())

        isBranchTypeChanging = true
        if (isTablet) {
            router.closeSubContent()
            selectionHelper.resetSelection()
        }
        completeDialogDismissing(isTabChanging = true)
        currentTabId = navxId

        listFilter.conversationType = if (isChannelTab()) ConversationType.CHAT else ConversationType.DIALOG
        personFilter = null

        isContactSelectedInSearchPanel = false

        if (isChannelTab()) {
            onRootFolderSelected()
        } else {
            selectedFolder?.let { folder ->
                onFilterSelected(listFilter.filterConfiguration!!.copy(selectedFolder = folder))
            }
        }
        mView?.run {
            resetUiState()
            clearFocusFromSearchPanel()
            clearSearchQuery(false)
            if (customizationOptions.splittingChannelsAndDialogsEnabled && isDefaultMode) {
                setBranchTypeTitle(navxId)
            }
        }
        makeSearchRequest()
        onBranchTypeTabSelected()

        mView?.let {
            // взводим таймер во время которого ошибка синхронизации не будет показана
            Completable.timer(500, TimeUnit.MILLISECONDS)
                .doOnSubscribe { ignoreSyncError.set(true) }
                .doFinally { ignoreSyncError.set(false) }
                .subscribe()
                .storeIn(syncErrorDelayDisposable)
        }
        isBranchTypeChanging = false

        // Восстанавливаем предыдущее состояние заглушки
        restoreCurrentStub(forChannels = isChannelTab())
        mView?.showStub(currentStub)

        // берем данные из кеша при переключении вкладок
        listCommandAndKeeper.themeListCommand.getCache(isChannelTab())?.let {
            processUpdatingDataListResult(it, updatingFromTail = false, notifyDataSetChanged = false)
            if (dataList.isNotEmpty()) {
                mView?.scrollToPosition(0)
            }
        }
    }

    private fun onTabChangedByClick(isChatTab: Boolean) {
        if (isChannelTab() && isChatTab) return
        mView?.restartProgress()
        mView?.changeThemesRegistry(
            if (isChatTab) CommunicatorRegistryType.ChatsRegistry()
            else CommunicatorRegistryType.DialogsRegistry()
        )
        sendAnalyticSwitchRee()
    }

    override fun deleteDialogs(forAll: Boolean) {
        if (dialogType.isDeleted) {
            MetricsDispatcher.startTrace(FIREBASE_DELETE_DIALOG)
        }
        if (conversationForPreview != null || checkHelper.checkedItems.isNotEmpty()) {
            val conversations = conversationForPreview?.let { listOf(it) } ?: checkHelper.checkedItems.map {
                it as ConversationModel
            }
            val dialogsUuids = getUuids(conversations)
            resetSelectionIfNeeded(dialogsUuids)
            removeItemsFromReadManagerForUnreadType(dialogsUuids)
            conversationListInteractor.deleteDialogs(dialogsUuids, forAll)
                .subscribe(Functions.EMPTY_ACTION)
                .storeIn(deleteDialogDisposable)
            hideCheckModeInternal()
            analyticsUtil?.sendAnalytics(MassOperationRemoveTheme(getSimpleNameForAnalytic()))
        } else if (lastClickedSwipedConversation != null) {
            val itemPosition = conversationList.indexOf(lastClickedSwipedConversation!!)
            val conversationModel = lastClickedSwipedConversation!!.castTo<ConversationModel>()!!
            val dialogUuids = listOf(conversationModel.uuid)
            val isChatForOperation = conversationModel.isChatForOperations
            resetSelectionIfNeeded(dialogUuids)
            removeItemsFromReadManagerForUnreadType(dialogUuids)
            if (isChatForOperation && dialogType.isDeleted) {
                conversationListInteractor.deleteArchiveMessageForMe(conversationModel)
            } else {
                conversationListInteractor.deleteDialogs(dialogUuids, forAll)
            }
                .subscribe(Functions.EMPTY_ACTION)
                .storeIn(deleteDialogDisposable)
            if (dialogType.isDeleted) {
                MetricsDispatcher.stopTrace(FIREBASE_DELETE_DIALOG)
            }
            if (itemPosition < 0) {
                mView?.notifyDataSetChanged()
            }
            analyticsUtil?.sendAnalytics(SwipeRemoveTheme(getSimpleNameForAnalytic()))
        }
        mView?.run { showEmptyViewIfNeeded(this, conversationList, emptyViewErrorId) }
    }

    override fun onRootFolderSelected() {
        if (selectedFolderUuid != rootFolderUuid) {
            listFilter.filterConfiguration = listFilter.filterConfiguration!!.copyWithUpdatedFolder(null)
            selectedFolderUuid = rootFolderUuid
            updateViewByFolderChanges()
            if (!isChannelTab()) {
                selectedFolder = null
            }
        }
    }

    private fun updateViewByFolderChanges() {
        isFolderTitleChanged = true
        hideCheckModeInternal()
        makeSearchRequest()
    }

    private fun changeFolderTitle() {
        if (!isFolderTitleChanged) return
        mView?.let {
            it.setFolderTitle(if (selectedFolderUuid != rootFolderUuid) selectedFolder?.title else null)
            it.setFoldersCompact()
            isFolderTitleChanged = false
        }
    }

    private fun restoreFolderTitle() {
        if (selectedFolderUuid != rootFolderUuid) {
            mView?.setFolderTitle(selectedFolder?.title)
        }
    }

    override fun selectItem(conversationModel: ConversationModel) {
        selectionHelper.selectItem(conversationModel)
    }

    override fun changeSelection(conversationModel: ConversationModel) {
        selectionHelper.changeSelection(conversationModel)
    }

    override fun onDialogDismissed(uuid: UUID, isChat: Boolean) {
        removeItem(uuid)
        if (isChat) {
            hideDismissedConversation(uuid)
        } else {
            deleteDismissedDialog(uuid)
        }
    }

    override fun onFilterClick() {
        val initialConfiguration = ConversationFilterConfiguration(DialogType.ALL, ChatType.ALL, null)
        val currentConfiguration = listFilter.filterConfiguration!!
        mView?.showFilterSelection(initialConfiguration, currentConfiguration)
    }

    override fun moveDialogsToFolder(folderUuid: UUID, folderTitle: String) {
        val swipedDialog = lastClickedSwipedConversation
        if (swipedDialog is ConversationModel) {
            val selectedFolderUuid = if (folderUuid != UUIDUtils.NIL_UUID) folderUuid else null
            if (swipedDialog.folderUuid == selectedFolderUuid) {
                mView?.showDialogInFolderAlready(EMPTY)
                return
            }
        }
        val singleMove = checkHelper.checkedItems.isEmpty() || conversationForPreview != null
        val checkedConversationModels = checkHelper.checkedItems.map { it as ConversationModel }
        val checkedDialogs = HashSet(checkedConversationModels)
        val dialogsUuids = getUuids(
            if (singleMove)  {
                conversationForPreview?.let { listOf(it) } ?: listOf(lastClickedSwipedConversation!! as ConversationModel)
            } else {
                checkedConversationModels
            }
        )
        hideCheckModeInternal()
        analyticsUtil?.sendAnalytics(
            if (singleMove) {
                MoveDialogsToFoldersBySwipe(getSimpleNameForAnalytic())
            } else {
                MoveDialogsToFoldersByMassOperation(getSimpleNameForAnalytic())
            }
        )
        conversationListInteractor.moveDialogsToFolder(dialogsUuids, folderUuid)
            .subscribe(
                {
                    if (it.status.errorCode != ErrorCode.SUCCESS) {
                        // При перемещении диалога через свайп checkHelper.getCheckedItems().size() равен 0, передаем 1.
                        // Если mCheckHelper.getCheckedItems().size() равен 1, передаем 1. В остальных случаях передаем 2
                        mView?.showDialogInFolderAlready(it.status.errorMessage)
                        return@subscribe
                    }
                    if (selectedFolderUuid !== rootFolderUuid) {
                        removeItemsFromReadManagerForUnreadType(dialogsUuids)
                        resetSelectionIfNeeded(dialogsUuids)
                        if (singleMove) {
                            removeItem(dialogsUuids[0], false)
                        } else {
                            for (dialogUuid in dialogsUuids) {
                                removeItem(dialogUuid, false)
                            }
                        }
                        if (mView != null) {
                            showEmptyViewIfNeeded(mView!!, conversationList, emptyViewErrorId)
                        }
                    }
                    mView?.showSuccessMoveToFolder(if (checkedDialogs.size < 2) 1 else 2)
                },
                { error ->
                    Timber.e(error, "Error on move dialogs to folder")
                }
            ).storeIn(moveDialogDisposable)

        // Присваиваем null, чтобы после обработки действия по свайпу можно было корректно отработать
        // действиям массовых операций
        lastClickedSwipedConversation = null
    }

    override fun onNewDialogClick() = DebounceActionHandler.INSTANCE.handle {
        recipientSelectionManager.clear()
        if (mView == null) {
            return@handle
        }
        if (isChannelTab()) {
            router.showNewChatCreation()
        } else {
            router.showNewDialogRecipientSelection(UUID.fromString(selectedFolderUuid))
        }
    }

    override fun resetTypeIfUnanswered() {
        if (!isChannelTab() && DialogType.UNANSWERED == dialogType) {
            updateDialogType(DialogType.ALL)
            updateFilterString()
        }
    }

    override fun onCancelSearchClick(needSearchRequest: Boolean) {
        pagingLoadingObservable.onNext(NOT_LOADING)
        isContactSelectedInSearchPanel = false
        mSearchQuery = EMPTY
        searchQueryChanged(mSearchQuery)
        if (!isChannelTab()) {
            personFilter = null
        }
        if (needSearchRequest) {
            makeSearchRequest()
        }
    }

    /**
     * Callback об изименении текста в строке поиска
     * @param searchQuery новый текст поиска
     */
    override fun onSearchQueryChanged(searchQuery: String) {
        val newSearchQuery = searchQuery.trim()
        if (newSearchQuery.isNotBlank() && isDefaultMode) {
            checkHelper.disableCheckMode()
            mView?.setFabVisible(true)
            debounceActionHandler.handle {
                analyticsUtil?.sendAnalytics(
                    if (isChannelTab()) {
                        SearchChats(getSimpleNameForAnalytic())
                    } else {
                        SearchDialogs(getSimpleNameForAnalytic())
                    }
                )
            }
        }
        super.onSearchQueryChanged(newSearchQuery)
    }

    private fun initSubscriptions(
        unreadCounterProvider: CounterProvider<CommunicatorCounterModel>
    ) {
        val selectedItemObservable = selectionHelper.itemSelectionObservable.cache()

        // переходы
        selectedItemObservable
            .filter { mView != null }
            .subscribe(::handleSelectedItem) {
                Timber.e(it, "Error when trying to process item selection in dialogs list.")
            }.storeIn(selectionDisposable)

        selectedItemObservable
            .filter { it is ConversationModel && it.markAsReadOnClick }
            .delay {
                it as ConversationModel
                if (it.isNotice) {
                    Observable.just(it).delay(READ_NOTIFICATION_DELAY, TimeUnit.MILLISECONDS)
                } else {
                    Observable.just(it)
                }
            }
            .flatMap {
                conversationListInteractor.markDialogs(
                    listOf((it as ConversationModel).uuid),
                    true
                ).logAndIgnoreError()
            }
            .subscribe()
            .storeIn(singleDisposables)

        // Количество выделенных
        checkHelper.checkedItemsObservable
            .filter { mView != null }
            .subscribe({ checkedItems ->
                val hasCheckedItems = !checkedItems.isNullOrEmpty()
                var canReadDialogs = false
                var canUnreadDialogs = false
                if (hasCheckedItems) {
                    for (registryItem in checkedItems) {
                        if (!(registryItem as ConversationModel).canBeMarked()) continue

                        if (registryItem.canBeMarkedRead) {
                            canReadDialogs = true
                        } else if (registryItem.canBeMarkedUnread && !registryItem.isNotice) {
                            canUnreadDialogs = true
                        }

                        if (canUnreadDialogs && canReadDialogs) break
                    }
                }
                mView!!.onCheckStateChanged(hasCheckedItems, canReadDialogs, canUnreadDialogs)
            }, { Timber.e(it) })
            .storeIn(checkCountDisposable)

        recipientSelectionManager.getSelectionResultObservable(TABLET_NEW_CONVERSATION_RECIPIENT_SELECTION)
            .filter { result -> result.isSuccess }
            .filter { mView != null }
            .subscribe {
                router.showNewDialog(UUID.fromString(selectedFolderUuid))
            }.storeIn(singleDisposables)

        dialogSelectionResultManager.selectionDoneObservable
            .subscribe(::handleDialogSelectionResult)
            .storeIn(singleDisposables)

        // Счётчики диалогов и чатов из кэша, если они есть
        unreadCounterProvider.castTo<CommunicatorCounterProvider>()?.getCounterValue()?.let {
            unreadDialogsCount = it.unreadDialogs
            unreadChatsCount = it.unreadChats
            unviewedDialogs = it.unviewedDialogs
            unviewedChats = it.unviewedChats
        }
        // Счётчики диалогов и чатов по подписке
        unreadCounterProvider.counterEventObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                unreadDialogsCount = it.unreadDialogs
                unreadChatsCount = it.unreadChats
                unviewedDialogs = it.unviewedDialogs
                unviewedChats = it.unviewedChats
            }
            .filter { mView != null }
            .subscribe(
                {
                    if (isDefaultMode) {
                        mView!!.setUnreadAndUnviewedCounters(
                            it.unreadChats,
                            it.unreadDialogs,
                            it.unviewedChats,
                            it.unviewedDialogs
                        )
                    }
                },
                { Timber.e(it) }
            ).storeIn(singleDisposables)
    }

    /**
     * Обработка выбранного элемента реестра
     * @param conversation модель элемента реестра
     */
    private fun handleSelectedItem(conversation: ConversationRegistryItem) {
        if (conversation !is ConversationModel) return
        when {
            isSharingMode -> {
                if (!conversation.canSendMessage) {
                    mView?.showShareToNotAvailableChannelPopup()
                    return
                }
                mView?.notifyShareSelectionListener(
                    conversation.copy(
                        nameHighlights = null,
                        docsHighlights = null,
                        searchHighlights = null,
                        dialogNameHighlights = null
                    )
                )
                return
            }
            isSearchMessages -> {
                conversation.messageUuid?.let {
                    mView?.notifySearchMessagesSelectionListener(it)
                }
                return
            }
        }
        // (Перенос комментария) Логи переходов по событиям соц-сети. Не удаляйте, там до сих пор проблемы
        Timber.tag("SocnetLogs").d("Dialog id: ${conversation.uuid} Is socnet: ${conversation.isSocnetEvent} Is outgoing: ${conversation.isOutgoing} Service object: ${conversation.socnetServiceObject}")
        mView!!.hideCursorFromSearch()
        addItemToReadManager(conversation.uuid)
        val hasSelection = !UUIDUtils.isNilUuid(conversation.uuid)
        when {
            hasSelection -> {
                if (conversation.isConversation || conversation.isConsultation) {
                    conversationPrefetchDelegate.prefetchConversation(conversation, mSearchQuery.isBlank())
                }
                closeSwipe()
                // помечаем все диалоги просмотренными при входе в любой диалог
                markAllDialogsAsViewedWithCheck()
                // открываем экран переписки
                router.openContentScreen(
                    ThemeConversationParams(
                        model = conversation,
                        isChatTab = isChannelTab(),
                        isSearchEmpty = mSearchQuery.isBlank(),
                        isArchivedDialog = if (conversation.isChatForOperations) {
                            ChatType.HIDDEN == chatType
                        } else {
                            DialogType.DELETED == dialogType
                        }
                    )
                )
            }
            selectionHelper.isTablet -> router.closeSubContent()
        }
    }

    override fun onNewDeeplinkAction(args: DeeplinkAction) {
        if (args is OpenConversationDeeplinkAction) {
            if ((!args.isChat && isChannelTab()) || (args.isChat && !isChannelTab())) {
                mView?.changeTabSelection(if (args.isChat) NavxId.CHATS else NavxId.DIALOGS)
            }
        }

        checkSocialEventDialogToMarkRead(args)
        onNextListUpdate { findAndSelectChatOnTablet(args) }

        when {
            // Предотвращаем попытку открытия уже открытой переписки для пушей.
            args is OpenConversationDeeplinkAction && args.dialogUuid == router.topConversation -> Unit

            args is SwitchThemeTabDeeplinkAction -> {
                onTabChangedByClick(args.isChatTab)
            }

            // Обработка пушей по уведомлениям -> показываем список уведомлений конкретной категории.
            args is HandlePushNotificationDeeplinkAction -> {
                openNotificationListByDeeplink(args)
            }

            else -> {
                router.onNewDeeplinkAction(args)
            }
        }
    }

    private fun openNotificationListByDeeplink(deeplink: HandlePushNotificationDeeplinkAction) {
        if (deeplink.noticeTypes.size == 1) {
            scope.launch(Dispatchers.Main) {
                val result = conversationListInteractor.getConversationModel(deeplink.noticeTypes.first())
                if (result != null) {
                    waitingNoticeSyncDeeplink = null
                    selectItem(result)
                } else {
                    waitingNoticeSyncDeeplink = deeplink
                }
            }
        } else {
            waitingNoticeSyncDeeplink = null
            // Групповой пуш по нескольким типам -> не обрабатываем
        }
    }

    /**
     * Поиск и выделение канала в списке при открытии через DeeplinkAction
     * в сценариях создания нового канала через "+" на планшете.
     */
    private fun findAndSelectChatOnTablet(args: DeeplinkAction) {
        if (isChannelTab() && isTablet && args is OpenConversationDeeplinkAction) {
            findConversationToSelect(args)
        }
    }

    /**
     * Поиск диалога в списке по идентификатору или по участнику с последующим выделением.
     * Актуально для планшета при создании нового канала.
     */
    private fun findConversationToSelect(args: OpenConversationDeeplinkAction) {
        val predicate = when {
            args.dialogUuid != null ->
                { model: ConversationRegistryItem -> model.castTo<ConversationModel>()?.uuid == args.dialogUuid }
            args.recipients?.size == 1 ->
                args.recipients!!.first().let {
                    { model: ConversationRegistryItem -> model.castTo<ConversationModel>()?.participantsUuids?.firstOrNull() == it }
                }
            else -> null
        }
        predicate?.let {
            conversationList.find(it)?.let { model -> selectionHelper.changeSelection(model) }
        }
    }

    private fun checkSocialEventDialogToMarkRead(args: DeeplinkAction) {
        var dialogUuidToMarkRead: UUID? = null
        var messageUuidToMarkRead: UUID? = null

        when (args) {
            is OpenNewsDeepLinkAction       -> {
                dialogUuidToMarkRead = args.commentUuid
                messageUuidToMarkRead = args.commentUuid
            }
            is OpenProfileDeeplinkAction    -> {
                dialogUuidToMarkRead = args.dialogUuid
                messageUuidToMarkRead = args.messageUuid
            }
            is OpenWebViewDeeplinkAction    -> {
                dialogUuidToMarkRead = args.dialogUuid
                messageUuidToMarkRead = args.messageUuid
            }
            else                            -> Unit
        }

        setSocialRequireReadDialogUuid(dialogUuidToMarkRead, messageUuidToMarkRead)
    }

    override fun onPersonFilterViewClicked(personUuid: UUID) {
        showProfile(personUuid)
    }

    private fun showProfile(uuid: UUID) {
        mView?.clearFocusFromSearchPanel()
        router.showProfile(uuid)
    }

    private fun resetSelectionIfNeeded(dialogsListToCheck: List<UUID>) = with(selectionHelper) {
        if (dialogsListToCheck.contains((selectedItem as ConversationModel).uuid)) {
            resetSelection()
        }
    }

    private fun removeItemsFromReadManagerForUnreadType(conversationUuids: List<UUID>) {
        if (dialogType === DialogType.UNREAD || chatType === ChatType.UNREAD) {
            listCommandAndKeeper.doNotKeep(conversationUuids)
        }
    }

    private fun updateChangedItems(dialogs: MutableList<ConversationModel>, changedDialogs: List<ConversationModel>) {
        if (mView != null) {
            changedDialogs.asSequence().filter {
                // помечаем только входящие диалоги
                // [От переводчика] дословно здесь было:
                // "это возможно, помечаем только входящие диалоги" (it is possible mark only incoming dialogs)
                // но по смыслу, скорее всего, так
                it.isForMe
            }.forEach { changedDialog ->
                applyIfContains(dialogs, changedDialog) {
                    dialogs[it] = changedDialog
                    mView?.notifyItemsChanged(it, 1)
                }
            }
        }
    }

    private fun hideCheckModeInternal() {
        checkHelper.disableCheckMode()
        mScrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE)
        mView?.setFabVisible(true)
    }

    private fun addItemToReadManager(conversationUuid: UUID) {
        if (dialogType === DialogType.UNREAD || chatType === ChatType.UNREAD) {
            listCommandAndKeeper.notifyItemMightLeave(conversationUuid)
        }
    }

    private fun showPickFolderSelectionScreen(currentFolder: UUID? = null) {
        mView?.showFolderSelection(currentFolder)
    }

    private fun onBranchTypeTabSelected() {
        if (isChannelTab()) {
            updateChatType(chatType)
            hideCheckModeInternal()
            checkAndScheduleMarkAllRegistryItemsAsViewed()
        } else {
            updateDialogType(dialogType)
            checkAndScheduleMarkAllRegistryItemsAsViewed()
        }
        mView?.setFabVisible(!checkHelper.isCheckModeEnabled)
        updateFilterString()
        observeAreaSyncStatus()
    }

    //region Not Viewed Dialogs

    private fun subscribeOnNotViewedRegistryItemsCounter() {
        conversationListInteractor.communicatorCounter()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { checkAndScheduleMarkAllRegistryItemsAsViewed() }
            .storeIn(singleDisposables)
    }

    private fun subscribeOnConversationClosedEvents() {
        // подписываемся на события закрытия экрана переписки
        conversationEventsPublisher.conversationClosedObservable
            .subscribe(this::onConversationClosedEvent)
            .storeIn(singleDisposables)
    }

    private fun onConversationClosedEvent(conversationUuid: UUID) {
        val position = conversationList.indexOfFirst { it is ConversationModel && it.uuid == conversationUuid }
        if (position >= 0) {
            val registryItem = conversationList[position]
            if (registryItem is ConversationModel && !registryItem.isViewed) {
                markRegistryItemAsViewed(position, registryItem, isChannelTab())
            }
        }
        mView?.hideKeyboard()
    }

    private fun canMarkAllDialogsAsViewed(): Boolean {
        // можем сбрасывать счетчик непросмотренных диалогов только в фильтрах Все\Входящие\Непрочитанные и если в корневой папке и не в поиске
        return viewIsResumed
                && !isChannelTab()
                && dialogType in arrayOf(DialogType.ALL, DialogType.INCOMING, DialogType.UNREAD)
                && selectedFolder == null
                && listFilter.getSearchQuery().isNullOrBlank()
                && personFilter == null
                && !registryIsHidden
    }

    private fun canMarkAllChannelsAsViewed(): Boolean {
        // можем сбрасывать счетчик непросмотренных каналов только в фильтрах Все\Непрочитанные и не в поиске
        return viewIsResumed
                && isChannelTab()
                && chatType in arrayOf(ChatType.ALL, ChatType.UNREAD)
                && listFilter.getSearchQuery().isNullOrBlank()
                && personFilter == null
                && !registryIsHidden
    }

    private fun markRegistryItemAsViewed(position: Int, registryItem: ConversationModel, isChannel: Boolean) {
        registryItem.isViewed = true
        mView?.notifyItemsChanged(mDataListOffset + position, 1)

        if (isChannel) {
            conversationListInteractor.markChannelAsViewed(registryItem.uuid)
        } else {
            conversationListInteractor.markDialogAsViewed(registryItem.uuid)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Functions.EMPTY_ACTION) {
                Timber.e(it, "ThemePresenterImpl.markRegistryItemAsViewed, markAsViewed error")
            }.storeIn(singleDisposables)
    }

    private fun checkAndScheduleMarkAllRegistryItemsAsViewed() {
        when {
            isChannelTab() && !canMarkAllChannelsAsViewed() -> return
            !isChannelTab() && !canMarkAllDialogsAsViewed() -> return
        }
        // Сбрасываем счетчик непросмотренных через 3 секунды
        Single.timer(3, TimeUnit.SECONDS).ignoreElement()
            .subscribe(this::markAllRegistryAsViewed)
            .storeIn(scheduleMarkRegistryViewedDisposable)
    }

    private fun markAllDialogsAsViewedWithCheck(callback: () -> Unit = {}) {
        if (canMarkAllDialogsAsViewed() || canMarkAllChannelsAsViewed()) {
            markAllRegistryAsViewed(callback)
        } else {
            callback()
        }
    }

    private fun markAllRegistryAsViewed(callback: () -> Unit = {}) {
        val completable = if (isChannelTab()) conversationListInteractor.markChatRegistryAsViewed() else
            conversationListInteractor.markDialogRegistryAsViewed()
        completable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(callback::invoke)
            .storeIn(singleDisposables)
    }
    //endregion

    private fun updateFilterString() {
        mView ?: return

        val itemToDisplay: EntitledItem
        val displayFilterName: Boolean
        if (isChannelTab()) {
            itemToDisplay = chatType
            displayFilterName = chatType !== ChatType.ALL
        } else {
            itemToDisplay = dialogType
            displayFilterName = dialogType !== DialogType.ALL
        }
        mView!!.changeFilterByType(itemToDisplay, displayFilterName)
    }

    private fun updateDialogType(newDialogType: DialogType) {
        if (dialogType === newDialogType) return

        dialogType = newDialogType
        resetUI()
        makeSearchRequest()
        foldersProvider.castTo<ThemeFoldersInteractorImpl>()?.setDialogFilter(dialogType.toDialogFilter())
    }

    private fun updateChatType(newChatType: ChatType) {
        if (chatType === newChatType) return

        chatType = newChatType
        resetUI()
        makeSearchRequest()
        saveChatsFilterState(newChatType)
    }

    private fun showNewMessagePushes() {
        if (!isDefaultMode) return
        messagesPushManager.executeAction(ThemeSubscribeFromNotification(isChannel = isChannelTab()))
    }

    private fun hideNewMessagePushes() {
        if (!isDefaultMode) return
        messagesPushManager.executeAction(ThemeUnsubscribeFromNotification(isChannel = isChannelTab()))
    }

    private fun subscribeToCheckModeState() {
        if (!isDefaultMode) return
        checkHelper.checkModeEnabledObservable
            .subscribe(
                { enabled ->
                    with(mView!!) {
                        setCheckMode(enabled)
                        if (enabled) {
                            disableFolders()
                            disableFilters()
                            mScrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_DOWN_FAKE)
                            analyticsUtil?.sendAnalytics(DialogsChooseMassOperation(getSimpleNameForAnalytic()))
                        } else {
                            enableFilters()
                            enableFolders()
                        }
                    }
                },
                { Timber.e(it, "Error when trying to process check mode switching in contacts list.") }
            ).storeIn(checkModeStateDisposable)
    }

    private fun hideDismissedConversation(uuid: UUID) {
        conversationListInteractor.hideConversation(uuid)
            .subscribe(Functions.EMPTY_ACTION) { Timber.e(it) }
            .storeIn(deleteOrHideByDismissDisposable)
    }

    private fun deleteDismissedDialog(uuid: UUID) {
        conversationListInteractor.deleteDialogs(listOf(uuid), false)
            .subscribe(Functions.EMPTY_ACTION)
            .storeIn(deleteOrHideByDismissDisposable)
    }

    private fun onChatTypeChanged(newChatType: ChatType) {
        if (newChatType === chatType || mView == null) return

        updateChatType(newChatType)
        updateFilterString()

        analyticsUtil?.sendAnalytics(ChangeChatsFilter(getSimpleNameForAnalytic(), chatType))
    }

    private fun onDialogTypeChanged(newDialogType: DialogType) {
        if (newDialogType === dialogType || mView == null) return

        updateDialogType(newDialogType)
        updateFilterString()

        saveDialogsFilterState(newDialogType)
        analyticsUtil?.sendAnalytics(ChangeDialogsFilter(getSimpleNameForAnalytic(), dialogType))
    }

    private fun onFolderSelected(configuration: ConversationFilterConfiguration) {
        listFilter.filterConfiguration = configuration
        val folder = configuration.selectedFolder
        if (folder?.id != null && folder.id != rootFolderUuid) {
            selectedFolderUuid = folder.id
            selectedFolder = folder
            updateViewByFolderChanges()
            analyticsUtil?.sendAnalytics(GoToFoldersDialogs(getSimpleNameForAnalytic()))
        } else {
            onRootFolderSelected()
        }
    }

    private fun onSearchPersonSelected(person: PersonSuggestData?) {
        listFilter.personUuid = person?.uuid
        person?.let { mView?.setSelectedPersonToFilter(it) }
        isContactSelectedInSearchPanel = person != null
        if (isContactSelectedInSearchPanel) {
            analyticsUtil?.sendAnalytics(ChooseFromPersonSuggestView(getSimpleNameForAnalytic()))
        }
    }

    /**
     * При клике на холдер открыть или выбрать, если активирован режим выбора
     * @param conversation диалог, на который кликнули
     */
    override fun onConversationItemClicked(conversation: ConversationModel) {
        mView?.clearFocusFromSearchPanel()
        if (checkHelper.isCheckModeEnabled) {
            checkHelper.setChecked(conversation, !checkHelper.isChecked(conversation))
        } else {
            itemSelectionSubject.onNext(conversation)
        }
    }

    private fun handleItemSelection(conversation: ConversationModel) {
        mView!!.hideSwipePanel(conversation.compareUuid)
        selectionHelper.selectItem(conversation)
    }

    /**
     * Callback о long клике на диалог/чат
     * @param conversation диалог, на который кликнули
     */
    override fun onConversationItemLongClicked(conversation: ConversationModel) =
        DebounceActionHandler.INSTANCE.handle {
            mView?.clearFocusFromSearchPanel()
            when {
                isSharingMode -> {
                    onConversationItemClicked(conversation)
                }

                filesTasksDialogFeatureOn && conversation.isConversation -> {
                    closeSwipe()
                    conversationForPreview = conversation
                    conversationPrefetchDelegate.prefetchConversation(conversation, mSearchQuery.isBlank())
                    router.openConversationPreview(
                        ThemeConversationParams(
                            model = conversation,
                            isChatTab = isChannelTab(),
                            isSearchEmpty = mSearchQuery.isBlank(),
                            isArchivedDialog = if (conversation.isChatForOperations) {
                                ChatType.HIDDEN == chatType
                            } else {
                                DialogType.DELETED == dialogType
                            }
                        ),
                        conversationPreviewMenuActions()
                    )
                }

                else -> {
                    if (!isChannelTab() && TextUtils.isEmpty(mSearchQuery)) {
                        closeSwipe()
                        checkHelper.enableCheckMode()
                        checkHelper.setChecked(conversation, true)
                        mView!!.setFabVisible(false)
                    }
                }
            }
        }

    private fun conversationPreviewMenuActions(): List<ThemeConversationPreviewMenuAction> {
        return buildList {
            add(Go())
            if (!isChannelTab()) {
                doIf(conversationForPreview!!.canBeMarkedRead) { add(MarkGroupAsRead()) }
                doIf(!conversationForPreview!!.canBeMarkedRead) { add(MarkGroupAsUnread()) }
                add(MoveGroupToFolder())
                add(MarkDialog())
            } else {
                doIf(conversationForPreview!!.isPinned) { add(Unpin()) }
                doIf(!conversationForPreview!!.isPinned && conversationForPreview!!.isConversationHiddenOrArchived) { add(Pin()) }
                doIf(conversationForPreview!!.canBeUnhide) { add(Restore()) }
            }
            add(Delete())
        }
    }

    override fun onConversationItemTouch(conversation: ConversationModel, event: MotionEvent) {
        conversationPrefetchDelegate.onConversationItemTouch(conversation, mSearchQuery.isBlank(), event)
    }

    /**
     * Установить в фильтр выбранный контакт
     * @param contact выбранный контакт
     * @param position позиция контакта в списке
     */
    override fun onClickItem(contact: ContactVM, position: Int) {
        personFilter = contact.suggestData
    }

    /**
     * Открыть профиль контакта, по фотографии которого кликнули
     * @param contact контакт
     */
    override fun onContactPhotoClick(contact: ContactVM) = DebounceActionHandler.INSTANCE.handle {
        showProfile(contact.uuid)
    }

    /**
     * Открыть профиль первого контакта из кликнутого коллажа
     * @param conversation модель диалога/чата на который кликнули
     */
    override fun onCollageViewClick(conversation: ConversationModel) = DebounceActionHandler.INSTANCE.handle {
        val socnetGroupConversation = conversation.isSocnetEvent && conversation.documentType == DocumentType.SOCNET_GROUP
        if (!isChannelTab() && !socnetGroupConversation) {
            val uuids = conversation.participantsUuids
            if (uuids.size == 1) {
                showProfile(uuids[0])
            } else {
                mView?.clearFocusFromSearchPanel()
                router.showConversationMembers(conversation)
            }
        } else {
            selectItem(conversation)
        }
    }

    override fun onButtonViewClick(button: ConversationButton) {
        if (!button.link.isNullOrEmpty()) {
            router.openLinkInWebView(button.link!!)
        } else {
            conversationListInteractor.onConversationMessageButtonClick(
                button.messageUUID,
                button.buttonId
            ).subscribe(
                {
                    when (it.errorCode) {
                        ErrorCode.SUCCESS -> Unit
                        ErrorCode.NETWORK_ERROR -> mView?.showSyncErrorNotification()
                        else -> mView?.showGroupInvitedDialog(it.errorMessage)
                    }
                },
                { error -> Timber.e(error, "Error when accept/reject invited group: ${error.message}.") }
            ).storeIn(singleDisposables)
        }
    }

    /**
     * Удаление диалога/чата через свайп панель
     * @param conversation модель диалога/чата, который будем удалять
     */
    override fun onSwipeRemoveClicked(conversation: ConversationModel) {
        traceDeletionIfNeeded {
            mView?.hideSwipePanel(conversation.compareUuid)
            lastClickedSwipedConversation = conversation
            when {
                conversation.isNotice -> {
                    mView?.showNoticeDeletingConfirmationDialog()
                }
                dialogType.isDeleted -> {
                    if (conversation.meIsOwner) {
                        mView?.showDeletingConfirmationDialogForAll()
                    } else {
                        mView?.showDeletingConfirmationDialog()
                    }
                }
                else -> {
                    deleteDialogs()
                }
            }
        }
    }

    /** @SelfDocumented */
    override fun onSwipeDismissed(uuid: UUID) {
        traceDeletionIfNeeded {
            lastSwipedDismissedUuid = uuid
            onDialogDismissed(uuid, isChannelTab())
        }
    }

    override fun onDismissedWithoutMessage(uuid: String?) {
        lastSwipedDismissedUuid = UUID.fromString(uuid)
    }

    /** @SelfDocumented */
    override fun onSwipeRestoreClicked(conversation: ConversationModel) {
        mView!!.hideSwipePanel(conversation.compareUuid)
        conversationListInteractor.undeleteDialogs(listOf(conversation.uuid))
            .subscribe(
                { /* Ничего не делаем */ },
                { error -> Timber.e(error, "Error when trying to restore dialog: ${conversation.uuid}") }
            )
            .storeIn(singleDisposables)
        analyticsUtil?.sendAnalytics(RecoverTheme(getSimpleNameForAnalytic()))
    }

    /**
     * Перенести в папку диалога/чата через свайп панель
     * @param conversation модель диалога/чата, который будем переносить
     */
    override fun onSwipeMoveToFolderClicked(conversation: ConversationModel) {
        mView!!.hideSwipePanel(conversation.compareUuid)
        lastClickedSwipedConversation = conversation
        // По умолчанию переписка принадлежит корневой папке
        var folderUuid = conversation.folderUuid
        if (folderUuid == null || !isFoldersEnabled) folderUuid = ROOT_FOLDER_UUID
        showPickFolderSelectionScreen(folderUuid)
    }

    /**
     * Отметить прочитанным/непрочитанным диалог/чат через свайп панель
     * @param conversation модель диалога/чата, который отметим
     */
    override fun onSwipeMarkClicked(conversation: ConversationModel, markAsRead: Boolean) {
        MetricsDispatcher.startTrace(FIREBASE_READ_DIALOG_BY_SWIPE_MENU)
        mView!!.hideSwipePanel(conversation.compareUuid)
        switchReadState(conversation, markAsRead)
        analyticsUtil?.sendAnalytics(
            if (markAsRead) {
                SwipeReadTheme(getSimpleNameForAnalytic())
            } else {
                SwipeUnreadTheme(getSimpleNameForAnalytic())
            }
        )
    }

    /**
     * Обработка клика по кнопке или свайпу удалить чат
     * @param conversation модель чата, который собираемся скрывать
     */
    override fun onSwipeHideClicked(conversation: ConversationModel, isByDismiss: Boolean) {
        if (!isByDismiss) {
            mView!!.hideSwipePanel(conversation.compareUuid)
        }
        selectedChatForHide = conversation
        mView?.showHideChatConfirmation()
    }

    /** @SelfDocumented */
    override fun onPinChatClicked(conversation: ConversationModel) {
        mView!!.hideSwipePanel(conversation.compareUuid)
        conversationListInteractor.pinChat(conversation.uuid)
            .subscribe(
                { updateDataList(false) },
                { error -> Timber.e(error, "Error when trying to pin chat: ") }
            )
            .storeIn(singleDisposables)
    }

    /** @SelfDocumented */
    override fun onUnpinChatClicked(conversation: ConversationModel) {
        mView!!.hideSwipePanel(conversation.compareUuid)
        conversationListInteractor.unpinChat(conversation.uuid)
            .subscribe(
                { updateDataList(false) },
                { error -> Timber.e(error, "Error when trying to unpin chat: ${conversation.uuid}") }
            )
            .storeIn(singleDisposables)
    }

    /** @SelfDocumented */
    override fun onRestoreChatClicked(conversation: ConversationModel) {
        mView!!.hideSwipePanel(conversation.compareUuid)
        conversationListInteractor.unhideChat(conversation.uuid)
            .subscribe(
                { /*Ничего не делаем*/ },
                { error -> Timber.e(error, "Error when trying to unpin chat: ") }
            )
            .storeIn(singleDisposables)
        analyticsUtil?.sendAnalytics(RecoverTheme(getSimpleNameForAnalytic()))
    }

    private fun hideSelectedChat() {
        selectedChatForHide?.let {
            conversationListInteractor
                .hideConversation(it.uuid)
                .subscribe({
                    if (removeItem(it.uuid)) selectedChatForHide = null
                }, { error ->
                    selectedChatForHide = null
                    Timber.e(error, "Error when trying to hide chat: ")
                }).storeIn(hideConversationDisposable)
        }
    }

    private fun removeItem(uuid: UUID, withResetSelection: Boolean = true): Boolean {
        if (isDestroyed) return false
        if (withResetSelection) {
            resetSelectionIfNeeded(listOf(uuid))
        }
        conversationList
            .indexOfFirst { uuid == (it as? ConversationModel)?.uuid }
            .takeUnless { it < 0 }
            ?.let {
                conversationList.removeAt(it)
                dataList.removeAt(it)
                val indexToRemove = mDataListOffset + it
                mView?.notifyItemsRemoved(indexToRemove, 1)
                return true
            }
        return false
    }

    override fun onHideChatConfirmationAlertClicked(isConfirmed: Boolean) {
        if (isConfirmed) {
            hideSelectedChat()
        } else {
            selectedChatForHide?.let {
                val index = conversationList.indexOf(it)
                if (index >= 0) {
                    val notifyIndex = index + mDataListOffset
                    mView?.notifyItemsChanged(notifyIndex, 1)
                }
            }
        }
    }

    override fun onAttachmentClick(dialogUuid: UUID, attachments: List<AttachmentViewModel>, attachmentsPosition: Int) {
        mView ?: return
        val attachment: AttachmentViewModel = attachments[attachmentsPosition]
        val fileInfo = attachment.fileInfoViewModel
        if (fileInfo.isFolder) {
            val folderId: UUID = fileInfo.id
            router.showFolder(
                DefAttachmentListComponentConfig(
                    AllowedActionResolver.AlwaysTrue(),
                    DefAttachmentListParams(
                        DefAttachmentListEntity.LocalFolder(
                            catalogId = dialogUuid,
                            cloudObjectId = null,
                            id = folderId,
                            localId = fileInfo.attachId,
                            localRedactionId = fileInfo.redId,
                            blObjectName = UrlUtils.FILE_SD_OBJECT,
                            name = fileInfo.title,
                        )
                    )
                )
            )
        } else {
            router.showViewerSlider(MessageUtils.createViewerSliderArgs(dialogUuid, attachment, attachments, analyticsUtil?.castTo()))
        }
    }

    override fun onThemeTypeSelected(type: EntitledItem) {
        when (type) {
            is ChatType -> onFilterSelected(listFilter.filterConfiguration!!.copy(chatType = type))
            is DialogType -> onFilterSelected(listFilter.filterConfiguration!!.copy(dialogType = type))
        }
    }

    private fun onFilterSelected(configuration: ConversationFilterConfiguration) {
        if (isChannelTab()) {
            onChatTypeChanged(configuration.chatType)
        } else {
            onDialogTypeChanged(configuration.dialogType)
            if (selectedFolderUuid != configuration.folderUuid) {
                onFolderSelected(configuration)
            }
        }
    }

    override fun setFoldersEnabled(enabled: Boolean) {
        if (isFoldersEnabled == enabled) return
        isFoldersEnabled = enabled

        if (!isFoldersEnabled) {
            selectedFolder = null
            selectedFolderUuid = rootFolderUuid
        }

        if (mDataListOffset == 0 && !isChannelTab()) {
            if (enabled && (conversationList.isEmpty() || conversationList[0] != FoldersConversationRegistryItem)) {
                conversationList.add(0, FoldersConversationRegistryItem)
                updateStubInList(conversationList)
                super.swapDataList(ArrayList(conversationList))
                mView?.updateDataListWithoutNotification(conversationList, mDataListOffset)
                mView?.notifyItemsInserted(0, 1)
            }
            if (!enabled && (conversationList.isNotEmpty() && conversationList[0] == FoldersConversationRegistryItem)) {
                conversationList.removeAt(0)
                updateStubInList(conversationList)
                super.swapDataList(ArrayList(conversationList))
                mView?.updateDataListWithoutNotification(conversationList, mDataListOffset)
                mView?.notifyItemsRemoved(0, 1)
            }
        }
    }

    override fun trySetFoldersSync() {
        foldersProvider.castTo<ThemeFoldersInteractorImpl>()?.trySetFoldersSync()
    }

    override fun moveDialogToNewFolder() {
        mOnlyRootFolderDisposable.set(
            foldersProvider.castTo<ThemeFoldersInteractorImpl>()?.newFolderObservable?.subscribe { uuid ->
                moveDialogsToFolder(uuid)
            }
        )
    }

    /**
     * Открытие выбранной папки
     * @param folder модель папки
     */
    override fun opened(folder: Folder) {
        onFilterSelected(listFilter.filterConfiguration!!.copyWithUpdatedFolder(folder))
    }

    /**
     * Обработка выбранной папки для перемещения диалога
     * @param folder модель папки
     */
    override fun selected(folder: Folder) {
        val folderUuid = UUID.fromString(folder.id.ifBlank { rootFolderUuid })
        moveDialogsToFolder(folderUuid)
    }

    override fun closed() = Unit

    override fun additionalCommandClicked() = Unit

    override fun onBackPressed(): Boolean =
        when {
            checkHelper.isCheckModeEnabled -> {
                hideCheckModeInternal()
                true
            }
            // завершить активность, если в корневом каталоге
            selectedFolderUuid == rootFolderUuid ->
                false
            else -> {
                // перейти в корень
                onRootFolderSelected()
                true
            }
        }

    override fun onPhoneVerificationRequired() {
        router.showPhoneVerification()
    }

    override fun isBranchTypeChanging(): Boolean = isBranchTypeChanging

    override fun onScrollToTopPressed() {
        if (mDataListOffset != 0) {
            makeSearchRequest()
        } else {
            resetUI()
        }
    }

    override fun onRefresh() {
        if (mSearchQuery.isNotEmpty()) {
            mPendingSearchRequest = true
        }
        if (isChannelTab()) {
            super.onRefresh()
        } else {
            // помечаем все диалоги просмотренными при рефреше от пользователя, обязательно до запроса данных
            markAllDialogsAsViewedWithCheck { super.onRefresh() }
        }
        mView?.hideAllSwipedPanels()
    }

    override fun resetUI() {
        super.resetUI()
        if (!checkHelper.isCheckModeEnabled) mScrollHelper.resetState()
    }

    private fun closeSwipe() {
        if (isTablet) {
            mView!!.hideAllSwipedPanels()
        } else {
            // задержка связана с конфликтом анимации открытия нового экрана и закрытия свайпа
            handler.postDelayed({ mView?.hideAllSwipedPanels() }, DELAY_FOR_CLOSING_SWIPE)
        }
    }

    /**
     * Действие, которое необходимо выполнить после следующего обновления списка
     */
    private fun onNextListUpdate(action: () -> Unit) {
        actionOnListUpdate = action
    }

    /**
     * Попытка выполнить сохранненое действие, которое необходимо после обновления списка
     */
    private fun tryInvokeOnListUpdateAction() {
        actionOnListUpdate?.let { action ->
            action()
            actionOnListUpdate = null
        }
    }

    /**
     * Обработка результата экрана селектора диалога и получателей для шаринга
     * @param result результат экрана
     * @see DialogSelectionResult
     */
    private fun handleDialogSelectionResult(result: DialogSelectionResult) {
        // TODO Быстрофикс для сброса поискового контекста на контроллере, при вызовах refresh по колбэкам остается прежний поисковый контекст, который был ранее введен на экране шаринга
        // https://online.sbis.ru/opendoc.html?guid=b946510b-5fc4-4a83-adbb-98f60a97ffef
        onRefresh()
        when (result) {
            is SelectedDialogResult        -> openSelectedDialogToShare(result)
            is SelectedParticipantsResult  -> openNewDialogWithParticipants(result)
            is CancelDialogSelectionResult -> mView?.cleanCurrentIntentSharingContent()
            else                           -> Unit
        }
    }

    /**
     * Открытие выбранного диалога для шаринга
     * @param dialog выбранный диалог
     */
    private fun openSelectedDialogToShare(dialog: SelectedDialogResult) = with(dialog) {
        if (dialog.documentType?.isNews == true) {
            val needToSetRecipient = !dialog.isSocnetEvent && dialog.isForMe
            router.showNewsDetails(
                documentUuid?.toString(),
                relevantMessageUuid,
                dialog.dialogUuid,
                needToSetRecipient,
                dialog.isSocnetEvent
            )
        } else if (dialog.documentType?.isDiscussion == true && dialog.documentUuid != null) {
            val replyMessageUuid = if (dialog.isForMe) {
                dialog.relevantMessageUuid
            } else {
                null
            }
            router.showArticleDiscussion(
                CommunicatorArticleDiscussionParams(
                    dialog.documentUuid!!,
                    dialog.dialogUuid,
                    replyMessageUuid
                )
            )
        } else {
            router.showConversationDetailsScreen(
                ConversationDetailsParams(
                    dialogUuid = dialogUuid,
                    messageUuid = relevantMessageUuid,
                    isChat = isChat,
                    fromChatTab = isChannelTab(),
                    files = files?.asArrayList(),
                    textToShare = text
                )
            )
        }
    }

    /**
     * Открытие нового диалога для шаринга с выбранными участниками
     * @param participants выбранные участники
     */
    private fun openNewDialogWithParticipants(participants: SelectedParticipantsResult) {
        if (participants.departmentUuids.isEmpty()) {
            router.showNewDialogToShare(participants.personUuids, participants.text, participants.files)
        } else {
            openNewDialogWithParticipantsFromDepartments(participants)
        }
    }

    /**
     * Открытие нового диалога для шаринга с выбранными участниками + участниками из отделов(папок)
     * @param participants выбранные участники
     */
    private fun openNewDialogWithParticipantsFromDepartments(participants: SelectedParticipantsResult) {
        if (participants.departmentUuids.isNotEmpty()) {
            conversationListInteractor.getPersonUuidsByDepartments(participants.departmentUuids)
                .subscribe(
                    { router.showNewDialogToShare(participants.personUuids.plus(it), participants.text, participants.files) },
                    { error -> error.localizedMessage?.let { mView?.showLoadingError(it) }}
                ).storeIn(singleDisposables)
        }
    }

    private fun observeAreaSyncStatus() {
        val syncArea = if (isChannelTab()) CHAT_SYNC_AREA else DIALOG_SYNC_AREA
        if (currentSyncArea == syncArea) {
            return
        }
        currentSyncArea = syncArea
        currentSyncAreaDisposable?.dispose()
        val subscription = CompositeDisposable()

        areaSyncStatusPublisher.incrementalSyncStatus(currentSyncArea)
            .debounce { areaStatus ->
                when (areaStatus) {
                    // показываем индикатор загрузки с задержкой
                    AreaStatus.RUNNING -> Observable.just(areaStatus).delay(300, TimeUnit.MILLISECONDS)
                    else               -> Observable.just(areaStatus)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { handleIncrementalSyncStatus(it) },
                { throwable -> Timber.e(throwable, "Incremental sync state error") }
            )
            .storeIn(subscription)

        areaSyncStatusPublisher.partialSyncStatus(currentSyncArea)
            .subscribe(
                { handlePartialSyncStatus(it) },
                { throwable -> Timber.e(throwable, "Partial sync state error") }
            )
            .storeIn(subscription)

        subscription.storeIn(singleDisposables)
        currentSyncAreaDisposable = subscription

        pagingLoadingObservable
            .debounce { loadingStatus ->
                when (loadingStatus) {
                    // показываем индикатор загрузки с задержкой, для избежания дребезга
                    LOADING -> Observable.just(loadingStatus).delay(150, TimeUnit.MILLISECONDS)
                    else -> Observable.just(loadingStatus)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { handlePagingLoadingStatus(it) },
                { throwable -> Timber.e(throwable) }
            )
            .storeIn(singleDisposables)
    }

    private fun handleIncrementalSyncStatus(areaStatus: AreaStatus) {
        val view = mView ?: return
        when (areaStatus) {
            AreaStatus.NOT_RUNNING -> {
                view.showNetworkWaitingIndicator(false)
                view.hideSyncIndicator()
            }
            AreaStatus.RUNNING -> {
                view.showNetworkWaitingIndicator(false)
                view.showSyncIndicator()
            }
            AreaStatus.NETWORK_WAITING -> {
                view.showNetworkWaitingIndicator(true)
                updateDataListForStub()
            }
            AreaStatus.ERROR -> {
                updateDataListForStub()
            }
        }
    }

    private fun handlePartialSyncStatus(areaStatus: AreaStatus) {
        when (areaStatus) {
            AreaStatus.NOT_RUNNING -> pagingLoadingObservable.onNext(NOT_LOADING)
            AreaStatus.RUNNING     -> pagingLoadingObservable.onNext(LOADING)
            else -> Timber.w("Partial sync status $areaStatus")
        }
    }

    private fun handlePagingLoadingStatus(loadingStatus: PagingLoadingStatus) {
        val view = mView ?: return
        when (loadingStatus) {
            LOADING     -> view.showPagingLoadingProgress(true)
            NOT_LOADING -> view.showPagingLoadingProgress(false)
            ERROR       -> view.showPagingLoadingError()
        }
    }

    private fun handleSyncStatusWithRefreshCallback(params: HashMap<String, String>?) {
        params ?: return

        val networkError = params[ERROR_EVENT_KEY] == NETWORK_EVENT_KEY
        val partialSync = params[SYNC_TYPE_EVENT_KEY] == SYNC_TYPE_PARTIAL_EVENT_VALUE
        val someError = networkError || params[ERROR_EVENT_KEY] == OTHER_EVENT_KEY
        if (someError && partialSync) {
            pagingLoadingObservable.onNext(ERROR)
        }

        val searchEventValue = params[REGISTRY_SEARCH_EVENT_KEY]
        val searchActive = searchEventValue in listOf(REGISTRY_SEARCH_EVENT_ACTIVE_VALUE, REGISTRY_SEARCH_EVENT_PAUSED_VALUE)
        val searchFinished = searchEventValue == REGISTRY_SEARCH_EVENT_FINISHED_VALUE
        when {
            searchActive && someError -> pagingLoadingObservable.onNext(ERROR)
            searchActive              -> pagingLoadingObservable.onNext(LOADING)
            searchFinished            -> pagingLoadingObservable.onNext(NOT_LOADING)
        }
    }

    private fun traceDeletionIfNeeded(action: () -> Unit) {
        if (!dialogType.isDeleted) {
            MetricsDispatcher.startTrace(FIREBASE_DELETE_DIALOG)
        }
        action()
        if (!dialogType.isDeleted) {
            MetricsDispatcher.stopTrace(FIREBASE_DELETE_DIALOG)
        }
    }

    override fun getDataRefreshCallback(): DataRefreshedThemeControllerCallback {
        return object: DataRefreshedThemeControllerCallback() {
            override fun onEvent(param: HashMap<String, String>) {
                onRefreshCallback(param)
            }
        }
    }

    /** Проверка на пустой список диалогов без учета элемента папок в списке */
    private fun dataListIsEmpty(): Boolean {
        return dataList.isEmpty() || (dataList.size == 1 && dataList.first() is FoldersConversationRegistryItem)
    }

    /** Завершить удаление диалога смахиванием при переходе в другой реестр или смене вкладки*/
    private fun completeDialogDismissing(isTabChanging: Boolean) {
        if (lastSwipedDismissedUuid != UUIDUtils.NIL_UUID) {
            if (isTabChanging) {
                listCommandAndKeeper.themeListCommand.deleteFromCache(isChannelTab(), lastSwipedDismissedUuid)
            }
            onSwipeDismissed(lastSwipedDismissedUuid)
            lastSwipedDismissedUuid = UUIDUtils.NIL_UUID
        }
        mView?.clearSwipeMenuState()
    }

    private suspend fun subscribeOnAppForegroundEvents() {
        appLifecycleTracker.appForegroundStateFlow
            // Интересует переход из background в foreground
            .runningReduce { prev, current -> !prev && current }
            .filter { it }
            .collectLatest {
                onThemeTabAfterOpen()
                withContext(Dispatchers.Main) {
                    // something
                }
            }
    }

    private fun onThemeTabBeforeOpen() {
        listCommandAndKeeper.themeListCommand.onThemeTabBeforeOpen()
            .subscribeOn(Schedulers.io())
            .subscribe(Functions.EMPTY_ACTION, Timber::e)
            .storeIn(singleDisposables)
    }

    private fun onThemeTabAfterOpen() {
        listCommandAndKeeper.themeListCommand.onThemeTabAfterOpen()
            .subscribeOn(Schedulers.io())
            .subscribe(Functions.EMPTY_ACTION, Timber::e)
            .storeIn(singleDisposables)
    }

    override fun subscribeOnProfileSettingsDataRefreshed() {
        scope.launch {
            withContext(Dispatchers.IO) {
                themesRegistryDependency.profileSettingsControllerWrapperProvider
                    ?.profileSettingsControllerWrapper?.get()?.subscribeDataRefreshedEvent {
                        tryAutoImportContacts()
                    }
            }
        }
    }

    override fun tryAutoImportContacts(savedInstanceState: Bundle?) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val needImportContactsFromPhone = themesRegistryDependency.profileSettingsControllerWrapperProvider
                    ?.profileSettingsControllerWrapper?.get()?.getNeedImportContactsFromPhone() ?: false
                if (savedInstanceState == null && mView?.shouldRequestContactsPermissions() == true &&
                    needImportContactsFromPhone
                ) {
                    mView?.importContactsSafe()
                }
            }
        }
    }

    override fun handleConversationPreviewAction(conversationPreviewMenuAction: ThemeConversationPreviewMenuAction) {
        conversationForPreview?.let {
            when (conversationPreviewMenuAction) {
                is Report -> {
                    mView?.showComplainDialogFragment(
                        ComplainUseCase.Conversation(
                            it.uuid,
                            isChannelTab()
                        )
                    )
                }
                is Delete -> {
                    if (isChannelTab()) {
                        onSwipeHideClicked(it, false)
                    } else {
                        deleteDialogsByPanel()
                    }
                }
                is MarkDialog -> {
                    checkHelper.enableCheckMode()
                    checkHelper.setChecked(it, true)
                }
                is MarkGroupAsRead -> markDialogsAsRead(true)
                is MarkGroupAsUnread -> markDialogsAsRead(false)
                is MoveGroupToFolder -> onMoveGroupOperationClicked()
                is Pin -> onPinChatClicked(it)
                is Unpin -> onUnpinChatClicked(it)
                is Restore -> onRestoreChatClicked(it)
                else -> Unit
            }
        }
        conversationForPreview = null
    }

    private fun getSimpleNameForAnalytic(): String = ThemePresenterImpl::class.java.simpleName

    override fun setSearchMessagesQuery(query: String) {
        onSearchQueryChanged(query)
        if (personFilter == null) {
            getSuggestPersons()
        }
    }

    private fun getSuggestPersons() {
        contactsObservable.subscribe { recipientList ->
            val suggestData = recipientList.map { it.suggestData }
            scope.launch { _suggestedPersons.emit(suggestData) }
        }.storeIn(searchContactsDisposable)
    }

    override fun setSearchMessagesPerson(person: PersonSuggestData?) {
        personFilter = person
        if (person == null) {
            getSuggestPersons()
        }
        makeSearchRequest()
    }

    override val suggestedPersons: Flow<List<PersonSuggestData>>
        get() = _suggestedPersons

    override val foundMessages: Flow<List<UUID>>
        get() = _foundMessages
    override val totalFoundMessagesCount: Flow<Int>
        get() = _totalFoundMessagesCount
}

private val DeclarationDocumentType?.isNews get() =
    when (this) {
        DeclarationDocumentType.NEWS,
        DeclarationDocumentType.SOCNET_NEWS,
        DeclarationDocumentType.NEWS_REPOST -> true
        else                                -> false
    }

private val DeclarationDocumentType?.isDiscussion
    get() =
        when (this) {
            DeclarationDocumentType.GROUP_DISCUSSION_TOPIC -> true
            else                                           -> false
        }

private val ContactVM.suggestData: PersonSuggestData
    get() = PersonSuggestData(
        PersonData(uuid, rawPhoto, initialsStubData),
        name
    )

private fun getDialogTypeBySpinnerState(@ConversationSettings.DialogsFilterState spinnerState: Int) = when (spinnerState) {
    DIALOGS_FILTER_ALL -> DialogType.ALL
    DIALOGS_FILTER_INCOME -> DialogType.INCOMING
    DIALOGS_FILTER_NOT_READ -> DialogType.UNREAD
    DIALOGS_FILTER_NO_ANSWER -> DialogType.UNANSWERED
    DIALOGS_FILTER_DELETED -> DialogType.DELETED
    else                     -> DialogType.ALL
}

@ConversationSettings.DialogsFilterState
private fun getSpinnerStateByDialogType(dialogType: DialogType): Int = when (dialogType) {
    DialogType.ALL -> DIALOGS_FILTER_ALL
    DialogType.INCOMING -> DIALOGS_FILTER_INCOME
    DialogType.UNREAD -> DIALOGS_FILTER_NOT_READ
    DialogType.UNANSWERED -> DIALOGS_FILTER_NO_ANSWER
    DialogType.DELETED -> DIALOGS_FILTER_DELETED
    else                  -> DIALOGS_FILTER_ALL
}

private fun getChatTypeBySpinnerState(@ConversationSettings.ChatsFilterState spinnerState: Int) = when (spinnerState) {
    CHATS_FILTER_ALL -> ChatType.ALL
    CHATS_FILTER_NOT_READ -> ChatType.UNREAD
    CHATS_FILTER_HIDDEN -> ChatType.HIDDEN
    else                  -> ChatType.ALL
}

@ConversationSettings.ChatsFilterState
private fun getSpinnerStateByChatType(chatType: ChatType): Int = when (chatType) {
    ChatType.ALL -> CHATS_FILTER_ALL
    ChatType.UNREAD -> CHATS_FILTER_NOT_READ
    ChatType.HIDDEN -> CHATS_FILTER_HIDDEN
    else            -> DIALOGS_FILTER_ALL
}

/**
 * @param checkListItems           - коллекция выбранных элементов
 * @param onlyItemsThatCanBeMarked - если истина, метод вернёт uuid'ы элементов,
 * которые могут быть только помечены (например, как прочтённые), иначе – всех элементов которые так же могут быть удалены
 * @return - список uuid'ов, согласно опциям.
 */
private fun getUuids(
    checkListItems: Collection<ConversationModel>?,
    onlyItemsThatCanBeMarked: Boolean = false
): ArrayList<UUID> {
    if (checkListItems == null) return arrayListOf()

    return checkListItems.asSequence().filter {
        !onlyItemsThatCanBeMarked || it.canBeMarked()
    }.map {
        it.uuid
    }.toCollection(ArrayList())
}

private fun ConversationModel.canBeMarked() =
    !isChatForOperations

/**
 * Если диалог содержится в списке, применяет действие, в параметры которого передаёт индекс диалога в нём
 */
private inline fun applyIfContains(changedDialogs: List<ConversationModel>, dialog: ConversationModel, action: (Int) -> Unit) {
    // Перевёл алгоритм из ConversationListPresenter'а без изменения логики,
    // но, скорее всего, здесь нужен первый индекс, а не последний, лол О_о
    changedDialogs.indices.lastOrNull {
        changedDialogs[it].uuid == dialog.uuid
    }?.apply(action)
}

private fun Collection<ConversationRegistryItem>.castToConversationModelList(): MutableList<ConversationModel> =
    filterIsInstanceTo(ArrayList(size), ConversationModel::class.java)

private enum class PagingLoadingStatus {
    LOADING, NOT_LOADING, ERROR
}

/** @SelfDocumented */
internal fun ThemeFilter.isUnread() =
    themeType == ConversationType.DIALOG && dialogFilter == DialogFilter.UNREAD ||
        themeType == ConversationType.CHAT && chatFilter == ChatFilter.UNREAD

private const val FILES_TASKS_DIALOG_CLOUD_FEATURE = "files_tasks_dialog"