package ru.tensor.sbis.mvp.presenter;

import androidx.annotation.StringRes;

import ru.tensor.sbis.mvp.multiselection.MultiSelectionContract;

/**
 * Legacy-код
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface BaseTwoWayPaginationView<DM> extends TwoWayAdapterDispatcher<DM>, BaseLoadingView, DisplayErrorDelegate {

    /**
     * @SelfDocumented
     */
    void updateListViewState();

    /**
     * @SelfDocumented
     */
    void hideInformationView();

    /**
     * @see MultiSelectionContract.View#showMessageInEmptyView(String)
     */
    void showMessageInEmptyView(@StringRes int messageTextId);

    /**
     * @SelfDocumented
     */
    void ignoreProgress(boolean ignore);

    /**
     * @SelfDocumented
     */
    void showMessageInEmptyView(@StringRes int messageTextId, @StringRes int detailTextId);

    /**
     * @SelfDocumented
     */
    void scrollToPosition(int position);

    /**
     * @SelfDocumented
     */
    void showOlderLoadingProgress(boolean show);

    /**
     * @SelfDocumented
     */
    void showNewerLoadingProgress(boolean show);

    /**
     * @SelfDocumented
     */
    void showControls();

    /**
     * @SelfDocumented
     */
    void hideControls();

    /**
     * @SelfDocumented
     */
    void resetUiState();

}
