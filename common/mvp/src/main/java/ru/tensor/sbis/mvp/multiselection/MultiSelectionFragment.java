package ru.tensor.sbis.mvp.multiselection;

import static ru.tensor.sbis.design.utils.DoubleClickPreventerKt.preventDoubleClick;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import ru.tensor.sbis.base_components.BaseProgressDialogFragment;
import ru.tensor.sbis.base_components.ProgressDialogCallbacks;
import ru.tensor.sbis.common.util.AdjustResizeHelper;
import ru.tensor.sbis.design_notification.SbisPopupNotification;
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle;
import ru.tensor.sbis.mvp.R;
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager;
import ru.tensor.sbis.mvp.multiselection.adapter.MultiSelectionAdapter;
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;
import ru.tensor.sbis.mvp.search.BaseSearchableView;

/**
 * Экран мульти выбора
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"JavaDoc", "deprecation", "unused", "RedundantSuppression"})
public abstract class MultiSelectionFragment
        extends
        BaseSearchableView<MultiSelectionItem, MultiSelectionAdapter, MultiSelectionContract.View, MultiSelectionContract.Presenter>
        implements
        MultiSelectionContract.View,
        MultiSelectionItemClickListener,
        AdjustResizeHelper.KeyboardEventListener,
        MultiSelectedItemsPanel.OnArrowsClickListener,
        ProgressDialogCallbacks {

    /**
     * @SelfDocumented
     */
    public static final int DEFAULT_RESULT_MAX_COUNT = Integer.MAX_VALUE;

    /**
     * Duration of change visibility animation for done button
     */
    private static final int DONE_BUTTON_ANIMATION_DURATION = 50;

    /**
     * Necessary flags for the presenter's logic.
     * SINGLE_CHOICE_BUNDLE - opening selection screen for single choice
     * RESULT_CAN_BE_EMPTY_BUNDLE - closing selection screen with empty result
     * RESULT_MAX_COUNT_BUNDLE - numerical selection limit
     */
    public static final String SINGLE_CHOICE_BUNDLE = MultiSelectionFragment.class.getCanonicalName() + ".SINGLE_CHOICE_BUNDLE";
    public static final String RESULT_CAN_BE_EMPTY_BUNDLE = MultiSelectionFragment.class.getCanonicalName() + ".RESULT_CAN_BE_EMPTY_BUNDLE";
    public static final String RESULT_MAX_COUNT_BUNDLE = MultiSelectionFragment.class.getCanonicalName() + ".RESULT_MAX_COUNT_BUNDLE";
    private static final long BACK_BUTTON_DEBOUNCE_TIME = 100L;

    /**
     * @SelfDocumented
     */
    @NonNull
    public static Bundle createArguments(boolean isSingleChoice,
                                         boolean resultCanBeEmpty,
                                         int resultMaxCount) {
        Bundle args = new Bundle();
        args.putBoolean(SINGLE_CHOICE_BUNDLE, isSingleChoice);
        args.putBoolean(RESULT_CAN_BE_EMPTY_BUNDLE, resultCanBeEmpty);
        args.putInt(RESULT_MAX_COUNT_BUNDLE, resultMaxCount);
        return args;
    }

    @Nullable
    protected MultiSelectedItemsPanel mSelectedItemsView;
    @Nullable
    protected ViewGroup mRootView;
    @Nullable
    protected Toolbar mToolbar;
    @Nullable
    protected View mDoneButton;
    @Nullable
    protected View mCloseButton;
    @Nullable
    private BaseProgressDialogFragment mProgressDialog;

    /**
     * Implement this method to specify how to close your selection screen.
     */
    abstract public void finishSelection();

    /**
     * Метод, сигнализирующий о необходимости делегирования backpressed в Activity
     */
    @SuppressWarnings({"SameReturnValue", "SpellCheckingInspection"})
    protected boolean delegateBackPressToActivity() {
        return true;
    }

    @Inject
    protected void setAdapter(@NonNull MultiSelectionAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setOnItemClickListener(this);
    }

    private void setArrowButtonsListener(boolean isSet) {
        if (mSelectedItemsView != null)
            mSelectedItemsView.setOnArrowsClickListener(isSet ? this : null);
    }

    @CallSuper
    @Override
    protected void initViews(@NonNull View mainView, @Nullable Bundle savedInstanceState) {
        super.initViews(mainView, savedInstanceState);
        mRootView = getRootView(mainView);
        mSelectedItemsView = getSelectedItemsView(mainView);
        initToolbar(mainView);
        initItemList(mainView);
    }

    protected void initToolbar(@NonNull View mainView) {
        mToolbar = getToolbarView(mainView);
        mDoneButton = mToolbar.findViewById(ru.tensor.sbis.common.R.id.multi_selection_done);
        mCloseButton = mToolbar.findViewById(ru.tensor.sbis.common.R.id.multi_selection_close);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && shouldSetSupportActionBar()) {
            activity.setSupportActionBar(mToolbar);
        }
    }

    @SuppressWarnings("SameReturnValue")
    protected boolean shouldSetSupportActionBar() {
        return true;
    }

    @NonNull
    protected MultiSelectedItemsPanel getSelectedItemsView(@NonNull View mainView) {
        return mainView.findViewById(R.id.selected_items_panel);
    }

    @NonNull
    protected ViewGroup getRootView(@NonNull View mainView) {
        return mainView.findViewById(R.id.multi_selection_root);
    }

    @NonNull
    protected Toolbar getToolbarView(@NonNull View mainView) {
        return mainView.findViewById(ru.tensor.sbis.design.toolbar.R.id.toolbar);
    }

    protected void initViewListeners() {
        super.initViewListeners();
        mSelectedItemsView.setOnArrowsClickListener(this);
        mDoneButton.setOnClickListener(v -> onDoneButtonClick());
        mCloseButton.setOnClickListener(preventDoubleClick(BACK_BUTTON_DEBOUNCE_TIME, this::onCloseButtonClick));
        setArrowButtonsListener(true);
    }

    private void onDoneButtonClick() {
        mPresenter.finishSelection(true);
    }

    protected void onCloseButtonClick() {
        if (delegateBackPressToActivity() && getActivity() != null) {
            getActivity().onBackPressed();
        }
        mPresenter.finishSelection(false);
    }

    protected void initItemList(@NonNull View mainView) {
        mSbisListView = mainView.findViewById(ru.tensor.sbis.common.R.id.multi_selection_list);
        final Context context = getContext();

        if (mSbisListView != null && context != null) {
            mLayoutManager = new PaginationLayoutManager(context);

            mSbisListView.setSwipeColorSchemeResources(ru.tensor.sbis.design.R.color.color_accent);
            RecyclerView.ItemAnimator animator = mSbisListView.getRecyclerView().getItemAnimator();
            animator.setMoveDuration(120);
            mSbisListView.setRecyclerViewBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mSbisListView.setSwipeProgressViewOffset(false,
                    getResources().getDimensionPixelOffset(R.dimen.mvp_multi_selection_list_padding_top),
                    getResources().getDimensionPixelOffset(R.dimen.mvp_multi_selection_list_swipe_refresh_top_to_search_panel));
            mSbisListView.setHasFixedSize(true);
            mSbisListView.setLayoutManager(mLayoutManager);
            mSbisListView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        setArrowButtonsListener(false);
        mDoneButton.setOnClickListener(null);
        mCloseButton.setOnClickListener(null);
        if (needToRemoveAllViews()) {
            if (mRootView != null) {
                ViewGroup parentViewGroup = (ViewGroup) mRootView.getParent();
                if (parentViewGroup != null) {
                    parentViewGroup.removeAllViews();
                }
            }
        }
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(null);
        }
        mSelectedItemsView = null;
        mRootView = null;
        mToolbar = null;
        mDoneButton = null;
        mCloseButton = null;
        mProgressDialog = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mAdapter.setOnItemClickListener(null);
        super.onDestroy();
    }

    //region BasePresenterFragment
    @Override
    protected int getPresenterLoaderId() {
        return ru.tensor.sbis.common.R.id.multi_selection_presenter_loader_id;
    }

    @NonNull
    @Override
    protected MultiSelectionContract.View getPresenterView() {
        return this;
    }
    //endregion

    //region MultiSelectionContract.View
    @Override
    public void updateDataList(@Nullable List<MultiSelectionItem> dataList, int offset, boolean isSelectedBlockExpanded) {
        changeArrowButtonsVisibility(isSelectedBlockExpanded);
        updateDataList(dataList, offset);
        getPresenter().switchDoneButton();
    }

    @Override
    public void onItemCheckedStateChanged(@NonNull MultiSelectionItem item, int position, boolean checked, boolean removeCheckedItem) {
        if (mSelectedItemsView == null) return;
        if (checked) {
            mSelectedItemsView.addItem(item);
            if (removeCheckedItem) {
                mAdapter.removeItem(item);
            } else if (position > -1) {
                mAdapter.notifyItemChanged(position);
            }
        } else {
            if (position > -1) {
                mAdapter.notifyItemChanged(position, MultiSelectionAdapter.PAYLOAD_CHECKED);
            }
            mSelectedItemsView.removeItem(item);
        }
    }

    @Override
    public void addItemToSelectedPanel(@NonNull MultiSelectionItem item) {
        if (mSelectedItemsView == null) return;
        mSelectedItemsView.addItem(item);
    }

    @Override
    public void removeItemFromSelectedPanel(@NonNull MultiSelectionItem item) {
        if (mSelectedItemsView == null) return;
        mSelectedItemsView.removeItem(item);
    }

    @Override
    public void setDoneButtonVisibility(boolean isVisible) {
        int targetVisibility = isVisible ? View.VISIBLE : View.GONE;
        if (mDoneButton != null) {
            if (mDoneButton.isClickable() != isVisible) {
                setDoneButtonClickable(isVisible);
            }
            if (mDoneButton.getVisibility() != targetVisibility) {
                TransitionManager.beginDelayedTransition(mToolbar, new AutoTransition().setDuration(DONE_BUTTON_ANIMATION_DURATION));
                mDoneButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public void setDoneButtonClickable(boolean isClickable) {
        mDoneButton.setClickable(isClickable);
    }

    @Override
    public void showResultMaxCountRestriction(int resultMaxCountRestrictionTextId, int maxResultCount) {
        final Context context = getContext();
        if (context != null) {
            if (maxResultCount == 1) {
                SbisPopupNotification.INSTANCE.push(context, SbisPopupNotificationStyle.ERROR,
                        context.getString(resultMaxCountRestrictionTextId));
            } else {
                SbisPopupNotification.INSTANCE.push(context, SbisPopupNotificationStyle.ERROR,
                        String.format(context.getString(resultMaxCountRestrictionTextId), maxResultCount));
            }
        }
    }

    @Override
    public void showToast(int message) {
        SbisPopupNotification.pushToast(requireContext(), message, Toast.LENGTH_LONG);
    }

    @Override
    public void scrollToStart() {
        if (mLayoutManager != null) {
            mLayoutManager.scrollToPosition(0);
        }
    }

    @Override
    public void setCheckedItems(Set<MultiSelectionItem> checkedItems) {
        mAdapter.setCheckedItems(checkedItems);
        if (checkedItems != null && mSelectedItemsView != null) {
            mSelectedItemsView.setItems(checkedItems);
        }
    }

    @Override
    public void changeArrowButtonsVisibility(boolean isExpanded) {
        if (mSelectedItemsView != null) {
            mSelectedItemsView.setupToggle(isExpanded);
        }
    }

    @Override
    public void setRollUpArrowEnabled(boolean isEnabled) {
        if (mSelectedItemsView == null) return;
        mSelectedItemsView.enableRollUpArrow(isEnabled);
    }

    @Override
    public void showSavingProcess() {
        hideSavingProcess();
        mProgressDialog = BaseProgressDialogFragment.newInstance(true);
        mProgressDialog.init(null, getString(ru.tensor.sbis.design.R.string.design_please_wait));
        mProgressDialog.show(getChildFragmentManager(), BaseProgressDialogFragment.class.getSimpleName());
    }

    @Override
    public void hideSavingProcess() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        if (mSbisListView != null) {
            mSbisListView.setInProgress(false);
        }
    }

    @Override
    public void showLoading() {
        if (mSbisListView != null) {
            mSbisListView.hideInformationView();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void showMessageInEmptyView(String message) {
        if (mSbisListView != null) {
            mSbisListView.showInformationViewData(createEmptyViewContent(message));
        }
    }

    @Override
    public void addContentToDataList(List<MultiSelectionItem> list) {
        mAdapter.addContent(list);
    }
    //endregion

    //region MultiSelectionItemClickListener
    @Override
    public void onClickCheckbox(MultiSelectionItem item, int position) {
        mPresenter.onItemClicked(item, position, false);
    }

    @Override
    public void onClickItem(MultiSelectionItem item, int position) {
        mPresenter.onItemClicked(item, position, true);
    }
    //endregion

    //region AdjustResizeHelper.KeyboardEventListener
    @Override
    public boolean onKeyboardOpenMeasure(int keyboardHeight) {
        if (mRootView != null)
            mRootView.setPadding(mRootView.getPaddingLeft(), mRootView.getPaddingTop(), mRootView.getPaddingRight(), keyboardHeight);
        return true;
    }

    @Override
    public boolean onKeyboardCloseMeasure(int keyboardHeight) {
        if (mRootView != null)
            mRootView.setPadding(mRootView.getPaddingLeft(), mRootView.getPaddingTop(), mRootView.getPaddingRight(), 0);
        return true;
    }
    //endregion

    //region MultiSelectedItemsPanel.OnArrowsClickListener
    @Override
    public void onExpandArrowClick() {
        if (mSelectedItemsView == null) return;
        mSelectedItemsView.enableRollUpArrow(true);
        mPresenter.onExpandButtonClicked();
    }

    @Override
    public void onRollUpArrowClick() {
        mPresenter.onRollUpButtonClicked();
        scrollToStart();
    }
    //endregion

    //region SearchableView
    @Override
    public void hideControls() {
        mPresenter.onKeyboardClosed(true);
    }

    @Override
    public void showControls() {
        //ignore
    }
    //endregion

    @Override
    public void onProgressDialogDismiss() {
        //ignore
    }

    @Override
    public void onProgressDialogCancel() {
        mPresenter.finishSelection(false);
    }

    //FIXME https://online.sbis.ru/opendoc.html?guid=7b0b4c87-5c5a-41d9-a565-66f40683b1ad
    protected boolean needToRemoveAllViews() {
        return true;
    }

}