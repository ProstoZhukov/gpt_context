package ru.tensor.sbis.mvp_extensions.view_state;

/**
 * Статус пустого представления
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"unused", "RedundantSuppression"})
public enum EmptyViewState {
    EMPTY,
    DEFAULT,
    EMPTY_SEARCH_RESULT,
    ERROR,
    NO_INTERNET_CONNECTION
}
