package ru.tensor.sbis.mvp.multiselection;

import androidx.annotation.NonNull;

import io.reactivex.Observable;
import ru.tensor.sbis.mvp.data.model.PagedListResult;
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;
import ru.tensor.sbis.mvp.multiselection.data.SelectionFilter;

/**
 * Интерактор мульти выбора
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface MultiSelectionInteractor {

    /**
     * @SelfDocumented
     */
    Observable<PagedListResult<MultiSelectionItem>> searchItems(@NonNull SelectionFilter filter);

}
