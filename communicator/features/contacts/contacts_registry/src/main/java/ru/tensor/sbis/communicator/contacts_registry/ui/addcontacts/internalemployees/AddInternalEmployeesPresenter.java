package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import kotlin.Pair;
import ru.tensor.sbis.common.generated.EventType;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.common.util.scroll.ScrollHelper;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel;
import ru.tensor.sbis.mvp.data.model.PagedListResult;
import ru.tensor.sbis.mvp.search.BaseSearchablePresenter;
import timber.log.Timber;

/**
 * Презентер добавления сотрудников внутри компании в реестр контактов
 * @see AddInternalEmployeesContract.Presenter
 *
 * @author da.zhukov
 */
public class AddInternalEmployeesPresenter
        extends BaseSearchablePresenter<AddInternalEmployeesContract.View, AddContactModel, Pair<EventType, HashMap<String, String>>>
        implements AddInternalEmployeesContract.Presenter {

    //region cache
    private List<AddContactModel> mContactsList;
    //endregion

    private final AddInternalEmployeesInteractor mInteractor;
    private final UUID mFolderUuid;

    @Nullable
    private Disposable mAddingContactDisposable;

    /** Текущя позиция элемента запроса */
    private int currentFromRequest = 0;
    private boolean isLoadingError = false;

    public AddInternalEmployeesPresenter(@NonNull AddInternalEmployeesInteractor interactor,
                                         @NonNull NetworkUtils networkUtils,
                                         @Nullable UUID folderUuid,
                                         @NonNull ScrollHelper scrollHelper) {
        super(null, networkUtils, scrollHelper);
        mInteractor = interactor;
        mFolderUuid = folderUuid;
        makeSearchRequest();
    }

    @Override
    public void attachView(@NonNull AddInternalEmployeesContract.View view) {
        super.attachView(view);
        if (mLoadingState == LoadingState.NOT_LOADING) {
            showEmptyViewIfNeeded(view, mContactsList, getEmptyViewErrorId());
        }
    }

    @Override
    public void onDestroy() {
        if (mAddingContactDisposable != null && !mAddingContactDisposable.isDisposed()) {
            mAddingContactDisposable.dispose();
        }
        super.onDestroy();
    }

    //region Contract
    @Override
    public void onContactSelected(@NonNull AddContactModel contact) {
        mAddingContactDisposable = mInteractor.addContact(contact.getEmployee().getUuid(), mFolderUuid)
                .subscribe(
                        result -> {
                            if (mView != null) {
                                mView.hideKeyboard();
                                mView.onAddContactResult(result);
                            }
                        },
                        throwable -> {
                            if (mView != null) {
                                mView.hideKeyboard();
                                mView.showError(ru.tensor.sbis.communicator.design.R.string.communicator_contact_add_error);
                            }
                        }
                );
    }

    @Override
    public void onContactPhotoClicked(@NonNull AddContactModel contact) {
        UUID uuid = contact.getEmployee().getUuid();
        if (mView != null) {
            mView.openProfile(uuid);
        }
    }

    @Override
    public void onSelectionCancel() {
        if (mView != null) {
            mView.showSelectionCancel();
        }
    }

    @Override
    public boolean stubIsShowing() {
        if (mContactsList == null) {
            mContactsList = Collections.emptyList();
        }
        return mContactsList.isEmpty();
    }

    //endregion

    //region AbstractSearchablePresenter

    /**
     * Проверка на первый введенный символ в пустой поиск
     * Добавлена в связи с облачным поиском сотрудников от двух символов, поэтому
     * делаем запрос от двух символов, чтобы не провоцировать крутилку или заглушку
     * @param searchQuery новый поисковый запрос
     * @return true, если введен 1 символ в пустую поисковую строку
     */
    private boolean isFirstSymbol(@NonNull String searchQuery) {
        return mSearchQuery.isEmpty() && searchQuery.length() == 1;
    }

    @Override
    public void onSearchQueryChanged(@NonNull String searchQuery) {
        if (!(mSearchQuery.equals(searchQuery) || isFirstSymbol(searchQuery))) {
            super.onSearchQueryChanged(searchQuery);
        }
        currentFromRequest = 0;
    }

    @Override
    protected void makeSearchRequest() {
        super.makeSearchRequest();
        mLoadingState = LoadingState.LOADING_UNSPECIFIED;
        mLoadingPageSubscription.set(
                mInteractor.searchInternalEmployees(
                        mSearchQuery,
                        0,
                        getPageSize()
                )
                .subscribe(
                    this::processContactsLoading,
                    this::processContactsLoadingError
                )
        );
    }

    @Override
    public void onSearchButtonClicked() {
        if (mView != null) mView.hideCursorFromSearch();
    }

    @Override
    protected void initDefaultKeyboardVisibility() {
        mKeyboardIsVisible = true;
    }

    //endregion AbstractSearchablePresenter

    //region AbstractTwoWayPaginationPresenter
    @Override
    protected boolean isNeedLoadNewerPage(int firstVisibleItemPosition) {
        return mDataListOffset > 0
                && firstVisibleItemPosition - mDataListOffset <= getItemsReserve()
                && mLoadingState == LoadingState.NOT_LOADING;
    }

    @Override
    protected int getEmptyViewErrorId() {
        if (isLoadingError) {
            return ru.tensor.sbis.common.R.string.common_no_network_available_check_connection;
        } else {
            return mSearchQuery.isEmpty() ? ru.tensor.sbis.communicator.design.R.string.communicator_no_contacts_to_display
                    : ru.tensor.sbis.design.R.string.design_empty_search_error_string;
        }
    }

    @Nullable
    @Override
    protected List<AddContactModel> getDataList() {
        return mContactsList;
    }

    @Override
    protected void swapDataList(@NonNull List<AddContactModel> dataList) {
        mContactsList = dataList;
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<AddContactModel>> getLoadingNewerDataObservable(@Nullable AddContactModel contact, int itemsCount) {
        itemsCount = Math.min(mDataListOffset, itemsCount);
        int from = mDataListOffset - itemsCount;
        return mInteractor.searchInternalEmployees(mSearchQuery, from, itemsCount);
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<AddContactModel>> getLoadingOlderDataObservable(@NonNull AddContactModel contact, int itemsCount) {
        return mInteractor.loadInternalEmployeesPage(mSearchQuery, currentFromRequest, itemsCount);
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<AddContactModel>> getUpdatingListByLastEntityObservable(@Nullable AddContactModel contact, int itemsCount, boolean fromPullToRefresh) {
        return mInteractor.searchInternalEmployees(mSearchQuery, 0, itemsCount);
    }

    @Override
    protected void finalProcessOlderPageLoading() {
        super.finalProcessOlderPageLoading();
        if (mHasOlderPage) currentFromRequest += getPageSize();
    }

    @Override
    protected int getMaxDataListSize() {
        //вынужденное решение, облачная навигация не может отдавать сотрудников по анкору,
        //движение к older происходит через постраничную загрузку, которую не реализовать в newer
        return Integer.MAX_VALUE;
    }

    //endregion AbstractTwoWayPaginationPresenter

    private void processContactsLoading(@NonNull PagedListResult<AddContactModel> pagedListResult) {
        isLoadingError = false;
        mLoadingState = LoadingState.NOT_LOADING;
        processUpdatingDataListResult(pagedListResult, false);

        if (mView != null) {
            if (mContactsList.isEmpty()) {
                mView.showMessageInEmptyView(ru.tensor.sbis.design.R.string.design_empty_search_error_string);
            }
            mView.updateDataList(mContactsList, mDataListOffset);
        }
    }

    private void processContactsLoadingError(@NonNull Throwable error) {
        isLoadingError = true;
        mLoadingState = LoadingState.NOT_LOADING;
        mContactsList = Collections.emptyList();

        if (mView != null) {
            mView.showMessageInEmptyView(ru.tensor.sbis.common.R.string.common_no_network_available_check_connection);
            mView.updateDataList(mContactsList, mDataListOffset);
            Timber.e(error);
        }
    }

    //region Display view state
    protected boolean isNeedToDisplayViewState() {
        return true;
    }

    protected void displayViewState(@NonNull AddInternalEmployeesContract.View view) {
        view.updateDataList(mContactsList, mDataListOffset);
    }
    //endregion
}
