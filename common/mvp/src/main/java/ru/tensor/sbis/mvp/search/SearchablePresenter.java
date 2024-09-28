package ru.tensor.sbis.mvp.search;

import androidx.annotation.NonNull;

import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationPresenter;

/**
 * Legacy-код
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface SearchablePresenter<V> extends BaseTwoWayPaginationPresenter<V> {

    /**
     * @SelfDocumented
     */
    void onSearchQueryChanged(@NonNull String searchQuery);

    /**
     * @SelfDocumented
     */
    void onSearchClearButtonClicked();

    /**
     * @SelfDocumented
     */
    void onSearchButtonClicked();

    /**
     * @SelfDocumented
     */
    void onFilterPanelFocusStateChanged(boolean hasFocus);

    /**
     * @SelfDocumented
     */
    void onKeyboardOpened(boolean force);

    /**
     * @SelfDocumented
     */
    void onKeyboardClosed(boolean force);

    /**
     * Устанавливает задержку при отображении прогрессбара загрузки данных
     *
     * @param delayMillis задержка в миллисекундах
     */
    void setProgressDelay(int delayMillis);

}
