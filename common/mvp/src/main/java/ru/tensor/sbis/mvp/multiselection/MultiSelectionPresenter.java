package ru.tensor.sbis.mvp.multiselection;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.SerialDisposable;
import kotlin.Pair;
import ru.tensor.sbis.common.R;
import ru.tensor.sbis.common.generated.ErrorCode;
import ru.tensor.sbis.common.generated.EventType;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.common.util.scroll.ScrollHelper;
import ru.tensor.sbis.mvp.data.model.PagedListResult;
import ru.tensor.sbis.mvp.multiselection.data.BaseFilterKeys;
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;
import ru.tensor.sbis.mvp.multiselection.data.SelectionFilter;
import ru.tensor.sbis.mvp.search.BaseSearchablePresenter;
import timber.log.Timber;

/**
 * Base presenter for item multi selection.
 * Can build a list optionally with or without suggest, with or without pagination.
 *
 * @param <INTERACTOR>     interactor
 * @param <RESULT_MANAGER> manager for transferring result of selection
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"deprecation", "FieldMayBeFinal", "CanBeFinal", "rawtypes", "unused", "RedundantSuppression"})
public abstract class MultiSelectionPresenter<INTERACTOR extends MultiSelectionInteractor, RESULT_MANAGER extends MultiSelectionResultManager>
        extends BaseSearchablePresenter<MultiSelectionContract.View, MultiSelectionItem, Pair<EventType, HashMap<String, String>>>
        implements MultiSelectionContract.Presenter {

    private static final int INITIAL_PROGRESS_DISPLAY_DELAY_SEC = 1;

    /**
     * Flag describing how the interactor works.
     * If true, the presenter loads data with one-sided pagination.
     * If false, the presenter will load all the elements at once.
     */
    protected boolean mWithPagination = true;

    protected static final int DEFAULT_SUGGEST_SIZE = 20;

    //region constants for loading all the elements at once
    private static final int FULL_SUGGEST_SIZE = 320;
    private static final int FULL_SEARCH_SIZE = 500;
    //endregion

    protected final INTERACTOR mInteractor;
    protected final RESULT_MANAGER mSelectionResultManager;

    //region cache
    @NonNull
    protected Set<MultiSelectionItem> mItems = new LinkedHashSet<>();
    @NonNull
    protected Set<MultiSelectionItem> mCheckedItems = new LinkedHashSet<>();
    @NonNull
    protected Set<MultiSelectionItem> mAlreadyCheckedItems = new LinkedHashSet<>();

    /**
     * List of items sorted by design logic.
     */
    @NonNull
    protected List<MultiSelectionItem> mDisplayedCollection = new CopyOnWriteArrayList<>();
    //endregion

    protected boolean mCanResultBeEmpty;
    private boolean mIsSingleChoice;
    private int mResultMaxCount;

    //pagination logic, position from with we should get older page
    private int mLastLoadedItemPosition;

    //region business logic flags
    protected boolean mIsItemsLoading = true;
    protected boolean mIsSelectionBlockExpanded;
    protected boolean mNeedToSort;
    private boolean mIsFirstTimeClicked = true;
    private boolean mIsSearchQueryEmpty;
    /**
     * flag for indicate that there was checked items at the moment of screen was opened
     */
    protected boolean mHasStartItemsCollection;
    private boolean mShowNotFoundMessage;

    //endregion

    private SerialDisposable delayedProgressDisposable = new SerialDisposable();

    public MultiSelectionPresenter(int resultMaxCount,
                                   boolean isSingleChoice,
                                   boolean canResultBeEmpty,
                                   @NonNull NetworkUtils networkUtils,
                                   @NonNull ScrollHelper scrollHelper,
                                   @NonNull INTERACTOR interactor,
                                   @NonNull RESULT_MANAGER selectionResultManager) {
        super(null, networkUtils, scrollHelper);
        mInteractor = interactor;
        mSelectionResultManager = selectionResultManager;
        mResultMaxCount = resultMaxCount;
        mCanResultBeEmpty = canResultBeEmpty;
        mIsSingleChoice = isSingleChoice;
        mIsSelectionBlockExpanded = false;
        mNeedToSort = true;
        initStartSelectedItemsList();
        mHasStartItemsCollection = !mCheckedItems.isEmpty();
        updateDoneButtonVisibility();
    }

    /**
     * Method for fill in the {@link #mCheckedItems} and {@link #mDisplayedCollection} lists with start collection from your implementation of {@link MultiSelectionResultManager}.
     * Set {@link #mHasStartItemsCollection} in true if start collection not empty.
     */
    protected abstract void initStartSelectedItemsList();

    /**
     * Describes how the result should be handled. Depends on your implementation of of {@link MultiSelectionResultManager}.
     *
     * @param isSuccess     result state
     * @param selectedItems result list
     */
    protected abstract void putResultInSelectionResultManager(boolean isSuccess, List<MultiSelectionItem> selectedItems);

    //region customize messages for your screen states.

    /**
     * If user can select only one item on your screen, set resId for restriction message.
     * Else put 0.
     *
     * @return resId for message (singular)
     */
    @StringRes
    protected abstract int getSingleCountRestriction();

    /**
     * If user can select limited count of items on your screen, set resId for restriction message.
     * Else put 0.
     *
     * @return resId for message (plural)
     */
    @StringRes
    protected abstract int getMaxCountRestriction();

    /**
     * Set message for empty view, if items was not found.
     *
     * @return resId for message
     */
    @StringRes
    protected abstract int getItemsNotFoundMessage();

    /**
     * Set message for empty view, if all items was selected.
     *
     * @return resId for message
     */
    @StringRes
    protected abstract int getAllItemsWasSelectedMessage();
    //endregion

    @Override
    public void attachView(@NonNull MultiSelectionContract.View view) {
        super.attachView(view);
        mView.setRollUpArrowEnabled(mSearchQuery.isEmpty() && mIsSelectionBlockExpanded && !mCheckedItems.isEmpty());
        mView.changeArrowButtonsVisibility(mIsSelectionBlockExpanded);
        updateDoneButtonVisibility();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        delayedProgressDisposable.dispose();
    }

    protected final void setInteractorWorksWithPagination(boolean withPagination) {
        mWithPagination = withPagination;
    }

    /**
     * Override this method if by design there is should be limited suggest at your screen if search query is empty
     * (not all items, but a limited number of relevant items).
     *
     * @return int suggest limit
     */
    @SuppressWarnings("SameReturnValue")
    protected int getSuggestSize() {
        return Integer.MAX_VALUE;
    }
    //region Displaying Data

    @Override
    protected boolean isNeedToDisplayViewState() {
        return true;
    }

    @Override
    protected void displayViewState(@NonNull MultiSelectionContract.View view) {
        if (mIsItemsLoading) {
            showProgressDelayed();
        } else {
            hideProgress();
        }
        view.setCheckedItems(mCheckedItems);
        view.updateDataList(configureAndGetDisplayList(), mDataListOffset);
    }

    //endregion

    @Override
    public void onSearchQueryChanged(@NonNull String searchQuery) {
        if (!isInteractiveSearchMode()) {
            boolean isEmpty = searchQuery.isEmpty();
            if (mIsSearchQueryEmpty != isEmpty) {
                mIsSearchQueryEmpty = isEmpty;
                if (mView != null) {
                    mView.setRollUpArrowEnabled(isEmpty);
                }
                if (!mIsSearchQueryEmpty) {
                    mIsSelectionBlockExpanded = true;
                    if (mView != null) {
                        mView.setRollUpArrowEnabled(false);
                    }
                } else {
                    collapseToggle(false);
                }
            }
        }
        super.onSearchQueryChanged(searchQuery);
    }

    @Override
    protected void makeSearchRequest() {
        super.makeSearchRequest();
        mIsItemsLoading = true;
        showProgressIfNeeded();
        mLoadingPageSubscription.set(
                mInteractor.searchItems(
                                createFilter()
                                        .with(BaseFilterKeys.ITEMS_COUNT, getPageSize())
                                        .with(BaseFilterKeys.FROM_PULL_TO_REFRESH, true))
                        .doAfterTerminate(() -> {
                            mIsItemsLoading = false;
                            if (mView != null) {
                                mView.scrollToStart();
                            }
                        })
                        .doOnNext(this::doOnNext)
                        .subscribe(this::onItemsLoaded, this::onItemsLoadingError));
    }

    //region MultiSelectionContract.Presenter
    @SuppressWarnings("WeakerAccess")
    protected void onItemsLoaded(@NonNull PagedListResult<MultiSelectionItem> pagedListResult) {
        mIsItemsLoading = false;
        hideProgress();
        processUpdatingDataListResult(pagedListResult, false);
    }

    @SuppressWarnings("all")
    protected void onItemsLoadingError(@NonNull Throwable throwable) {
        mIsItemsLoading = false;
        hideProgress();
        mItems.clear();

        showErrorMessageInEmptyView(throwable.getMessage());
        Timber.e(throwable);
    }

    private void showErrorMessageInEmptyView(@Nullable String message) {
        if (mView == null) {
            setMissedErrorResId(R.string.common_update_error);
            return;
        }
        if (message != null && !message.isEmpty()) {
            mView.showMessageInEmptyView(message);
        } else {
            mView.showMessageInEmptyView(R.string.common_update_error);
        }
    }

    protected void showNotFoundMessage() {
        mItems.clear();
        if (mView != null) {
            mView.updateDataList(new ArrayList<>(mItems), mDataListOffset, mIsSelectionBlockExpanded);
            mView.setRollUpArrowEnabled(false);
            if (isCrudSupported() && !mShowNotFoundMessage) {
                return;
            }
            mView.showMessageInEmptyView(getItemsNotFoundMessage());
        }
    }

    private void showProgressDelayed() {
        hideProgress();
        delayedProgressDisposable.set(
                Observable.timer(INITIAL_PROGRESS_DISPLAY_DELAY_SEC, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(it -> showProgressIfNeeded())
        );
    }

    private void showProgressIfNeeded() {
        if (mView != null && mIsItemsLoading) {
            mView.showLoading();
        }
    }

    protected void hideProgress() {
        if (mView != null) {
            mView.hideLoading();
            mShowLoadingProcess = false;
        }
    }

    @Override
    public void onItemClicked(@NonNull MultiSelectionItem item, int position, boolean isClickedOnItem) {
        boolean alreadyChecked = mCheckedItems.contains(item);
        if (mIsSingleChoice || (mIsFirstTimeClicked && isClickedOnItem && mCheckedItems.isEmpty())) {
            singleSelection(item, position, alreadyChecked);
        } else {
            if (needCleanLastSelection(isClickedOnItem, alreadyChecked)) {
                mCheckedItems.clear();
                makeSearchRequest();
                if (mView != null) {
                    mView.updateDataList(configureAndGetDisplayList(), mDataListOffset);
                    mView.setCheckedItems(mCheckedItems);
                }
            }
            mIsFirstTimeClicked = false;
            multipleSelection(item, position, alreadyChecked);
        }
    }

    protected boolean needCleanLastSelection(boolean isClickedOnItem, boolean alreadyChecked) {
        return mHasStartItemsCollection && mIsFirstTimeClicked && !alreadyChecked && isClickedOnItem;
    }

    protected void singleSelection(@NonNull MultiSelectionItem item, int position, boolean alreadyChecked) {
        //finish selection if clicked on item first time (without start collection of checked items), continue selection if clicked on checkbox in this case
        if (alreadyChecked) {
            removeItemFromCheckedList(item);
            if (mView != null) {
                mView.onItemCheckedStateChanged(item, position, false, true);
                updateDoneButtonVisibility();
            }
        } else {
            if ((mHasStartItemsCollection || mIsSingleChoice) && mIsFirstTimeClicked && mView != null) {
                mCheckedItems.clear();
                mView.updateDataListWithoutNotification(configureAndGetDisplayList(), mDataListOffset);
                mView.setCheckedItems(mCheckedItems);
            }
            if (isResultMaxCountExceeded(item.getItemCount())) {
                showRestrictionMessage();
            } else {
                mCheckedItems.add(item);
                if (mIsSingleChoice && mView != null) {
                    mView.setRollUpArrowEnabled(true);
                    expandToggle();
                    mView.setCheckedItems(mCheckedItems);
                    mView.notifyItemsChanged(mDisplayedCollection.indexOf(item), 1);
                    updateDoneButtonVisibility();
                }
                finishSelection(true);
            }
        }
    }
    //so complex logic is due to the design of the screen

    protected void multipleSelection(@NonNull MultiSelectionItem item, int position,
                                     boolean alreadyChecked) {
        //when select new item, check if max count exceeded straightway
        if (isResultMaxCountExceeded(item.getItemCount())) {
            showRestrictionMessage();
            return;
        }
        if (alreadyChecked) {
            mCheckedItems.remove(item);
            if (!shouldSaveUncheckedItemPosition()) {
                addFirstAlreadyCheckedItem(item);
                if (mView != null) {
                    mView.onItemCheckedStateChanged(item, position, false, false);
                }
            }
            //remove that item from displayed collection
            if (isInteractiveSearchMode()) {
                mDisplayedCollection.remove(item);
            }
            if (mView != null) {
                mView.removeItemFromSelectedPanel(item);
            }
            if (mSearchQuery.isEmpty() || isInteractiveSearchMode()) {
                if (shouldSaveUncheckedItemPosition()) {
                    updateDataList(true);
                } else if (mView != null) {
                    mView.updateDataList(configureAndGetDisplayList(), mDataListOffset, mIsSelectionBlockExpanded);
                }
                if (mView != null) {
                    mView.setDoneButtonClickable(false);
                }
            }
        } else {
            //check item case
            mCheckedItems.add(item);
            if (!shouldSaveUncheckedItemPosition()) {
                mAlreadyCheckedItems.remove(item);
            }

            if (mSearchQuery.isEmpty() || isInteractiveSearchMode()) {
                //when check item non at selection block, needed to hide it and all checked, close toggle
                stashJustCheckedNewItem(item, position);
                if (mWithPagination && mHasOlderPage && mItems.size() - mCheckedItems.size() <= getSuggestSize()) {
                    loadOlderPage();
                }
            } else if (mView != null && !isInteractiveSearchMode()) {
                mView.addItemToSelectedPanel(item);
            }
        }

        if (mView != null) {
            if (!mSearchQuery.isEmpty() && !isInteractiveSearchMode()) {
                mView.clearSearchQuery();
                mView.hideKeyboard();
            }
            updateDoneButtonVisibility();
        }
    }

    /**
     * Признак активного режима отображения, при котором выбранные элементы списка не исчезают
     * с экрана при вводе поискового запроса, а их выбор не очищает поле ввода поиска.
     *
     * @return true - режим активен.
     */
    @SuppressWarnings("SameReturnValue")
    protected boolean isInteractiveSearchMode() {
        return false;
    }

    /**
     * В некоторых случаях для ускорения обновления списка не нужно сохранять оригинальную позицию элемента, у которого отменен выбор.
     * Если метод возвращает false, то такой элемент будет помещен первым в списке невыбранных элементов.
     * Если true, то список будет обновлен, в результате чего элемент вернется на исходную позицию (на ту позицию, которую он занимал до того, как его выбрали).
     *
     * @return true - позиция элемента, у которого отменен выбор, сохраняется, false - элемент, у которого отменен выбор, будет первым в списке невыбранных.
     */
    @SuppressWarnings("SameReturnValue")
    protected boolean shouldSaveUncheckedItemPosition() {
        return true;
    }

    protected void removeItemFromCheckedList(MultiSelectionItem item) {
        mCheckedItems.remove(item);
        if (shouldSaveUncheckedItemPosition()) {
            updateDataList(true);
        } else {
            addFirstAlreadyCheckedItem(item);
            if (mView != null) {
                mView.updateDataList(configureAndGetDisplayList(), mDataListOffset, mIsSelectionBlockExpanded);
            }
        }
    }

    protected void addFirstAlreadyCheckedItem(MultiSelectionItem item) {
        ArrayList<MultiSelectionItem> list = new ArrayList<>(mAlreadyCheckedItems);
        list.add(0, item);
        mAlreadyCheckedItems.clear();
        mAlreadyCheckedItems.addAll(list);
    }

    protected void stashJustCheckedNewItem(@NonNull MultiSelectionItem item, int position) {
        //stash just checked item, there must be no visible checked items according to ui logic
        mNeedToSort = true;
        if (mView != null) {
            mView.onItemCheckedStateChanged(item, position, true, true);
            mView.updateDataList(configureAndGetDisplayList(), mDataListOffset);
        }
    }

    protected boolean isResultMaxCountExceeded(int addedItemsCount) {
        int currentCount = 0;
        for (MultiSelectionItem item : mCheckedItems) {
            currentCount += item.getItemCount();
        }
        return currentCount + addedItemsCount > mResultMaxCount;
    }

    protected void showRestrictionMessage() {
        if (mView != null) {
            mView.hideKeyboard();
            if (mResultMaxCount == 1) {
                mView.showResultMaxCountRestriction(getSingleCountRestriction(), mResultMaxCount);
            } else {
                mView.showResultMaxCountRestriction(getMaxCountRestriction(), mResultMaxCount);
            }
        }
    }

    @Override
    public void finishSelection(boolean isSuccess) {
        putResultInSelectionResultManager(isSuccess, new ArrayList<>(mCheckedItems));
        if (mView != null) {
            mView.finishSelection();
        }
    }
    //endregion

    //region Pagination
    @Override
    protected boolean isNeedLoadNewerPage(int firstVisibleItemPosition) {
        return false;
    }

    @Override
    protected void resetPagination() {
        mLastLoadedItemPosition = 0;
        List<MultiSelectionItem> dataModelList = getDataList();
        if (!dataModelList.isEmpty()) {
            mFirstVisibleItem = 0;
            mLastVisibleItem = 0;
            mDataListOffset = 0;
            mHasNewerPage = false;
            mHasOlderPage = false;
            mShowOlderProgress = false;
        }
    }

    @Override
    protected void processLoadingOlderPageResult(@NonNull PagedListResult<MultiSelectionItem> pagedListResult) {
        final List<MultiSelectionItem> dataList = pagedListResult.getDataList();
        int insertedFrom = getDataList().size() + mDataListOffset;
        int insertCount = dataList.size();

        mHasOlderPage = pagedListResult.hasMore();
        mShowOlderProgress = !pagedListResult.isFullyCached() || mHasOlderPage;
        mItems.addAll(dataList);

        if (mView != null) {
            mView.updateDataList(configureAndGetDisplayList(), mDataListOffset);
            mView.showOlderLoadingProgress(mShowOlderProgress);
            if (insertCount > 0) {
                mView.notifyItemsInserted(insertedFrom, insertCount);
            }
        }
    }

    @Override
    protected int getEmptyViewErrorId() {
        return 0;
    }

    @NonNull
    @Override
    protected List<MultiSelectionItem> getDataList() {
        return new ArrayList<>(mItems);
    }

    @Override
    protected int getPageSize() {
        if (mWithPagination) {
            return super.getPageSize();
        } else {
            return mSearchQuery.isEmpty() ? FULL_SUGGEST_SIZE : FULL_SEARCH_SIZE;
        }
    }

    @Override
    protected void swapDataList(@NonNull List<MultiSelectionItem> dataList) {
        mItems = new LinkedHashSet<>(dataList);
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<MultiSelectionItem>> getLoadingNewerDataObservable(@Nullable MultiSelectionItem dataModel, int itemsCount) {
        //stub, there is one-sided pagination
        return mInteractor.searchItems(createFilter());
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<MultiSelectionItem>> getLoadingOlderDataObservable(@Nullable MultiSelectionItem dataModel, int itemsCount) {
        return mInteractor.searchItems(
                        createFilter()
                                .with(BaseFilterKeys.ITEMS_COUNT, itemsCount)
                                .with(BaseFilterKeys.FROM_PULL_TO_REFRESH, true))
                .doOnNext(pagedListResult -> {
                    doOnNext(pagedListResult);
                    if (mView != null && pagedListResult.hasMore()) {
                        mView.updateDataList(configureAndGetDisplayList(), mDataListOffset);
                    }
                });
    }

    @Override
    protected void finalProcessUpdating() {
        super.finalProcessUpdating();
        switchDoneButton();
    }

    @Override
    public void switchDoneButton() {
        if (mView != null) {
            mView.setDoneButtonClickable(isDoneButtonVisible());
        }
    }

    private void increaseLastLoadedItemPosition(int size) {
        mLastLoadedItemPosition += size;
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<MultiSelectionItem>> getUpdatingListByLastEntityObservable(@Nullable MultiSelectionItem dataModel, int itemsCount, boolean fromPullToRefresh) {
        mLastLoadedItemPosition = 0;
        return mInteractor.searchItems(
                        createFilter()
                                .with(BaseFilterKeys.ITEMS_COUNT, getPageSize())
                                .with(BaseFilterKeys.FROM_PULL_TO_REFRESH, fromPullToRefresh))
                .doOnNext(this::doOnNext);
    }
    //endregion

    @Override
    protected void processLoadingNextPageError(@NonNull Throwable error) {
        super.processLoadingNextPageError(error);
        if (mView != null) {
            mView.showMessageInEmptyView(R.string.common_update_error);
        }
    }

    @Override
    protected void processUpdatingDataListResult(@NonNull PagedListResult<MultiSelectionItem> pagedListResult, boolean updatingFromTail) {
        final ErrorCode errorCode = pagedListResult.getCommandStatus() != null ? pagedListResult.getCommandStatus().getErrorCode() : null;
        List<MultiSelectionItem> dataList = pagedListResult.getDataList();

        processUpdatingDataListResultWithoutViewUpdating(pagedListResult, updatingFromTail, errorCode);

        if (mView != null) {
            if (errorCode == ErrorCode.NETWORK_ERROR) {
                showEmptyViewIfNeeded(mView, dataList, getEmptyViewErrorId(), R.string.common_no_network_available_check_connection);
            } else {
                showEmptyViewIfNeeded(mView, dataList, getEmptyViewErrorId());
            }
            if (pagedListResult.getDataList().size() == 0) {
                showNotFoundMessage();
            } else {
                mNeedToSort = true;
                mView.updateDataList(configureAndGetDisplayList(), mDataListOffset, mIsSelectionBlockExpanded);
                mView.showOlderLoadingProgress(mShowOlderProgress);
                mView.showNewerLoadingProgress(mHasNewerPage);
            }
        }
    }

    protected void doOnNext(PagedListResult<MultiSelectionItem> pagedListResult) {
        if (pagedListResult.hasMore()) {
            increaseLastLoadedItemPosition(pagedListResult.getDataList().size());
        }
        if (isCrudSupported()) {
            mShowNotFoundMessage = pagedListResult.getDataList().isEmpty() && !pagedListResult.hasMore() && pagedListResult.isFullyCached();
        }

    }

    @SuppressWarnings("SpellCheckingInspection")
    protected List<UUID> getSelectedItemsUuids() {
        List<UUID> result = new ArrayList<>();
        for (MultiSelectionItem item : mCheckedItems) {
            result.add(item.getUUID());
        }
        return result;
    }

    @Override
    public void onExpandButtonClicked() {
        expandToggle();
    }

    @Override
    public void onRollUpButtonClicked() {
        collapseToggle(true);
    }

    @Override
    public boolean onBackButtonClicked() {
        //ignored by default, override this method as necessary
        return false;
    }

    private void expandToggle() {
        mNeedToSort = true;
        if (!mIsSelectionBlockExpanded) {
            mIsSelectionBlockExpanded = true;
            mView.updateDataList(configureAndGetDisplayList(), mDataListOffset, mIsSelectionBlockExpanded);
            mView.scrollToStart();
        }
    }

    private void collapseToggle(boolean withUpdateView) {
        mNeedToSort = true;
        if (mIsSelectionBlockExpanded) {
            mIsSelectionBlockExpanded = false;
            if (mView != null) {
                if (withUpdateView) {
                    mView.updateDataList(configureAndGetDisplayList(), mDataListOffset, mIsSelectionBlockExpanded);
                }
            }
        }
    }

    protected void updateDoneButtonVisibility() {
        if (mView != null) {
            mView.setDoneButtonVisibility(isDoneButtonVisible());
        }
    }

    private boolean isDoneButtonVisible() {
        return !mCheckedItems.isEmpty() || mCanResultBeEmpty && mHasStartItemsCollection;
    }

    /**
     * We need to store whole item list,
     * either they should be displayed according business logic.
     *
     * @return - bounded to business logic result list of items.
     */
    @NonNull
    protected List<MultiSelectionItem> configureAndGetDisplayList() {
        if (mNeedToSort) {
            mDisplayedCollection.clear();
            ArrayList<MultiSelectionItem> allCheckedItems = new ArrayList<>(mCheckedItems);
            allCheckedItems.addAll(mAlreadyCheckedItems);
            if (mIsSelectionBlockExpanded) {
                if (isInteractiveSearchMode()) {
                    mDisplayedCollection.addAll(mItems);
                    mDisplayedCollection.removeAll(mCheckedItems);
                    subListToSuggestIfNeed();
                    mDisplayedCollection.addAll(0, mCheckedItems);
                } else if (!mSearchQuery.isEmpty()) {
                    List<MultiSelectionItem> retainCheckedList = new ArrayList<>(mItems);
                    retainCheckedList.retainAll(mCheckedItems);
                    for (MultiSelectionItem item : retainCheckedList) {
                        item.setIsChecked(true);
                    }
                    List<MultiSelectionItem> sortedItems = new ArrayList<>(mItems);
                    sortedItems.removeAll(retainCheckedList);
                    sortedItems.addAll(0, retainCheckedList);
                    mItems.clear();
                    mItems.addAll(sortedItems);
                    mDisplayedCollection.addAll(mItems);
                } else {
                    mDisplayedCollection.addAll(mItems);
                    mDisplayedCollection.removeAll(mCheckedItems);
                    subListToSuggestIfNeed();
                    if (shouldSaveUncheckedItemPosition()) {
                        mDisplayedCollection.addAll(0, mCheckedItems);
                    } else {
                        mDisplayedCollection.removeAll(mAlreadyCheckedItems);
                        mDisplayedCollection.addAll(0, allCheckedItems);
                    }
                }
            } else {
                mDisplayedCollection.addAll(mItems);
                if (mSearchQuery.isEmpty()) {
                    if (shouldSaveUncheckedItemPosition()) {
                        mDisplayedCollection.removeAll(mCheckedItems);
                    } else {
                        mDisplayedCollection.removeAll(allCheckedItems);
                        mDisplayedCollection.addAll(0, mAlreadyCheckedItems);
                    }
                    subListToSuggestIfNeed();
                } else {
                    mDisplayedCollection.removeAll(mCheckedItems);
                }
            }
        }
        if (mView != null) {
            if (mDisplayedCollection.isEmpty() && !mIsItemsLoading) {
                mView.showMessageInEmptyView(getAllItemsWasSelectedMessage());
            }
            if (mCheckedItems.isEmpty() && mIsSelectionBlockExpanded) {
                mIsSelectionBlockExpanded = false;
            }
        }

        return new ArrayList<>(mDisplayedCollection);
    }

    protected void subListToSuggestIfNeed() {
        if (mDisplayedCollection.size() > getSuggestSize()) {
            mDisplayedCollection = mDisplayedCollection.subList(0, getSuggestSize());
        }
    }

    @CallSuper
    protected SelectionFilter createFilter() {
        boolean isLastItemExist = !getDataList().isEmpty() && mLastLoadedItemPosition != 0;
        int dataSize = getDataList().size();
        MultiSelectionItem lastItem = null;
        if (isLastItemExist) {
            lastItem = isLastItemExist && dataSize > mLastLoadedItemPosition ?
                    getDataList().get(mLastLoadedItemPosition - 1) :
                    getDataList().get(dataSize - 1);
        }

        return new SelectionFilter(mSearchQuery)
                .with(BaseFilterKeys.FROM_POSITION, mLastLoadedItemPosition)
                .with(BaseFilterKeys.FROM_ITEM, lastItem != null ? lastItem.getUUID() : null);
    }

    /**
     * Поддерживается ли работа с CRUD-фасадом.
     * (пример - обработка показа заглушки при чтении из пустого кеша)
     *
     * @return true - поддерживается
     */
    protected boolean isCrudSupported() {
        return false;
    }

}
