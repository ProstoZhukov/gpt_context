package ru.tensor.sbis.mvp.interactor.crudinterface.filter;

import androidx.annotation.NonNull;

/**
 * Обертка над фильтром списка
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public abstract class AnchorPositionQueryBuilder<ANCHOR, QUERY> extends ListFilter.Builder<ANCHOR, QUERY> {

    protected int mFromPosition;

    /**
     * @SelfDocumented
     */
    @SuppressWarnings("rawtypes")
    @NonNull
    public ListFilter.Builder from(int position) {
        mFromPosition = position;
        return this;
    }

}
