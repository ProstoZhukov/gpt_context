package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import ru.tensor.sbis.base_components.adapter.OnItemClickListener;
import ru.tensor.sbis.base_components.adapter.contacts.holder.OnContactPhotoClickListener;
import ru.tensor.sbis.common.listener.ResultListener;
import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.common.util.DeviceConfigurationUtils;
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent;
import ru.tensor.sbis.communicator.contacts_registry.ContactsRegistryFeatureFacade;
import ru.tensor.sbis.communicator.contacts_registry.R;
import ru.tensor.sbis.communicator.contacts_registry.contract.ContactsRegistryDependency;
import ru.tensor.sbis.communicator.contacts_registry.di.list.DaggerContactListComponent;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.AddContactResult;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.adapter.AddContactListAdapter;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts.di.AddNewContactsComponent;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts.di.DaggerAddNewContactsComponent;
import ru.tensor.sbis.communicator.core.views.SearchField;
import ru.tensor.sbis.design.SbisMobileIcon;
import ru.tensor.sbis.design.stubview.StubViewCase;
import ru.tensor.sbis.design.utils.KeyboardUtils;
import ru.tensor.sbis.design.utils.ThemeUtil;
import ru.tensor.sbis.design_notification.snackbar.SnackBarUtils;
import ru.tensor.sbis.design_notification.SbisPopupNotification;
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle;
import ru.tensor.sbis.mvp.fragment.BaseListFragmentWithTwoWayPagination;
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager;

/**
 * Фрагмент добавления нового контакта в реестр контактов
 *
 * @author da.zhukov
 */
