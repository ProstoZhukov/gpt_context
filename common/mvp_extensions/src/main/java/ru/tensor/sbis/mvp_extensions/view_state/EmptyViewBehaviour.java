package ru.tensor.sbis.mvp_extensions.view_state;

import androidx.annotation.NonNull;

/**
 * Поведение пустого представления
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public interface EmptyViewBehaviour {

    /**
     * Обновить статус
     */
    void updateEmptyViewState(@NonNull EmptyViewState emptyViewState);
}
