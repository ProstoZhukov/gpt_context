package ru.tensor.sbis.mvp.search;

import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationView;

/**
 * Legacy-код
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface SearchableView<DM> extends BaseTwoWayPaginationView<DM> {

    /**
     * @SelfDocumented
     */
    void showCursorInFiltersPanel();

    /**
     * @SelfDocumented
     */
    void clearSearchQuery();

    /**
     * @SelfDocumented
     */
    void hideCursorFromSearch();

    /**
     * @SelfDocumented
     */
    void hideKeyboard();

    /**
     * @SelfDocumented
     */
    void showKeyboard();

    /**
     * @SelfDocumented
     */
    void enableFolders();

    /**
     * @SelfDocumented
     */
    void disableFolders();

    /**
     * @SelfDocumented
     */
    void enableFilters();

    /**
     * @SelfDocumented
     */
    void disableFilters();

}
