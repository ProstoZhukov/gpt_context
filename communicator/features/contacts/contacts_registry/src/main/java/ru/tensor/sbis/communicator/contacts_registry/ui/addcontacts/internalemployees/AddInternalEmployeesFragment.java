package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import ru.tensor.sbis.base_components.adapter.OnItemClickListener;
import ru.tensor.sbis.base_components.adapter.contacts.holder.OnContactPhotoClickListener;
import ru.tensor.sbis.common.listener.ResultListener;
import ru.tensor.sbis.common.util.AdjustResizeHelper;
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent;
import ru.tensor.sbis.communicator.contacts_registry.ContactsRegistryFeatureFacade;
import ru.tensor.sbis.communicator.contacts_registry.R;
import ru.tensor.sbis.communicator.contacts_registry.contract.ContactsRegistryDependency;
import ru.tensor.sbis.communicator.contacts_registry.di.list.DaggerContactListComponent;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.AddContactResult;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.adapter.AddContactListAdapter;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.di.AddInternalEmployeesComponent;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.di.DaggerAddInternalEmployeesComponent;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel;
import ru.tensor.sbis.design.stubview.StubViewCase;
import ru.tensor.sbis.design_notification.snackbar.SnackBarUtils;
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager;
import ru.tensor.sbis.mvp.search.BaseSearchableView;

/**
 * Фрагмент добавления сотрудника внутри компании в реестр контактов
 *
 * @author da.zhukov
 */
@UiThread
public class AddInternalEmployeesFragment
        extends BaseSearchableView<AddContactModel, AddContactListAdapter, AddInternalEmployeesContract.View, AddInternalEmployeesContract.Presenter>
        implements AddInternalEmployeesContract.View, OnItemClickListener<AddContactModel>, OnContactPhotoClickListener<AddContactModel>,
        AdjustResizeHelper.KeyboardEventListener {

    private static final String FOLDER_UUID = AddInternalEmployeesFragment.class.getCanonicalName() + ".FOLDER_UUID";
    private static final String LAYOUT_MANAGER_STATE_KEY = "list_layout_manager_state";

    private AddInternalEmployeesComponent mComponent;

    @Nullable
    private ResultListener<AddContactResult> mResultListener;
    private View mRootView;
    private PaginationLayoutManager layoutManager;

    public static AddInternalEmployeesFragment newInstance(@Nullable UUID folderUuid) {
        AddInternalEmployeesFragment fragment = new AddInternalEmployeesFragment();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    @Override
    public void onDestroyView() {
        mAdapter.setOnContactClickListener(null);
        mAdapter.setOnContactPhotoClickListener(null);
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(LAYOUT_MANAGER_STATE_KEY, layoutManager.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (getActivity() != null && !getActivity().isChangingConfigurations()) {
            mComponent = null;
        }
        super.onDestroy();
    }

    @Override
    protected void initViews(@NonNull View mainView, @Nullable Bundle savedInstanceState) {
        super.initViews(mainView, savedInstanceState);
        initList(mainView, savedInstanceState);
    }

    private void initList(@NonNull View mainView, Bundle savedInstanceState) {
        mRootView = mainView.findViewById(R.id.communicator_fragment_add_internal_employees_coordinator_layout);
        mSbisListView = mainView.findViewById(R.id.communicator_fragment_add_internal_employees_sbis_list_view);
        mSbisListView.setHasFixedSize(true);
        mSbisListView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        mSbisListView.getRecycledViewPool().setMaxRecycledViews(0, 15);

        layoutManager = new PaginationLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSbisListView.setLayoutManager(layoutManager);

        mSbisListView.setAdapter(mAdapter);
        if(savedInstanceState != null) {
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE_KEY));
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.communicator_fragment_add_internal_employees;
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
    public void openProfile(@NonNull UUID profileUuid) {
        Context context = getContext();
        if (context == null) return;
        ContactsRegistryDependency contactsDependency = ContactsRegistryFeatureFacade.contactsDependency;
        context.startActivity(contactsDependency.createPersonCardIntent(context, profileUuid));
    }
    //endregion Contract

    //region ClickListeners
    @Override
    public void onClickItem(AddContactModel contact, int position) {
        mPresenter.onContactSelected(contact);
    }

    @Override
    public void onContactPhotoClick(AddContactModel contact) {
        mPresenter.onContactPhotoClicked(contact);
    }
    //endregion ClickListeners

    //region BasePresenterFragment
    @Override
    protected void inject() {
        mComponent = DaggerAddInternalEmployeesComponent.builder()
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

    @NonNull
    @Override
    protected AddInternalEmployeesContract.View getPresenterView() {
        return this;
    }

    @NonNull
    @Override
    protected AddInternalEmployeesContract.Presenter createPresenter() {
        return mComponent.getAddInternalEmployeesPresenter();
    }
    //endregion BasePresenterFragment

    //region SearchableView
    @Override
    public void hideControls() {
        mPresenter.onKeyboardClosed(true);
    }

    @Override
    public void showControls() {
        //ignore
    }

    @Override
    public void showKeyboard() {
        if (mSearchPanel != null) {
            mSearchPanel.showKeyboard();
        }
    }

    @Override
    public void hideKeyboard() {
        if (mSearchPanel != null) {
            mSearchPanel.hideKeyboard();
            onKeyboardCloseMeasure(0);
        }
    }

    //endregion

    //region onKeyboardMeasure
    @Override
    public boolean onKeyboardOpenMeasure(int keyboardHeight) {
        super.onKeyboardOpenMeasure(keyboardHeight);
        if (mSearchPanel != null) {
            mSearchPanel.showCursorInSearch();
        }
        if (mSbisListView != null) {
            int bottomHeight;
            if (mPresenter.stubIsShowing() && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                bottomHeight = 0;
            } else {
                bottomHeight = keyboardHeight;
            }
            mSbisListView.setPadding(0, 0, 0, bottomHeight);
        }
        return true;
    }

    @Override
    public boolean onKeyboardCloseMeasure(int keyboardHeight) {
        super.onKeyboardCloseMeasure(keyboardHeight);
        if (mSbisListView != null) {
            mSbisListView.setPadding(0, 0, 0, 0);
        }
        return true;
    }
    //endregion

    @NonNull
    @Override
    protected Object createEmptyViewContent(@StringRes int messageTextId) {
        if (messageTextId == ru.tensor.sbis.communicator.design.R.string.communicator_no_contacts_to_display) {
            return StubViewCase.NO_CONTACTS.getContent();
        } else if (messageTextId == ru.tensor.sbis.design.R.string.design_empty_search_error_string) {
            return StubViewCase.NO_SEARCH_RESULTS.getContent();
        } else if (messageTextId == ru.tensor.sbis.common.R.string.common_no_network_available_check_connection) {
            return StubViewCase.NO_CONNECTION.getContent();
        } else {
            return StubViewCase.SBIS_ERROR.getContent();
        }
    }

    @NonNull
    @Override
    protected Object createEmptyViewContent(int messageTextId, int detailTextId) {
        if (detailTextId == ru.tensor.sbis.common.R.string.common_no_network_available_check_connection) {
            return StubViewCase.NO_CONNECTION.getContent();
        } else {
            return createEmptyViewContent(messageTextId);
        }
    }
}