@UiThread
public class AddNewContactsFragment extends BaseListFragmentWithTwoWayPagination<AddContactModel, AddContactListAdapter, AddNewContactsContract.View, AddNewContactsContract.Presenter>
        implements AddNewContactsContract.View, OnItemClickListener<AddContactModel>, OnContactPhotoClickListener<AddContactModel> {

    private static final String FOLDER_UUID = AddNewContactsFragment.class.getCanonicalName() + ".FOLDER_UUID";
    private static final String IS_SEARCH_FIELD_FOCUSED = AddNewContactsFragment.class.getCanonicalName() + ".IS_SEARCH_FIELD_FOCUSED";
    private static final String WAS_SEARCH_BUTTON_CLICKED = AddNewContactsFragment.class.getCanonicalName() + ".WAS_SEARCH_BUTTON_CLICKED";

    private AddNewContactsComponent mComponent;
    private CompositeDisposable mCompositeDisposable;
    private SearchField mNameField, mPhoneField, mEmailField;
    private Button mSearchButton;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private LinearLayout mResultHeader;
    private View mRootView, mDescriptionView;
    private boolean isSearchFieldFocused = true;
    private boolean wasSearchButtonClicked = false;
    private int appBarVerticalOffset = 0;
    private PaginationLayoutManager paginationLayoutManager;
    private boolean isLoading = false;

    @Nullable
    private ResultListener<AddContactResult> mResultListener;

    public static AddNewContactsFragment newInstance(@Nullable UUID folderUuid) {
        AddNewContactsFragment fragment = new AddNewContactsFragment();
        Bundle args = new Bundle();
        args.putSerializable(FOLDER_UUID, folderUuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    public void setAdapter(@NonNull AddContactListAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setOnContactClickListener(this);
        mAdapter.setOnContactPhotoClickListener(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            mResultListener = (ResultListener<AddContactResult>) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " should implement "
                    + ResultListener.class.getSimpleName());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mPresenter.onSelectionCancel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        mResultListener = null;
        super.onDetach();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onDestroyView() {
        mDescriptionView.setOnClickListener(null);
        mAdapter.setOnContactClickListener(null);
        mAdapter.setOnContactPhotoClickListener(null);
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (!requireActivity().isChangingConfigurations()) {
            mComponent = null;
        }
        super.onDestroy();
    }

    //region Contract
    @Override
    public void onAddContactResult(AddContactResult result) {
        if (mResultListener != null) {
            mResultListener.onResultOk(result);
        }
    }

    @Override
    public void showError(int errorTextResId) {
        if (mRootView != null) {
            SnackBarUtils.showFailureSnackbar(mRootView, requireContext().getResources().getString(errorTextResId));
        }
    }

    @Override
    public void showSelectionCancel() {
        if (mResultListener != null) {
            mResultListener.onResultCancel();
        }
    }

    @Override
    public void clearSearchFieldName() {
        mNameField.setSearchQuery(SearchField.DEFAULT_SEARCH_QUERY);
    }

    @Override
    public void clearSearchFieldPhone() {
        mPhoneField.setSearchQuery(SearchField.DEFAULT_SEARCH_QUERY);
    }

    @Override
    public void clearSearchFieldEmail() {
        mEmailField.setSearchQuery(SearchField.DEFAULT_SEARCH_QUERY);
    }

    @Override
    public void hideKeyboardAndClearFocus() {
        View viewWithFocus = requireActivity().getCurrentFocus();
        if (viewWithFocus != null) {
            viewWithFocus.clearFocus();
            KeyboardUtils.hideKeyboard(mSearchButton);
        }
    }

    private void showKeyboard(@NonNull View view) {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            KeyboardUtils.showKeyboard(view);
        } else {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public void showSearchRestrictionAlert() {
        if (getContext() != null) {
            String icon = String.valueOf(SbisMobileIcon.Icon.smi_alert.getCharacter());
            SbisPopupNotification.INSTANCE.push(requireContext(), SbisPopupNotificationStyle.INFORMATION,
                    getContext().getString(ru.tensor.sbis.communicator.design.R.string.communicator_contacts_adding_toast_message), icon);
        }
    }

    @Override
    public void setSearchNameBackgroundColor(boolean isErrorColor) {
        int color;
        if (isErrorColor) {
            color = ThemeUtil.getThemeColorInt(requireContext(), ru.tensor.sbis.design.R.attr.dangerSameBackgroundColor);
        } else {
            color = ThemeUtil.getThemeColorInt(requireContext(), com.google.android.material.R.attr.backgroundColor);
        }
        mNameField.setSearchFieldBackgroundColor(color);
    }

    @Override
    public void openProfile(@NonNull UUID profileUuid) {
        isSearchFieldFocused = false;
        Context context = getContext();
        if (context != null) {
            ContactsRegistryDependency contactsDependency = ContactsRegistryFeatureFacade.contactsDependency;
            context.startActivity(contactsDependency.createPersonCardIntent(context, profileUuid));
        }
    }

    @Override
    public void setWasSearchButtonClickedFlag() {
        wasSearchButtonClicked = true;
        showSbisListView();
    }

    @Override
    public void showLoading() {
        if (mSbisListView != null) {
            isLoading = true;
            View progressView = mSbisListView.findViewById(ru.tensor.sbis.design.list_utils.R.id.list_view_progress_view_id);
            int progressHeight = progressView != null ? progressView.getHeight() : 0;
            int padding = Math.max((mAppBarLayout.getHeight() + appBarVerticalOffset - progressHeight) / 2, 0);
            mSbisListView.setProgressBarVerticalMargin(0, padding);
            mSbisListView.setInProgress(true);
            mSbisListView.updateViewState();
            setupSearchLayoutScroll();
        }
    }

    @Override
    public void hideLoading() {
        if (mSbisListView != null) {
            isLoading = false;
            mSbisListView.setInProgress(false);
            mSbisListView.updateViewState();
            mSbisListView.setRefreshing(false);
            setupSearchLayoutScroll();
        }
    }
    //endregion Contract

    //region BasePresenterFragment
    @NonNull
    @Override
    protected AddNewContactsContract.View getPresenterView() {
        return this;
    }

    @NonNull
    @Override
    protected AddNewContactsContract.Presenter createPresenter() {
        return mComponent.getAddNewContactsPresenter();
    }

    @Override
    protected void inject() {
        mComponent =DaggerAddNewContactsComponent.builder()
                .folderUuid((UUID) requireArguments().getSerializable(FOLDER_UUID))
                .contactListComponent(
                        DaggerContactListComponent.builder()
                            .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(requireContext()))
                            .contactsDependency(ContactsRegistryFeatureFacade.contactsDependency)
                            .isViewHidden(false)
                            .build()
                )
                .build();
        mComponent.inject(this);
    }
    //endregion BasePresenterFragment

    //region AbstractBasePresenterListFragmentWithTwoWayPagination
    @Override
    protected int getLayoutRes() {
        return R.layout.communicator_fragment_add_new_contacts;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initViews(@NonNull View mainView, @Nullable Bundle savedInstanceState) {
        mRootView = mainView.findViewById(R.id.communicator_fragment_add_new_contacts_coordinator_layout);
        mDescriptionView = mainView.findViewById(ru.tensor.sbis.communicator.core.R.id.communicator_extended_contacts_search_description);

        mSbisListView = mainView.findViewById(R.id.communicator_fragment_add_new_contacts_sbis_list_view);
        mSbisListView.setHasFixedSize(true);
        mSbisListView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        mSbisListView.getRecycledViewPool().setMaxRecycledViews(0, 15);
        mSbisListView.getRecyclerView().setOverScrollMode(View.OVER_SCROLL_NEVER);

        paginationLayoutManager = new PaginationLayoutManager(requireContext()) {

            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                setupSearchLayoutScroll();
            }
        };
        paginationLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSbisListView.setLayoutManager(paginationLayoutManager);

        mSbisListView.setAdapter(mAdapter);

        mNameField = mainView.findViewById(ru.tensor.sbis.communicator.core.R.id.communicator_search_field_name);
        mPhoneField = mainView.findViewById(ru.tensor.sbis.communicator.core.R.id.communicator_search_field_phone);
        mEmailField = mainView.findViewById(ru.tensor.sbis.communicator.core.R.id.communicator_search_field_email);
        mSearchButton = mainView.findViewById(ru.tensor.sbis.communicator.core.R.id.communicator_extended_contacts_search_find_button);

        mCollapsingToolbarLayout = mainView.findViewById(R.id.communicator_fragment_add_new_contacts_collapsing_toolbar_layout);
        mResultHeader = mainView.findViewById(ru.tensor.sbis.communicator.core.R.id.communicator_result_header);

        mAppBarLayout = mainView.findViewById(R.id.communicator_fragment_add_new_contacts_app_bar_layout);
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            // прячем клавиатуру при скроле
            if (verticalOffset != appBarVerticalOffset) {
                hideKeyboardAndClearFocus();
                appBarVerticalOffset = verticalOffset;
            }
        });
        View.OnFocusChangeListener listener = (v, hasFocus) -> {
            if (hasFocus) mSbisListView.scrollToPosition(0);
        };
        mNameField.setOnFocusChangeListener(listener);
        mPhoneField.setOnFocusChangeListener(listener);
        mEmailField.setOnFocusChangeListener(listener);
        setupSearchLayoutScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!DeviceConfigurationUtils.isTablet(mRootView.getContext())) {
            View viewWithFocus = requireActivity().getCurrentFocus();
            if (viewWithFocus != null && isSearchFieldFocused) {
                showKeyboard(viewWithFocus);
            } else {
                hideKeyboardAndClearFocus();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        View viewWithFocus = requireActivity().getCurrentFocus();
        boolean isFocused = viewWithFocus instanceof EditText;
        outState.putBoolean(IS_SEARCH_FIELD_FOCUSED, isFocused);
        outState.putBoolean(WAS_SEARCH_BUTTON_CLICKED, wasSearchButtonClicked);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            isSearchFieldFocused = savedInstanceState.getBoolean(IS_SEARCH_FIELD_FOCUSED);
            wasSearchButtonClicked = savedInstanceState.getBoolean(WAS_SEARCH_BUTTON_CLICKED);
            showSbisListView();
        }
    }

    @Override
    protected void initViewListeners() {
        super.initViewListeners();

        mDescriptionView.setOnClickListener(view -> hideKeyboardAndClearFocus());

        mCompositeDisposable = new CompositeDisposable();

        mCompositeDisposable.add(
                mNameField.searchQueryChangedObservable(true)
                        .skip(1)
                        .subscribe(nameQuery -> mPresenter.onSearchFieldNameQueryChanged(nameQuery))
        );
        mCompositeDisposable.add(
                mPhoneField.searchQueryChangedObservable(true)
                        .skip(1)
                        .subscribe(phoneQuery -> mPresenter.onSearchFieldPhoneQueryChanged(phoneQuery))
        );
        mCompositeDisposable.add(
                mEmailField.searchQueryChangedObservable(true)
                        .skip(1)
                        .subscribe(emailQuery -> mPresenter.onSearchFieldEmailQueryChanged(emailQuery))
        );
        mCompositeDisposable.add(
                mNameField.cancelSearchObservable()
                .subscribe(o -> mPresenter.onSearchFieldNameClearButtonClicked())
        );
        mCompositeDisposable.add(
                mPhoneField.cancelSearchObservable()
                .subscribe(o -> mPresenter.onSearchFieldPhoneClearButtonClicked())
        );
        mCompositeDisposable.add(
                mEmailField.cancelSearchObservable()
                .subscribe(o -> mPresenter.onSearchFieldEmailClearButtonClicked())
        );

        subscribeToSearchField(mNameField, mCompositeDisposable);
        subscribeToSearchField(mPhoneField, mCompositeDisposable);
        subscribeToSearchField(mEmailField, mCompositeDisposable);

        mSearchButton.setOnClickListener(view -> mPresenter.onSearchButtonClicked());
    }

    private void subscribeToSearchField(@NonNull SearchField searchField, @NonNull CompositeDisposable compositeDisposable) {
        compositeDisposable.add(
                searchField.searchFieldEditorActionsObservable()
                        .subscribe(
                                actionId -> {
                                    if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEARCH) {
                                        mPresenter.onSearchButtonClicked();
                                    }
                                }
                        )
        );
    }

    @Override
    public void updateDataList(List<AddContactModel> dataList, int offset) {
        super.updateDataList(dataList, offset);

        // If dataList empty or error occurred then make result header visible and hide it otherwise
        mResultHeader.setVisibility(CommonUtils.isEmpty(dataList) ? View.GONE : View.VISIBLE);
    }

    @NonNull
    @Override
    protected Object createEmptyViewContent(int messageTextId) {
        if (messageTextId == ru.tensor.sbis.design.R.string.design_empty_search_error_string) {
            return StubViewCase.NO_SEARCH_RESULTS;
        } else if (messageTextId == ru.tensor.sbis.common.R.string.common_no_network_available_check_connection) {
            return StubViewCase.NO_CONNECTION;
        } else {
            return StubViewCase.SBIS_ERROR;
        }
    }

    @NonNull
    @Override
    protected Object createEmptyViewContent(int messageTextId, int detailTextId) {
        if (detailTextId == ru.tensor.sbis.common.R.string.common_no_network_available_check_connection) {
            return StubViewCase.NO_CONNECTION;
        } else {
            return createEmptyViewContent(messageTextId);
        }
    }

    //endregion AbstractBasePresenterListFragmentWithTwoWayPagination

    //region ClickListeners
    @Override
    public void onClickItem(AddContactModel item, int position) {
        mPresenter.onContactSelected(item);
    }

    @Override
    public void onContactPhotoClick(AddContactModel contact) {
        mPresenter.onContactPhotoClicked(contact);
    }
    //endregion ClickListeners

    private void showSbisListView() {
        if (wasSearchButtonClicked && mSbisListView != null && mSbisListView.getVisibility() != View.VISIBLE) {
            mSbisListView.setVisibility(View.VISIBLE);
            setupSearchLayoutScroll();
        }
    }

    private void setupSearchLayoutScroll() {
        if (mSbisListView == null) {
            return;
        }
        if (mSbisListView.isInformationViewVisible() || isLoading) {
            int minHeight = (int)getResources().getDimension(ru.tensor.sbis.communicator.design.R.dimen.communicator_add_new_contact_button_height);
            setSearchBarScrollLimit(minHeight);
        } else {
            setupSearchScrollForRecycler();
        }
    }

    private void setupSearchScrollForRecycler() {
        int last = paginationLayoutManager.findLastVisibleItemPosition();
        // если последняя видимая ячейка в списке это не последний элемент в адаптере, скрол не ограничен
        if (last < mAdapter.getItemCount() - 1) {
            setSearchBarScrollLimit(0);
            return;
        }
        View rootView = getView();
        View lastCell = paginationLayoutManager.findViewByPosition(last);
        // скрол ограничен высотой контента в списке
        if (lastCell != null && rootView != null) {
            int contentHeight = (int) (lastCell.getY() + lastCell.getHeight());
            int scrollLimit = Math.min(Math.max(rootView.getHeight() - contentHeight, 0), mAppBarLayout.getHeight());
            setSearchBarScrollLimit(scrollLimit);
        }
    }

    private void setSearchBarScrollLimit(int scrollLimit) {
        if (mCollapsingToolbarLayout.getMinimumHeight() != scrollLimit) {
            mCollapsingToolbarLayout.setMinimumHeight(scrollLimit);
        }
    }
}
