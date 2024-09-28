package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist;

import static ru.tensor.sbis.communicator.base_folders.CommunicatorBaseFoldersProviderKt.ROOT_FOLDER_UUID;
import static ru.tensor.sbis.communicator.contacts_registry.ContactsRegistryFeatureFacade.contactsDependency;
import static ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListFragmentKt.DEFAULT_MAX_CONTACT_ITEMS_ON_SCREEN;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.SerialDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import ru.tensor.sbis.base_components.adapter.checkable.ObservableCheckCountHelper;
import ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper;
import ru.tensor.sbis.common.generated.ErrorCode;
import ru.tensor.sbis.common.generated.EventType;
import ru.tensor.sbis.common.navigation.NavxId;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.common.util.UUIDUtils;
import ru.tensor.sbis.common.util.scroll.ScrollEvent;
import ru.tensor.sbis.common.util.scroll.ScrollHelper;
import ru.tensor.sbis.communication_decl.analytics.AnalyticsEvent;
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil;
import ru.tensor.sbis.communicator.common.analytics.ContactsAnalyticsEvent.ChangeContactsFilter;
import ru.tensor.sbis.communicator.common.analytics.ContactsAnalyticsEvent.GoToFoldersContacts;
import ru.tensor.sbis.communicator.common.analytics.ContactsAnalyticsEvent.ImportContacts;
import ru.tensor.sbis.communicator.common.analytics.ContactsAnalyticsEvent.MoveContactsToFolders;
import ru.tensor.sbis.communicator.common.analytics.ContactsAnalyticsEvent.OpenContactsInCompany;
import ru.tensor.sbis.communicator.common.analytics.ContactsAnalyticsEvent.OpenFindNewContacts;
import ru.tensor.sbis.communicator.common.analytics.ContactsAnalyticsEvent.OpenedFoldersContacts;
import ru.tensor.sbis.communicator.common.analytics.ContactsAnalyticsEvent.SearchContacts;
import ru.tensor.sbis.communicator.common.data.model.NetworkAvailability;
import ru.tensor.sbis.communicator.contacts_registry.list.ContactListInteractor;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactFoldersModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactRegistryModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsStubModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.stub_helper.ContactsStubHelper;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.stub_helper.ContactsStubHelperImpl;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.stub_helper.ContactsStubs;
import ru.tensor.sbis.communicator.contacts_registry.ui.filters.ContactFilterConfiguration;
import ru.tensor.sbis.communicator.contacts_registry.ui.folders.ContactListFoldersInteractorImpl;
import ru.tensor.sbis.communicator.contacts_registry.ui.spinner.ContactSortOrder;
import ru.tensor.sbis.communicator.contacts_registry.ui.spinner.ContactSortOrderProvider;
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsDispatcher;
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsType;
import ru.tensor.sbis.communicator.declaration.model.EntitledItem;
import ru.tensor.sbis.design.SbisMobileIcon;
import ru.tensor.sbis.design.folders.data.model.Folder;
import ru.tensor.sbis.design.stubview.StubViewContent;
import ru.tensor.sbis.design.utils.DebounceActionHandler;
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle;
import ru.tensor.sbis.mvp.data.model.PagedListResult;
import ru.tensor.sbis.mvp.search.BaseSearchablePresenter;
import ru.tensor.sbis.mvp_extensions.view_state.EmptyViewState;
import ru.tensor.sbis.verification_decl.account.UserAccount;
import ru.tensor.sbis.verification_decl.login.LoginInterface;
import ru.tensor.sbis.verification_decl.red_button.RedButtonActivatedProvider;
import timber.log.Timber;

/**
 * Реализация презентера реестра контактов
 *
 * @author da.zhukov
 * @see ContactListContract.Presenter
 */
