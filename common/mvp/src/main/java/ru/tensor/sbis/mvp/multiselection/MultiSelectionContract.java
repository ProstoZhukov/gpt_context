package ru.tensor.sbis.mvp.multiselection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.List;
import java.util.Set;

import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationView;
import ru.tensor.sbis.mvp.search.SearchablePresenter;
import ru.tensor.sbis.mvp.search.SearchableView;

/**
 * Контракт мультивыбора
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("ALL")
public interface MultiSelectionContract {

    /**
     * Вью
     */
    interface View extends BaseTwoWayPaginationView<MultiSelectionItem>, SearchableView<MultiSelectionItem> {

        /**
         * @SelfDocumented
         */
        void updateDataList(@Nullable List<MultiSelectionItem> dataList, int offset, boolean isSelectedBlockExpanded);

        /**
         * @SelfDocumented
         */
        void onItemCheckedStateChanged(@NonNull MultiSelectionItem contact,
                                       int position,
                                       boolean checked,
                                       boolean removeCheckedItem);

        /**
         * @SelfDocumented
         */
        void addItemToSelectedPanel(@NonNull MultiSelectionItem contact);

        /**
         * @SelfDocumented
         */
        void removeItemFromSelectedPanel(@NonNull MultiSelectionItem contact);

        /**
         * @SelfDocumented
         */
        void setDoneButtonVisibility(boolean isVisible);

        /**
         * @SelfDocumented
         */
        void setDoneButtonClickable(boolean isClickable);

        /**
         * @SelfDocumented
         */
        void finishSelection();

        /**
         * @SelfDocumented
         */
        void showResultMaxCountRestriction(@StringRes int resultMaxCountRestrictionTextId, int maxResultCount);

        /**
         * @SelfDocumented
         */
        void showToast(@StringRes int message);

        /**
         * @SelfDocumented
         */
        void scrollToStart();

        /**
         * @SelfDocumented
         */
        void setCheckedItems(Set<MultiSelectionItem> checkedItems);

        /**
         * @SelfDocumented
         */
        void changeArrowButtonsVisibility(boolean isExpanded);

        /**
         * @SelfDocumented
         */
        void setRollUpArrowEnabled(boolean isEnabled);

        /**
         * @SelfDocumented
         */
        void showSavingProcess();

        /**
         * @SelfDocumented
         */
        void hideSavingProcess();

        /**
         * @SelfDocumented
         */
        void addContentToDataList(List<MultiSelectionItem> list);

        /**
         * @SelfDocumented
         */
        void showMessageInEmptyView(String message);

    }

    /**
     * Презентер
     */
    interface Presenter extends SearchablePresenter<MultiSelectionContract.View> {

        /**
         * @SelfDocumented
         */
        void finishSelection(boolean isSuccess);

        /**
         * @SelfDocumented
         */
        void onItemClicked(@NonNull MultiSelectionItem contact, int position, boolean isClickedOnItem);

        /**
         * @SelfDocumented
         */
        void onExpandButtonClicked();

        /**
         * @SelfDocumented
         */
        void onRollUpButtonClicked();

        /**
         * @SelfDocumented
         */
        boolean onBackButtonClicked();

        /**
         * @SelfDocumented
         */
        void switchDoneButton();

    }

}
