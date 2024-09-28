package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import kotlin.Pair;
import ru.tensor.sbis.CXX.NetworkException;
import ru.tensor.sbis.common.generated.EventType;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel;
import ru.tensor.sbis.mvp.data.model.PagedListResult;
import ru.tensor.sbis.mvp.presenter.AbstractTwoWayPaginationPresenter;
import timber.log.Timber;

/**
 * Презентер добавления нового контакта в реестр контактов
 * @see AddNewContactsContract.Presenter
 *
 * @author da.zhukov
 */
public class AddNewContactsPresenter
        extends AbstractTwoWayPaginationPresenter<AddNewContactsContract.View, AddContactModel, Pair<EventType, HashMap<String, String>>>
        implements AddNewContactsContract.Presenter {

    //region cache
    private List<AddContactModel> mNewContactsList;
    //endregion

    private static final String SEARCH_QUERY_DEFAULT = "";

    private final AddNewContactsInteractor mInteractor;
    private final UUID mFolderUuid;
    private String mNameQuery, mPhoneQuery, mEmailQuery;
    private boolean isLoadingError = false;

    @Nullable
    private Disposable mAddingContactDisposable;

    public AddNewContactsPresenter(@NonNull AddNewContactsInteractor interactor,
                                   @NonNull NetworkUtils networkUtils,
                                   @Nullable UUID folderUuid
    ) {
        super(null, networkUtils);
        mInteractor = interactor;
        mFolderUuid = folderUuid;
        mNameQuery = SEARCH_QUERY_DEFAULT;
        mPhoneQuery = SEARCH_QUERY_DEFAULT;
        mEmailQuery = SEARCH_QUERY_DEFAULT;
    }

    @Override
    public void attachView(@NonNull AddNewContactsContract.View view) {
        super.attachView(view);
        showEmptyViewIfNeeded(view, mNewContactsList, getEmptyViewErrorId());
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
    public void onSearchButtonClicked() {
        if (TextUtils.isEmpty(mNameQuery)) {
            if (mView != null) {
                mView.showSearchRestrictionAlert();
                mView.setSearchNameBackgroundColor(true);
            }
            return;
        }
        if (mView != null) {
            mView.hideKeyboardAndClearFocus();
            mView.setWasSearchButtonClickedFlag();
        }
        resetPagination();
        mView.setSearchNameBackgroundColor(false);
        mLoadingPageSubscription.set(
                mInteractor.searchNewContacts(mNameQuery, mPhoneQuery, mEmailQuery, 0, getPageSize())
                        .doOnSubscribe(disposable -> mView.showLoading())
                        .doAfterTerminate(() -> mView.hideLoading())
                        .subscribe(
                                this::processContactsLoading,
                                this::processContactsLoadingError
                        )
        );
    }

    @Override
    public void onSearchFieldNameQueryChanged(@NonNull String nameQuery) {
        if (!mNameQuery.equals(nameQuery)) {
            mNameQuery = nameQuery;
        }
    }

    @Override
    public void onSearchFieldPhoneQueryChanged(@NonNull String phoneQuery) {
        if (!mPhoneQuery.equals(phoneQuery)) {
            mPhoneQuery = phoneQuery;
        }
    }

    @Override
    public void onSearchFieldEmailQueryChanged(@NonNull String emailQuery) {
        if (!mEmailQuery.equals(emailQuery)) {
            mEmailQuery = emailQuery;
        }
    }

    @Override
    public void onSearchFieldNameClearButtonClicked() {
        if (mView != null) {
            mView.clearSearchFieldName();
        }
    }

    @Override
    public void onSearchFieldPhoneClearButtonClicked() {
        if (mView != null) {
            mView.clearSearchFieldPhone();
        }
    }

    @Override
    public void onSearchFieldEmailClearButtonClicked() {
        if (mView != null) {
            mView.clearSearchFieldEmail();
        }
    }

    @Override
    public void onContactSelected(@NonNull AddContactModel contact) {
        mAddingContactDisposable = mInteractor.addContact(contact.getEmployee().getUuid(), mFolderUuid)
                .subscribe(
                        result -> {
                            if (mView != null) {
                                mView.hideKeyboardAndClearFocus();
                                mView.onAddContactResult(result);
                            }
                        },
                        throwable -> {
                            if (mView != null) {
                                mView.hideKeyboardAndClearFocus();
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
    //endregion Contract

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
            final String searchQuery = mNameQuery + mPhoneQuery + mEmailQuery;
            return searchQuery.isEmpty() ? ru.tensor.sbis.communicator.design.R.string.communicator_no_contacts_to_display
                    : ru.tensor.sbis.design.R.string.design_empty_search_error_string;
        }
    }

    @Nullable
    @Override
    protected List<AddContactModel> getDataList() {
        return mNewContactsList;
    }

    @Override
    protected void swapDataList(@NonNull List<AddContactModel> dataList) {
        mNewContactsList = dataList;
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<AddContactModel>> getLoadingNewerDataObservable(@Nullable AddContactModel contactModel, int itemsCount) {
        itemsCount = Math.min(mDataListOffset, itemsCount);
        int from = mDataListOffset - itemsCount;
        return mInteractor.searchNewContacts(mNameQuery, mPhoneQuery, mEmailQuery, from, itemsCount);
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<AddContactModel>> getLoadingOlderDataObservable(@NonNull AddContactModel contactModel, int itemsCount) {
        return mInteractor.searchNewContacts(mNameQuery, mPhoneQuery, mEmailQuery, mDataListOffset + getDataListSize(), itemsCount);
    }

    @NonNull
    @Override
    protected Observable<PagedListResult<AddContactModel>> getUpdatingListByLastEntityObservable(@Nullable AddContactModel contactModel, int itemsCount, boolean fromPullToRefresh) {
        return mInteractor.searchNewContacts(mNameQuery, mPhoneQuery, mEmailQuery, 0, itemsCount);
    }
    //endregion AbstractTwoWayPaginationPresenter

    private void processContactsLoading(@NonNull PagedListResult<AddContactModel> pagedListResult) {
        isLoadingError = false;
        mLoadingState = LoadingState.NOT_LOADING;
        processUpdatingDataListResult(pagedListResult, false);

        if (mView != null) {
            if (mNewContactsList.isEmpty()) {
                mView.showMessageInEmptyView(ru.tensor.sbis.design.R.string.design_empty_search_error_string);
            }
            mView.updateDataList(mNewContactsList, mDataListOffset);
        }
    }

    private void processContactsLoadingError(@NonNull Throwable error) {
        isLoadingError = true;
        mLoadingState = LoadingState.NOT_LOADING;
        mNewContactsList = Collections.emptyList();

        if (mView != null) {
            int stubInfo = error instanceof NetworkException ? ru.tensor.sbis.common.R.string.common_no_network_available_check_connection
                    : ru.tensor.sbis.design.R.string.design_empty_search_error_string;
            mView.showMessageInEmptyView(stubInfo);
            mView.updateDataList(mNewContactsList, mDataListOffset);
            Timber.e(error);
        }
    }

    //region Display view state
    protected boolean isNeedToDisplayViewState() {
        return true;
    }

    protected void displayViewState(@NonNull AddNewContactsContract.View view) {
        view.updateDataList(mNewContactsList, mDataListOffset);
    }
    //endregion

    @Override
    public void onScroll(int dy, int firstVisibleItemPosition, int lastVisibleItemPosition, int computeVerticalScrollOffset) {
        super.onScroll(dy, firstVisibleItemPosition, lastVisibleItemPosition, computeVerticalScrollOffset);
        if (dy != 0 && mView != null) {
            mView.hideKeyboardAndClearFocus();
        }
    }

    private int getDataListSize() {
        return mNewContactsList != null ? mNewContactsList.size() : 0;
    }

    @Override
    protected int getPageSize() {
        // надеюсь временный костыль, чтобы количество найденных контактов совпадало с ios.
        return 100;
    }

    protected int getMaxDataListSize() {
        if (isNewerPaginationSupported()) {
            return 350;
        }
        // Если отключена пагинация вверх, убираем ограничение с длины списка данных
        return Integer.MAX_VALUE;
    }
}