public class ContactListPresenterImpl
        extends BaseSearchablePresenter<ContactListContract.View, ContactRegistryModel, Pair<EventType, HashMap<String, String>>>
        implements ContactListContract.Presenter {

    private static final long TABLET_CONTACT_SELECTION_DEBOUNCE = 200;
    private static final long SEARCH_RESET_PAGINATION_DEBOUNCE = 2000;
    private static final long DEBOUNCE_HANDLER_VALUE_FOR_ANALYTICS_SEND = 3000;
    private static final int FOLDERS_WITH_STUB_ITEMS_COUNT = 2;

    private final boolean mIsTablet;

    /**
     * Разрешение на добавление новых контактов
     */
    private boolean mCanAddNewContacts = true;

    private AnalyticsUtil analyticsUtil = null;
    private final DebounceActionHandler debounceActionHandler = new DebounceActionHandler(DEBOUNCE_HANDLER_VALUE_FOR_ANALYTICS_SEND);

    // BL delegates
    @Nullable
    private final RedButtonActivatedProvider mRedButtonActivatedProvider;
    @NonNull
    private final ContactListInteractor mInteractor;
    @NonNull
    private final ContactListFoldersInteractorImpl mFoldersInteractor;
    @NonNull
    private final ObservableCheckCountHelper<ContactRegistryModel> mCheckHelper;
    @NonNull
    private final SelectionHelper<ContactRegistryModel> mSelectionHelper;
    @NonNull
    private final ContactsStubHelper mContactsStubHelper;

    private final PublishSubject<ContactsModel> contactSelectionSubject = PublishSubject.create();
    @NonNull
    private final LoginInterface mLoginInterface;

    private final PublishSubject<Boolean> resetPaginationSubject = PublishSubject.create();

    private boolean needImportContacts = true;

    // для метрики первой загрузки
    private boolean isFirstDataLoading = true;
    private boolean isFirstResume = true;

    private final NetworkAvailability mNetworkAvailability;

    // Disposables
    private final CompositeDisposable mSingleDisposables = new CompositeDisposable();
    private final SerialDisposable mCheckCountDisposable = new SerialDisposable();
    private final SerialDisposable mCheckModeStateDisposable = new SerialDisposable();
    private final SerialDisposable mSelectionDisposable = new SerialDisposable();
    private final SerialDisposable mMoveContactDisposable = new SerialDisposable();
    private final CompositeDisposable mDeleteContactsDisposable = new CompositeDisposable();
    private final SerialDisposable mFolderListDisposable = new SerialDisposable();
    private final SerialDisposable mSelectedFolderDisposable = new SerialDisposable();
    private final SerialDisposable mOnlyRootFolderDisposable = new SerialDisposable();

    // Navigation
    private final ContactSortOrderProvider mOrderProvider;
    private boolean needToResetPagination = false;

    //region folders
    @NonNull
    private final String mRootFolderUuid = ROOT_FOLDER_UUID.toString();
    private Folder mSelectedFolder;
    @NonNull
    private String mSelectedFolderUuid = mRootFolderUuid;
    private final ContactRegistryModel mFolderListItem = ContactFoldersModel.INSTANCE;
    private boolean mIsFoldersEnabled = false;
    private boolean mIsFolderTitleChanged = false;
    //endregion

    // Data cache
    @NonNull
    private List<ContactRegistryModel> mContactList = new ArrayList<>();
    @Nullable
    private ContactsModel mLastClickedSwipedContact;
    private UUID mLastSwipedDismissedUuid = UUIDUtils.NIL_UUID;
    private ArrayList<AddContactOption> mAddContactOptions;
    private boolean importContactsOptionEnabled;
    private int maxContactsOnScreen = DEFAULT_MAX_CONTACT_ITEMS_ON_SCREEN;
    private boolean needToRespondToEvents = true;
    private boolean isViewHidden = false;

    private boolean lastCheckEmployeesTabIsGranted = false;

    public ContactListPresenterImpl(
        @NonNull ContactSortOrderProvider orderProvider,
        @NonNull ContactListInteractor interactor,
        @NonNull ContactListFoldersInteractorImpl foldersInteractor,
        @NonNull LoginInterface loginInterface,
        @NonNull NetworkUtils networkUtils,
        @NonNull SelectionHelper<ContactRegistryModel> selectionHelper,
        @NonNull ScrollHelper scrollHelper,
        @NonNull ObservableCheckCountHelper<ContactRegistryModel> checkHelper,
        @Nullable RedButtonActivatedProvider redButtonActivatedProvider,
        @NonNull NetworkAvailability networkAvailability,
        @NonNull ContactsStubHelper contactsStubHelper,
        boolean isViewHidden,
        boolean importContactsOptionEnabled
    ) {
        super(null, networkUtils, scrollHelper);
        mInteractor = interactor;
        mFoldersInteractor = foldersInteractor;
        mLoginInterface = loginInterface;
        mSelectionHelper = selectionHelper;
        mCheckHelper = checkHelper;
        mContactsStubHelper = contactsStubHelper;
        mRedButtonActivatedProvider = redButtonActivatedProvider;
        mIsTablet = mSelectionHelper.isTablet();
        this.importContactsOptionEnabled = importContactsOptionEnabled;
        storeAllDisposables();
        initAddContactOptions();
        subscribeToCheckedCount();
        initCanAddNewContactsFeature();
        mNetworkAvailability = networkAvailability;
        this.isViewHidden = isViewHidden;

        mSelectionDisposable.set(mSelectionHelper.getItemSelectionObservable()
                .subscribe(
                        model -> {
                            if (mView != null && model instanceof ContactsModel) {
                                mView.hideKeyboard();
                                boolean hasSelection = !UUIDUtils.isNilUuid(((ContactsModel) model).getContact().getUuid());

                                if (hasSelection) {
                                    mView.showContactDetailsScreen(((ContactsModel) model).getContact().getUuid());
                                } else {
                                    mView.hideContactDetails();
                                }
                            }
                        },
                        throwable -> Timber.e(throwable, "Error when trying to process item selection in contacts list.")
                )
        );

        subscribeContactSelection();

        mOrderProvider = orderProvider;
        makeSearchRequest();
        subscribeListCacheChanges();
        subscribeMessageSentEvents();
        subscribeProfileSettingsEvents();
        subscribeOnSearchResetPagination();
        initAnalyticsUtil();
    }

    private void subscribeListCacheChanges() {
        mSingleDisposables.add(mInteractor.observeContactsListCacheChanges()
                .subscribe(Unit -> eventResponse(false)));
    }

    private void subscribeMessageSentEvents() {
        mSingleDisposables.add(mInteractor.observeMessageSentEvents()
                .subscribe(Unit -> eventResponse(true)));
    }

    private void subscribeProfileSettingsEvents() {
        mSingleDisposables.add(mInteractor.observeProfileSettingsEvents()
                .subscribe(Unit -> tryAutoImportContacts()));
    }

    private void eventResponse(boolean fromPullToRefresh) {
        if (needToRespondToEvents) {
            updateDataList(fromPullToRefresh);
        }
    }

    private void subscribeContactSelection() {
        mSingleDisposables.add(contactSelectionSubject
                .debounce(mIsTablet ? TABLET_CONTACT_SELECTION_DEBOUNCE : 0, TimeUnit.MILLISECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleContactSelection));
    }

    /**
     * Получение флага доступности фичи добавления новых контактов.
     * В случае получения false -> необходимо блокировать доступ к экранам добавления контактов
     */
    private void initCanAddNewContactsFeature() {
        mSingleDisposables.add(
                mInteractor.canAddNewContacts()
                        .subscribe(canAddNewContacts -> mCanAddNewContacts = canAddNewContacts)
        );
    }

    private void initAnalyticsUtil() {
        AnalyticsUtil.Provider analyticsProvider = contactsDependency.getAnalyticsUtilProvider();
        if (analyticsProvider != null) {
            analyticsUtil = analyticsProvider.getAnalyticsUtil();
        }
    }

    private void sendAnalytics(AnalyticsEvent analyticsEvent) {
        if (analyticsUtil != null) {
            analyticsUtil.sendAnalytics(analyticsEvent);
        }
    }

    //region Lifecycle methods
    @Override
    public void attachView(@NonNull ContactListContract.View view) {
        super.attachView(view);
        if (mView != null) {
            mView.enableHeaders(getHeadersEnabled());
            mView.setSearchText(mSearchQuery);
            mView.updateEmptyViewState(EmptyViewState.DEFAULT);
            mView.attachSelectionHelper(mSelectionHelper);
            mView.attachCheckHelper(mCheckHelper);
            mView.showStub(mContactsStubHelper.getCurrentStub());
            updateSelectedFolderTitle();
        }
        subscribeToCheckModeState();
        updateFilterString();
        restoreFolderTitle();
    }

    @Override
    public void viewIsStarted() {
        super.viewIsStarted();
        if (!isFirstResume) {
            updateDataList(true);
        }
    }

    @Override
    public void viewIsResumed() {
        super.viewIsResumed();
        if (mView != null) {
            mView.setFabVisible(!isUsefulFolder() && !mCheckHelper.isCheckModeEnabled());
            if (mCheckHelper.isCheckModeEnabled()) {
                mScrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_DOWN_FAKE);
            }
            if (!isFirstResume) {
                mView.showSearchPanel();
            }
            isFirstResume = false;
        }
    }

    private void tryAutoImportContacts() {
        if (importContactsOptionEnabled) {
            mSingleDisposables.add(
                    mInteractor.getNeedImportContactsFromPhone()
                            .subscribe(needImportContactsFromPhone -> {
                                if (mView != null) {
                                    if (mView.checkShouldRequestContactsPermissions() && needImportContacts &&
                                            needImportContactsFromPhone) {
                                        needImportContacts = false;
                                        mView.importContacts();
                                    }
                                }
                            })
            );
        }
    }
    //endregion

    private void updateSelectedFolderTitle() {
        if (mView == null || !mIsFolderTitleChanged) return;
        if (mRootFolderUuid.equals(mSelectedFolderUuid)) {
            mView.setFolderTitle(null);
        } else {
            mView.setFolderTitle(mSelectedFolder.getTitle());
            mView.setFoldersCompact();
        }
        mIsFolderTitleChanged = false;
    }

    private void restoreFolderTitle() {
        if (!mSelectedFolderUuid.equals(mRootFolderUuid)
                && mView != null
                && mSelectedFolder != null) {
            mView.setFolderTitle(mSelectedFolder.getTitle());
        }
    }

    @Override
    public void setFoldersEnabled(boolean enabled) {
        if (mIsFoldersEnabled == enabled) return;
        mIsFoldersEnabled = enabled;

        if (!mIsFoldersEnabled) {
            mSelectedFolder = null;
            mSelectedFolderUuid = mRootFolderUuid;
        }

        if (mDataListOffset == 0) {
            updateFoldersItemInList(true);
            updateStubItemInList(false);
        }
    }

    @Override
    public void setContactItemsMaxCountOnScreen(int count) {
        maxContactsOnScreen = count;
    }

    private void storeAllDisposables() {
        mSingleDisposables.addAll(
            mCheckCountDisposable,
            mCheckModeStateDisposable,
            mSelectionDisposable,
            mMoveContactDisposable,
            mDeleteContactsDisposable,
            mFolderListDisposable,
            mSelectedFolderDisposable,
            mOnlyRootFolderDisposable
        );
    }

    @Override
    public void detachView() {
        completeContactDismissing();
        mSelectionHelper.detachAdapter();
        if (mView != null) setFirstVisibleItemPosition(mView.firstCompletelyVisibleItemPosition());
        super.detachView();
    }

    @Override
    public void onDestroy() {
        mSingleDisposables.dispose();
        super.onDestroy();
    }

    @Override
    protected boolean isNewerPaginationSupported() {
        return false;
    }
    //endregion

    //region Init methods
    private void initAddContactOptions() {
        mAddContactOptions = new ArrayList<>();
        if (importContactsOptionEnabled) {
            mAddContactOptions.add(AddContactOption.IMPORT_CONTACT);
        }
        if (isCorporateAccount()) {
            mAddContactOptions.add(AddContactOption.CONTACT_IN_COMPANY);
        }
        mAddContactOptions.add(AddContactOption.NEW_CONTACT);
    }

    private void subscribeToCheckedCount() {
        mCheckCountDisposable.set(
                mCheckHelper.getCheckedCountObservable()
                        .subscribe(count -> {
                                if (mView != null) {
                                    mView.onCheckStateChanged(count > 0);
                                }
                            }
                        )
        );
    }

    private void subscribeToCheckModeState() {
        mCheckModeStateDisposable.set(
                mCheckHelper.getCheckModeEnabledObservable()
                        .subscribe(
                                enabled -> {
                                    if (enabled) {
                                        if (mView != null) {
                                            mView.disableFilters();
                                            mView.showCheckMode();
                                        }
                                        mScrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_DOWN_FAKE);
                                    } else if (mView != null) {
                                        mView.hideCheckMode();
                                        mView.enableFilters();
                                    }
                                },
                                throwable -> Timber.e(throwable, "Error when trying to process check mode switching in contacts list.")
                        )
        );
    }
    //endregion

    private void selectFolder(@Nullable Folder folder) {
        if (importContactsOptionEnabled && folder != null &&
                folder.getTitle().equals("Импортированные контакты") &&
                folder.getTotalContentCount() == 0 && mView != null ) {
            mView.importContacts();
            sendAnalytics(new ImportContacts(getSimpleNameForAnalytic()));
            return;
        }
        if (folder != null && !folder.getId().equals(mRootFolderUuid)) {
            mSelectedFolder = folder;
            mSelectedFolderUuid = folder.getId();
            sendAnalytics(new GoToFoldersContacts(getSimpleNameForAnalytic()));
            updateViewByFolderChanges();
        } else {
            onRootFolderSelected();
        }
    }

    @Override
    public void onRootFolderSelected() {
        if (mSelectedFolderUuid.equals(mRootFolderUuid)) return;
        mSelectedFolder = null;
        mSelectedFolderUuid = mRootFolderUuid;
        updateViewByFolderChanges();
        resetUI();
    }

    private void updateViewByFolderChanges() {
        mIsFolderTitleChanged = true;
        hideCheckModeInternal();
        updateSpecificViewsByFolderChanges();
        if (mView != null && !mSearchQuery.isEmpty()) {
            mView.clearSearchQuery();
        } else {
            updateDataList(true);
        }
    }

    private void updateSpecificViewsByFolderChanges() {
        if (mView == null) return;
        mView.setFilterVisible(!isUsefulFolder());
        mView.setFabVisible(!isUsefulFolder());
    }

    private boolean isUsefulFolder() {
        //постоянный uuid папки "полезные контакты"
        final String usefulFolderUuid = "abcdabcd-bf6d-415d-1015-abcbacbcacba";
        return mSelectedFolderUuid.equals(usefulFolderUuid);
    }

    @Override
    public void onContactTypeSelected(ContactSortOrder item) {
        onFilterSelected(new ContactFilterConfiguration(item, mSelectedFolder));
    }

    /**
     * Открытие выбранной папки
     * @param folder модель папки
     */
    @Override
    public void opened(@NotNull Folder folder) {
        onFilterSelected(
            new ContactFilterConfiguration(
                getCurrentOrder(),
                folder
            )
        );
        syncSelectedFolderTitle(folder);
    }

    private void syncSelectedFolderTitle(@NonNull Folder selectedFolder) {
        syncFolders();
        mSelectedFolderDisposable.set(
            mFoldersInteractor.getSelectedFolderSubject(selectedFolder.getId())
                .subscribe(folder -> {
                    if (mSelectedFolder != null
                            && mSelectedFolder.getId().equals(folder.getId())
                            && !mSelectedFolder.getTitle().equals(folder.getTitle())) {
                        mIsFolderTitleChanged = true;
                        mSelectedFolder = folder;
                        updateSelectedFolderTitle();
                    }
                })
        );
    }

    @Override
    public void syncFolders() {
        mFolderListDisposable.set(mFoldersInteractor.list().subscribe());
    }

    @Override
    public void moveContactToNewFolder() {
        mOnlyRootFolderDisposable.set(mFoldersInteractor.getNewFolderObservable()
            .subscribe( uuid -> {
                if (mLastClickedSwipedContact != null) {
                    moveContact(mLastClickedSwipedContact, uuid);
                }
            })
        );
    }

    /**
     * Обработка выбранной папки для перемещения контакта
     * @param folder модель папки
     */
    @Override
    public void selected(@NotNull Folder folder) {
        String folderId = StringUtils.isNotBlank(folder.getId()) ?
                folder.getId() : mRootFolderUuid;
        UUID folderUuid = Objects.requireNonNull(UUIDUtils.fromString(folderId));
        List<ContactRegistryModel> checked = mCheckHelper.getChecked();
        if (!checked.isEmpty()) {
            moveContacts(checked, folderUuid);
            hideCheckModeInternal();
        } else if (mLastClickedSwipedContact != null) {
            moveContact(mLastClickedSwipedContact, folderUuid);
        }
    }

    @Override
    public void closed() {}

    @Override
    public void additionalCommandClicked() {}

    private void onFilterSelected(ContactFilterConfiguration configuration) {
        ContactSortOrder newSortOrder = configuration.getContactSortOrder();
        if (getCurrentOrder() != newSortOrder) {
            onOrderChanged(newSortOrder);
        }
        if (!mSelectedFolderUuid.equals(configuration.getFolderUuid())) {
            selectFolder(configuration.getSelectedFolder());
            resetUI();
        }
    }

    @Override
    public ContactSortOrder getCurrentOrder() {
        return mOrderProvider.get();
    }

    @Override
    public void onOrderChanged(@NonNull ContactSortOrder order) {
        if (order != getCurrentOrder()) {
            mOrderProvider.set(order);
            updateFilterString();
            needToResetPagination = false;
            makeSearchRequest();
            sendAnalytics(new ChangeContactsFilter(getSimpleNameForAnalytic(), order.getTitleRes()));
        }
    }

    @Override
    public void onScrollToTopPressed() {
        if (mDataListOffset != 0) {
            makeSearchRequest();
        } else {
            resetUI();
        }
    }

    //region Search
    @Override
    protected void makeSearchRequest() {
        super.makeSearchRequest();
        if (mView != null) {
            mView.showProgress();

        }
        resetPaginationSubject.onNext(true);
        updateDataList(!isViewHidden);
    }

    //Сброс состояния пагинации и текущего списка во время долгого поиска
    //Механика отсутствует в базовой реализации некрудового презентера
    private void subscribeOnSearchResetPagination() {
        mSingleDisposables.add(
            resetPaginationSubject.debounce(SEARCH_RESET_PAGINATION_DEBOUNCE, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(it -> {
                    if (needToResetPagination && mLoadingState != LoadingState.NOT_LOADING) {
                        resetUI();
                        resetPagination(true);
                    }
                })
        );
    }

    @Override
    public void onSearchButtonClicked() {
        if (mView != null) mView.hideCursorFromSearch();
    }

    @Override
    protected void processUpdatingDataListResult(@NonNull PagedListResult<ContactRegistryModel> pagedListResult, boolean updatingFromTail) {
        stopFirstLoadMetric();
        needToResetPagination = false;
        if (mView != null) mView.enableHeaders(getHeadersEnabled());
        HashMap<String, String> metaData = pagedListResult.getMetaData();
        handleMetaDataResult(metaData);
        super.processUpdatingDataListResult(pagedListResult, updatingFromTail);
    }

    private void handleMetaDataResult(HashMap<String, String> metadata) {
        if (metadata != null) {
            ContactsStubs stub = mContactsStubHelper.createStub(metadata);
            if (stub != null && stub.equals(ContactsStubs.NO_CONNECTION)) {
                mNetworkAvailability.off();
                mShowOlderProgress = false;
                processNetworkError();
            } else {
                mNetworkAvailability.on();
            }
            if (mView != null) {
                mView.showStub(stub);
            }
        }
    }

    @Override
    protected void resetPagination() {
        resetPagination(false);
        needToResetPagination = true;
    }

    @Override
    public void onSearchQueryChanged(@NonNull String searchQuery) {
        if (!TextUtils.isEmpty(searchQuery)) {
            mCheckHelper.disableCheckMode();
            if (mView != null) {
                mView.setFabVisible(!isUsefulFolder());
            }
            debounceActionHandler.handle(() -> {
                sendAnalytics(new SearchContacts(getSimpleNameForAnalytic()));
                return Unit.INSTANCE;
            });
        }
        super.onSearchQueryChanged(searchQuery);
    }
    //endregion

    private boolean getHeadersEnabled() {
        return ContactSortOrder.BY_LAST_MESSAGE_DATE.equals(getCurrentOrder());
    }

    @Override
    public void onContactItemClicked(@NonNull ContactsModel model) {
        if (mCheckHelper.isCheckModeEnabled()) {
            if (model.getContact().isInMyContacts()) {
                if (!model.getContact().isMyAccountManager()) {
                    mCheckHelper.setChecked(model, !mCheckHelper.isChecked(model));
                } else if (mView != null) {
                    mView.showInformationPopup(ru.tensor.sbis.communicator.design.R.string.communicator_account_manager_clicked_toast, getIcon(SbisMobileIcon.Icon.smi_alert.getCharacter()));
                }
            }
        } else {
            DebounceActionHandler.Companion.getINSTANCE().handle(() -> {
                contactSelectionSubject.onNext(model);
                return Unit.INSTANCE;
            });
        }
    }

    @Override
    public void onContactPhotoClicked(@NonNull ContactsModel model) {
        onContactItemClicked(model);
    }

    private void handleContactSelection(@NonNull ContactsModel contact) {
        if (mView != null) {
            mView.closeAllOpenSwipeMenus();
        }
        mSelectionHelper.selectItem(contact);
    }

    @Override
    public void onContactItemLongClicked(@NonNull ContactsModel model) {
        DebounceActionHandler.Companion.getINSTANCE().handle(() -> {
            handleContactItemLongClicked(model);
            return Unit.INSTANCE;
        });
    }

    private void handleContactItemLongClicked(@NonNull ContactsModel model) {
        if (isUsefulFolder()) return;
        if (model.getContact().isInMyContacts() && TextUtils.isEmpty(mSearchQuery)) {
            mCheckHelper.enableCheckMode();
            if (mView != null) {
                mView.setFabVisible(false);
            }
            if (!model.getContact().isMyAccountManager()) {
                mCheckHelper.setChecked(model, true);
            }
        }
    }

    //region TwoWayPagination implementation
    @Override
    protected boolean isNeedLoadNewerPage(int firstVisibleItemPosition) {
        return mDataListOffset > 0 &&
                firstVisibleItemPosition - mDataListOffset <= getItemsReserve()
                && mLoadingState == LoadingState.NOT_LOADING;
    }

    @NonNull
    @Override
    protected List<ContactRegistryModel> getDataList() {
        return mContactList;
    }

    @Override
    protected void swapDataList(@NonNull List<ContactRegistryModel> dataList) {
        if (mContactList != dataList) {
            mContactList = dataList;
        }
        updateFoldersItemInList(false);
        updateStubItemInList(false);
        hideOrShowSearchPanelIfNeeded(mContactList);
    }

    /**
     * Обновить наличие элемента папок в списке контактов.
     *
     * @param withNotify true, если совместно с обновлением списка необходимо обновить UI
     */
    private void updateFoldersItemInList(boolean withNotify) {
        updateSelectedFolderTitle();
        if (mIsFoldersEnabled
                && mSelectedFolderUuid.equals(mRootFolderUuid)
                && mDataListOffset == 0
                && (mContactList.isEmpty() || !(mContactList.get(0) instanceof ContactFoldersModel))
        ) {
            mContactList.add(0, mFolderListItem);
            if (withNotify && mView != null) mView.notifyItemsInserted(0, 1);
        } else {
            if (!mContactList.isEmpty() && mContactList.get(0) instanceof ContactFoldersModel) {
                mContactList.remove(0);
                if (withNotify && mView != null) mView.notifyItemsRemoved(0, 1);
            }
        }
    }

    /**
     * Обновить наличие элемента заглушки в списке контактов.
     *
     * @param withNotify true, если совместно с обновлением списка необходимо обновить UI
     */
    private void updateStubItemInList(boolean withNotify) {
        boolean folderInList = !mContactList.isEmpty() && (mContactList.size() == 1 && mContactList.get(0) instanceof ContactFoldersModel);
        if (mContactList.isEmpty() || (folderInList && !needToResetPagination)) {
            HashMap<Integer, Function0<Unit>> action = new HashMap<>();
            action.put(
                    ru.tensor.sbis.design.stubview.R.string.design_stub_view_no_connection_details_clickable,
                    () -> {
                        onRefresh();
                        return Unit.INSTANCE;
                    });

            ContactsStubs stub = mContactsStubHelper.getCurrentStub();
            StubViewContent content = stub != null ? stub.toStubCaseContent(action) :
                    ContactsStubHelperImpl.Companion.noContactsStubViewContent(action);
            ContactsStubModel contactsStubModel = new ContactsStubModel(content);
            if (folderInList) {
                mContactList.add(1, contactsStubModel);
                if (withNotify && mView != null) mView.notifyItemsInserted(1, 1);
            }
        }
    }

    /**
     * Показать строку поиска в реестре контактов, если нет контактов.
     *
     * @param contactList список контактов
     */
    private void hideOrShowSearchPanelIfNeeded(List<ContactRegistryModel> contactList) {
        if (mView == null) return;
        if (contactList.isEmpty() || contactList.get(contactList.size() - 1) instanceof ContactsStubModel) {
            mView.showSearchPanel();
        }
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<ContactRegistryModel>> getLoadingNewerDataObservable(@Nullable ContactRegistryModel contactRegistryModel, int itemsCount) {
        // Корректируем размер для 0 страницы
        itemsCount = Math.min(mDataListOffset, itemsCount);
        int from = mDataListOffset - itemsCount;
        return loadContacts(contactRegistryModel, from, itemsCount, !isViewHidden);
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<ContactRegistryModel>> getLoadingOlderDataObservable(@NonNull ContactRegistryModel contactRegistryModel, int itemsCount) {
        int from = mDataListOffset + mContactList.size();
        return loadContacts(contactRegistryModel, from, itemsCount, !isViewHidden);
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<ContactRegistryModel>> getUpdatingListByLastEntityObservable(@Nullable ContactRegistryModel contactRegistryModel, int itemsCount, boolean fromPullToRefresh) {
        startFirstLoadMetric(fromPullToRefresh);
        final int from = mDataListOffset;
        // Из-за папок компонент списка неправильно счтитает кол-во элементов при добавлении первого контакта в список
        int count = itemsCount > 0 ? mContactList.size() >= FOLDERS_WITH_STUB_ITEMS_COUNT ? itemsCount : maxContactsOnScreen : getPageSize();
        return loadContacts(contactRegistryModel, from, count, fromPullToRefresh);
    }

    private Observable<PagedListResult<ContactRegistryModel>> loadContacts(@Nullable ContactRegistryModel contactRegistryModel, int from, int count, boolean reload) {
        return mInteractor.searchContacts(
                mSearchQuery,
                convertToControllerUuid(mSelectedFolderUuid),
                contactRegistryModel,
                from,
                count,
                getCurrentOrder(),
                reload
        );
    }

    @Override
    protected boolean hasPartialPage(int currentDataListSize) {
        int dataListSize = currentDataListSize;
        List<ContactRegistryModel> dataList = getDataList();
        if (dataListSize > 0 && !dataList.isEmpty() && dataList.get(0) instanceof ContactFoldersModel) {
            dataListSize--;
        }
        return super.hasPartialPage(dataListSize);
    }

    @Override
    protected void processUpdatingDataListResultWithoutViewUpdating(@NonNull PagedListResult<ContactRegistryModel> pagedListResult, boolean updatingFromTail, ErrorCode errorCode) {
        super.processUpdatingDataListResultWithoutViewUpdating(pagedListResult, updatingFromTail, errorCode);

        mHasNewerPage = mDataListOffset > 0;
        mHasOlderPage = pagedListResult.hasMore();
        mShowOlderProgress = !pagedListResult.isFullyCached() || mHasOlderPage;
        loadPagesIfNeeded();
    }
    //endregion

    // region Displaying data
    @Override
    protected void resetUI() {
        super.resetUI();
        if (!mCheckHelper.isCheckModeEnabled()) mScrollHelper.resetState();
    }

    @Override
    protected boolean isNeedToDisplayViewState() {
        return true;
    }

    @Override
    protected void displayViewState(@NonNull ContactListContract.View view) {
        view.enableHeaders(getHeadersEnabled());
        view.updateDataList(mContactList, mDataListOffset);
    }

    private void notifyItemRemoved(@NonNull UUID uuid, int index) {
        if (mView != null) {
            mView.enableHeaders(getHeadersEnabled());
            mView.notifyContactRemoved(uuid, index + mDataListOffset);
        }
    }
    // endregion

    // region Rx Callback overrides
    @Override
    protected void processLoadingNextPageError(@NonNull Throwable throwable) {
        processThrowable(throwable);
    }

    @Override
    protected void processUpdatingDataListError(@NonNull Throwable throwable) {
        processThrowable(throwable);
    }

    @Override
    protected int getEmptyViewErrorId() {
        // заглушки контролируются контроллером
        // необходимо вернуть хоть что-то
        return 0;
    }

    private void processThrowable(@NonNull Throwable throwable) {
        if (mView != null) {
            mView.showDefaultLoadingError();
        }
        Timber.d(throwable, "Error on getting contacts from controller");
    }

    @Override
    protected void finalProcessOlderPageLoading() {
        super.finalProcessOlderPageLoading();
        if (mView != null) {
            mView.hideLoading();
        }
    }
    // endregion

    // region View Callbacks
    @Override
    public void onAddContactBtnClick() {
        if (mView != null) {
            if (mCanAddNewContacts) {
                DebounceActionHandler.Companion.getINSTANCE().handle(() -> {
                    mView.showAddContactPane(mAddContactOptions);
                    return Unit.INSTANCE;
                });
            } else {
                mView.showAddNewContactsDisabledMessage(ru.tensor.sbis.communicator.design.R.string.communicator_add_new_contacts_disabled_message);
            }
        }
    }

    @Override
    public void onRequestAddContactOptionResult(int optionResult) {
        if (mView == null) return;
        switch (mAddContactOptions.get(optionResult)) {
            case IMPORT_CONTACT:
                mView.importContacts();
                sendAnalytics(new ImportContacts(getSimpleNameForAnalytic()));
                break;
            case CONTACT_IN_COMPANY:
                mView.openAddInternalEmployeesScreen(UUIDUtils.fromString(mSelectedFolderUuid));
                mView.clearSearchQuery();
                mView.resetUiState();
                sendAnalytics(new OpenContactsInCompany(getSimpleNameForAnalytic()));
                break;
            case NEW_CONTACT:
                mView.openFindContactsScreen(UUIDUtils.fromString(mSelectedFolderUuid));
                mView.clearSearchQuery();
                mView.resetUiState();
                sendAnalytics(new OpenFindNewContacts(getSimpleNameForAnalytic()));
                break;
        }
    }

    @Override
    public void onDismissOrDeleteContactClick(@NonNull UUID uuid) {
        ContactsModel contact = null;
        for (ContactRegistryModel registryModel : getDataList()) {
            if (registryModel instanceof ContactsModel && UUIDUtils.equals(((ContactsModel) registryModel).getContact().getUuid(), uuid)) {
                contact = (ContactsModel) registryModel;
                break;
            }
        }
        if (contact != null) deleteSingleContact(contact);
    }

    @Override
    public void onDismissedWithoutMessage(String uuid) {
        mLastSwipedDismissedUuid = UUID.fromString(uuid);
    }

    private void completeContactDismissing() {
        onDismissOrDeleteContactClick(mLastSwipedDismissedUuid);
        if (mView != null) mView.clearSwipeMenuState();
    }

    @Override
    public void onDeleteCheckedClicked() {
        deleteContacts(mCheckHelper.getChecked());
        hideCheckModeInternal();
    }

    @Override
    public void onMoveBySwipeClicked(@NonNull ContactsModel contact) {
        mLastClickedSwipedContact = contact;
        if (mView != null) {
            UUID folderUuid = contact.getContact().getFolderUuid();
            // По умолчанию контакт находится в корневой папке
            if (folderUuid == null || !mIsFoldersEnabled) folderUuid = ROOT_FOLDER_UUID;
            mView.showFolderSelection(folderUuid);
        }
    }

    @Override
    public void onSendMessageClicked(@NonNull ContactsModel contact) {
        if (mView != null) {
            mView.showChat(contact.getContact().getUuid());
        }
    }

    @Override
    public void onMoveCheckedClicked() {
        if (mView != null) {
            mView.showFolderSelection(null);
        }
        sendAnalytics(new MoveContactsToFolders(getSimpleNameForAnalytic()));
    }

    @Override
    public void onBlockCheckedClicked() {
        if (mView != null) {
            mView.showBlockContactsDialog(
                    ru.tensor.sbis.communicator.design.R.plurals.communicator_block_contacts_message,
                    mCheckHelper.getCheckedCount()
            );
        }
    }

    @Override
    public void onBlockContactsGranted() {
        blockContacts(mCheckHelper.getChecked());
        hideCheckModeInternal();
    }

    // endregion

    // region Business logic properties
    private boolean isCorporateAccount() {
        final UserAccount currentAccount = mLoginInterface.getCurrentAccount();
        return currentAccount == null || !currentAccount.isPhysic();
    }
    // endregion

    @Override
    public boolean onBackButtonClicked() {
        if (mCheckHelper.isCheckModeEnabled()) {
            // hide check mode
            hideCheckModeInternal();
            return true;
        } else if (mSelectedFolderUuid.equals(mRootFolderUuid)) {
            // finish activity if it's root folder
            return false;
        } else {
            //go to root folder
            onRootFolderSelected();
            return true;
        }
    }

    @Override
    public void onCheckModeCancelClicked() {
        hideCheckModeInternal();
    }

    @Override
    public void onBranchTypeTabClick(String navxId) {
        if (Objects.equals(navxId, NavxId.STAFF.getId())) {
            if (mView != null) {
                completeContactDismissing();
                mInteractor.cancelContactsControllerSynchronizations();
                mView.openEmployees();
            }
        }
    }

    @Override
    public void onNavigationDrawerStateChanged() {
        if (mView != null) {
            mView.closeAllOpenSwipeMenus();
        }
    }

    @Override
    public void onFilterClicked() {
        if (mView != null) {
            DebounceActionHandler.Companion.getINSTANCE().handle(() -> {
                mView.showFilterSelection(new ContactFilterConfiguration(mOrderProvider.get(), mSelectedFolder));
                return Unit.INSTANCE;
            });
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Single<Pair<Boolean, Boolean>> isCanImportContacts() {
        boolean redButtonProviderIsNotNull = mRedButtonActivatedProvider != null;
        if (redButtonProviderIsNotNull) {
            return mRedButtonActivatedProvider.isRedButtonActivated().map(isActivated -> new Pair<>(redButtonProviderIsNotNull, !isActivated));
        } else {
            return Single.just(new Pair<>(redButtonProviderIsNotNull, true));
        }
    }

    @Override
    public void onPhoneVerificationRequired() {
        if (mView != null) {
            mView.showPhoneVerification();
        }
    }

    @Override
    public void resetSelection() {
        mSelectionHelper.resetSelection();
    }

    @Override
    public void onViewVisibilityChanged(boolean isInvisible) {
        isViewHidden = isInvisible;
        if (isInvisible) {
            needToRespondToEvents = false;
        } else {
            needToRespondToEvents = true;
            if (mView != null) {
                mView.showStub(mContactsStubHelper.getCurrentStub());
                updateDataList(true);
            }
            tryAutoImportContacts();
        }
    }

    @Override
    protected boolean isNeedToRestoreScrollPosition() {
        return false;
    }

    @Override
    public void sendAnalyticOpenedContactsFolders() {
        sendAnalytics(new OpenedFoldersContacts(getSimpleNameForAnalytic()));
    }

    @Override
    public void setEmployeesTabNavIxIsGranted(boolean isGranted) {
        lastCheckEmployeesTabIsGranted = isGranted;
    }

    @Override
    public boolean employeesTabNavXIsGranted() {
        return lastCheckEmployeesTabIsGranted;
    }

    private void hideCheckModeInternal() {
        mCheckHelper.disableCheckMode();
        if (mView != null) {
            mView.setFabVisible(!isUsefulFolder());
        }
        mScrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE);
    }

    // region Business Logic Actions
    private void deleteSingleContact(@NonNull ContactsModel contact) {
        UUID uuid = contact.getContact().getUuid();
        mDeleteContactsDisposable.add(
            mInteractor.deleteContacts(
                Collections.singletonList(uuid),
                convertToControllerUuid(mSelectedFolderUuid)
            ).subscribe(
                status -> {
                    if (mView != null) {
                        switch (status.getErrorCode()) {
                            case SUCCESS:
                                int removedIndex = removeSingle(contact);
                                if (removedIndex >= 0) {
                                    notifyItemRemoved(uuid, removedIndex);
                                }
                                updateStubItemInList(true);
                                hideOrShowSearchPanelIfNeeded(mContactList);
                                showEmptyViewIfNeeded(mView, mContactList, getEmptyViewErrorId());
                                resetSelectionIfNeeded(Collections.singletonList(uuid));
                                break;
                            case NETWORK_ERROR:
                                mView.showErrorPopup(ru.tensor.sbis.communicator.design.R.string.communicator_contact_action_network_error, getIcon(SbisMobileIcon.Icon.smi_WiFiNone.getCharacter()));
                                mView.cancelDismiss(uuid);
                                break;
                            default:
                                mView.showErrorPopup(ru.tensor.sbis.communicator.design.R.string.communicator_contact_delete_error, getIcon(SbisMobileIcon.Icon.smi_AlertNull.getCharacter()));
                                mView.cancelDismiss(uuid);
                                break;
                        }
                    }
                },
                throwable -> {
                    if (mView != null) {
                        mView.showErrorPopup(ru.tensor.sbis.communicator.design.R.string.communicator_contact_delete_error,getIcon(SbisMobileIcon.Icon.smi_AlertNull.getCharacter()));
                        mView.cancelDismiss(uuid);
                    }
                }
            )
        );
    }

    private void deleteContacts(@NonNull List<ContactRegistryModel> contacts) {
        List<UUID> uuids = retrieveUuidList(contacts);
        mDeleteContactsDisposable.add(
                mInteractor.deleteContacts(uuids, convertToControllerUuid(mSelectedFolderUuid))
                        .subscribe(
                                status -> {
                                    if (mView != null) {
                                        switch (status.getErrorCode()) {
                                            case SUCCESS:
                                                applyRemoves(contacts);
                                                displayViewState(mView);
                                                updateStubItemInList(true);
                                                hideOrShowSearchPanelIfNeeded(mContactList);
                                                showEmptyViewIfNeeded(mView, mContactList, getEmptyViewErrorId());
                                                resetSelectionIfNeeded(uuids);
                                                break;
                                            case NETWORK_ERROR:
                                                mView.showErrorPopup(ru.tensor.sbis.communicator.design.R.string.communicator_contact_action_network_error, getIcon(SbisMobileIcon.Icon.smi_WiFiNone.getCharacter()));
                                                break;
                                            default:
                                                mView.showErrorPopup(ru.tensor.sbis.communicator.design.R.string.communicator_contact_delete_error, getIcon(SbisMobileIcon.Icon.smi_AlertNull.getCharacter()));
                                                break;
                                        }
                                    }
                                },
                                throwable -> {
                                    if (mView != null) {
                                        mView.showErrorPopup(ru.tensor.sbis.communicator.design.R.string.communicator_contact_delete_error, getIcon(SbisMobileIcon.Icon.smi_AlertNull.getCharacter()));
                                    }
                                }
                        )
        );
    }

    private void moveContacts(List<ContactRegistryModel> contacts, UUID targetFolderUuid) {
        List<UUID> uuids = retrieveUuidList(contacts);
        mMoveContactDisposable.set(
                mInteractor.moveContacts(uuids, convertToControllerUuid(targetFolderUuid.toString()), convertToControllerUuid(mSelectedFolderUuid))
                        .subscribe(status -> {
                            if (mView != null) {
                                switch (status.getErrorCode()) {
                                    case SUCCESS:
                                        // Вычищаем перемещенные контакты, если находимся не в корневой папке
                                        if (!mSelectedFolderUuid.equals(mRootFolderUuid)) {
                                            applyRemoves(contacts);
                                            updateStubItemInList(false);
                                            displayViewState(mView);
                                            showEmptyViewIfNeeded(mView, mContactList, getEmptyViewErrorId());
                                        }
                                        mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_move_success_message,
                                                getIcon(SbisMobileIcon.Icon.smi_Successful.getCharacter()), uuids.size(), SbisPopupNotificationStyle.SUCCESS);
                                        break;
                                    case CONTACT_ALREADY_IN_FOLDER:
                                        mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_want_add_in_same_folder,
                                                getIcon(SbisMobileIcon.Icon.smi_information.getCharacter()), uuids.size(), SbisPopupNotificationStyle.INFORMATION);
                                        break;
                                    case NETWORK_ERROR:
                                        mView.showErrorPopup(ru.tensor.sbis.communicator.design.R.string.communicator_contact_action_network_error, getIcon(SbisMobileIcon.Icon.smi_WiFiNone.getCharacter()));
                                        break;
                                    default:
                                        mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_move_error,
                                                getIcon(SbisMobileIcon.Icon.smi_AlertNull.getCharacter()), uuids.size(), SbisPopupNotificationStyle.ERROR);
                                        break;
                                }
                            }
                        }, throwable -> {
                            if (mView != null) {
                                mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_move_error,
                                        getIcon(SbisMobileIcon.Icon.smi_AlertNull.getCharacter()), uuids.size(), SbisPopupNotificationStyle.ERROR);
                            }
                        })
        );
    }

    private void moveContact(ContactsModel contact, UUID targetFolderUuid) {
        mMoveContactDisposable.set(
                mInteractor.moveContact(
                        contact.getContact().getUuid(),
                        convertToControllerUuid(targetFolderUuid.toString()),
                        convertToControllerUuid(mSelectedFolderUuid)
                ).subscribe(status -> {
                    if (mView != null) {
                        switch (status.getErrorCode()) {
                            case SUCCESS:
                                // Вычищаем перемещенный контакт, если находимся не в корневой папке
                                if (!mSelectedFolderUuid.equals(mRootFolderUuid)) {
                                    applyRemoves(Collections.singletonList(contact));
                                    updateStubItemInList(false);
                                    displayViewState(mView);
                                    showEmptyViewIfNeeded(mView, mContactList, getEmptyViewErrorId());
                                }
                                sendAnalytics(new MoveContactsToFolders(getSimpleNameForAnalytic()));
                                mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_move_success_message, null,
                                        1, SbisPopupNotificationStyle.SUCCESS);
                                break;
                            case CONTACT_ALREADY_IN_FOLDER:
                                mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_want_add_in_same_folder,
                                        getIcon(SbisMobileIcon.Icon.smi_information.getCharacter()), 1, SbisPopupNotificationStyle.INFORMATION);
                                break;
                            case NETWORK_ERROR:
                                mView.showErrorPopup(ru.tensor.sbis.communicator.design.R.string.communicator_contact_action_network_error, getIcon(SbisMobileIcon.Icon.smi_WiFiNone.getCharacter()));
                                break;
                            case OTHER_ERROR:
                                String errorMessage = status.getErrorMessage();
                                int startIndex = errorMessage.indexOf("Данный контакт уже находится в папке");
                                if (startIndex != -1) {
                                    mView.showContactInAnotherFolderAlready(errorMessage.substring(startIndex));
                                } else {
                                    mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_move_error,
                                            getIcon(SbisMobileIcon.Icon.smi_AlertNull.getCharacter()), 1, SbisPopupNotificationStyle.ERROR);
                                }
                                break;
                            default:
                                mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_move_error,
                                        getIcon(SbisMobileIcon.Icon.smi_AlertNull.getCharacter()), 1, SbisPopupNotificationStyle.ERROR);
                                break;
                        }
                    }
                }, throwable -> {
                    if (mView != null) {
                        mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_move_error,
                                getIcon(SbisMobileIcon.Icon.smi_AlertNull.getCharacter()), 1, SbisPopupNotificationStyle.ERROR);
                    }
                })
        );
    }

    private void blockContacts(List<ContactRegistryModel> contacts) {
        List<UUID> uuids = retrieveUuidList(contacts);
        mSingleDisposables.add(
            mInteractor.blockContacts(uuids)
                    .subscribe(status -> {
                                if (mView != null) {
                                    switch (status.getErrorCode()) {
                                        case SUCCESS:
                                            // Вычищаем перемещенные контакты, если находимся не в корневой папке
                                            if (!mSelectedFolderUuid.equals(mRootFolderUuid)) {
                                                applyRemoves(contacts);
                                                updateStubItemInList(false);
                                                displayViewState(mView);
                                                showEmptyViewIfNeeded(mView, mContactList, getEmptyViewErrorId());
                                            }
                                            mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_black_list_success_message, null,
                                                    uuids.size(), SbisPopupNotificationStyle.SUCCESS);
                                            break;
                                        case CONTACT_ALREADY_IN_FOLDER:
                                            mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_black_list_add_in_same_folder,
                                                    getIcon(SbisMobileIcon.Icon.smi_information.getCharacter()), 1, SbisPopupNotificationStyle.INFORMATION);
                                            break;
                                        case NETWORK_ERROR:
                                            mView.showErrorPopup(ru.tensor.sbis.communicator.design.R.string.communicator_contact_action_network_error, getIcon(SbisMobileIcon.Icon.smi_WiFiNone.getCharacter()));
                                            break;
                                        default:
                                            mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_black_list_error,
                                                    getIcon(SbisMobileIcon.Icon.smi_AlertNull.getCharacter()), uuids.size(), SbisPopupNotificationStyle.ERROR);
                                            break;
                                    }
                                }
                            }, throwable -> {
                                if (mView != null) {
                                    mView.showPopupWithPlurals(ru.tensor.sbis.communicator.design.R.plurals.communicator_contact_black_list_error,
                                            getIcon(SbisMobileIcon.Icon.smi_AlertNull.getCharacter()), uuids.size(), SbisPopupNotificationStyle.ERROR);
                                }
                            }
                    )
        );
    }

    private void applyRemoves(@NonNull List<ContactRegistryModel> listToRemove) {
        final List<ContactRegistryModel> modelList = new ArrayList<>(getDataList());
        int modelIndex;
        for (ContactRegistryModel model : listToRemove) {
            modelIndex = modelList.indexOf(model);
            if (modelIndex != -1) {
                modelList.remove(modelIndex);
            }
        }
        swapDataList(modelList);
    }

    private int removeSingle(@NonNull ContactRegistryModel model) {
        final List<ContactRegistryModel> modelList = getDataList();

        int index;
        index = modelList.indexOf(model);
        if (index >= 0) {
            modelList.remove(index);
        }

        return index;
    }
    // endregion

    // region Utilities
    @NonNull
    private static List<UUID> retrieveUuidList(@Nullable List<ContactRegistryModel> checkListItems) {
        if (checkListItems == null) {
            return new ArrayList<>(0);
        }
        int size = checkListItems.size();
        List<UUID> positions = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            if (checkListItems.get(i) != null) {
                positions.add(((ContactsModel) checkListItems.get(i)).getContact().getUuid());
            }
        }
        return positions;
    }
    //endregion

    private void resetSelectionIfNeeded(@NonNull List<UUID> listToCheck) {
        if (listToCheck.contains(((ContactsModel) mSelectionHelper.getSelectedItem()).getContact().getUuid())) {
            resetSelection();
        }
    }

    @Nullable
    private UUID convertToControllerUuid(@NonNull String id) {
        UUID uuid = UUIDUtils.fromString(id);
        return !UUIDUtils.isNilUuid(uuid) ? uuid : null;
    }

    private void updateFilterString() {
        if (mView != null) {
            EntitledItem itemToDisplay;
            itemToDisplay = mOrderProvider.get();
            mView.changeFilterByType(itemToDisplay);
        }
    }

    private void startFirstLoadMetric(boolean isCloudCall) {
        if (isFirstDataLoading && isCloudCall) {
            MetricsDispatcher.INSTANCE.startTrace(MetricsType.FIREBASE_CONTACT_LIST_FIRST_PAGE_LOADING);
        }
    }

    private void stopFirstLoadMetric() {
        if (isFirstDataLoading && getDataList().isEmpty()) {
            MetricsDispatcher.INSTANCE.stopTrace(MetricsType.FIREBASE_CONTACT_LIST_FIRST_PAGE_LOADING);
            isFirstDataLoading = false;
        }
    }

    private String getIcon(char path) {
        return String.valueOf(path);
    }

    @Override
    public void additionalCommandTitleClicked() {}

    @Override
    public void additionalCommandIconClicked() {}

    @NonNull
    private String getSimpleNameForAnalytic() {
        return ContactListPresenterImpl.class.getSimpleName();
    }
}
