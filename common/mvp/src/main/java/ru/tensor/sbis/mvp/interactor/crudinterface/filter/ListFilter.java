package ru.tensor.sbis.mvp.interactor.crudinterface.filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.common.generated.QueryDirection;

/**
 * Модель фильтра для списка
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public abstract class ListFilter {

    // TODO переместить сюда часть полей презентера
    protected String mSearchQuery;

    /**
     * @SelfDocumented
     */
    public void setSearchQuery(String searchQuery) {
        mSearchQuery = searchQuery;
    }

    /**
     * @SelfDocumented
     */
    @SuppressWarnings("rawtypes")
    @NonNull
    public abstract Builder queryBuilder();

    /**
     * Билдер для создания модели фильтра
     */
    public static abstract class Builder<ANCHOR, QUERY> {

        @Nullable
        protected ANCHOR mAnchorModel;
        protected boolean mFromPullRefresh;
        protected int mItemsCount;
        protected QueryDirection mDirection;
        protected boolean mInclusive;
        @Nullable
        protected String mSearchQuery;

        /**
         * @SelfDocumented
         */
        @SuppressWarnings("rawtypes")
        @NonNull
        public Builder anchorModel(@Nullable ANCHOR anchorModel) {
            mAnchorModel = anchorModel;
            return this;
        }

        /**
         * @SelfDocumented
         */
        @SuppressWarnings("rawtypes")
        @NonNull
        public Builder itemsCount(int itemsCount) {
            mItemsCount = itemsCount;
            return this;
        }

        /**
         * @SelfDocumented
         */
        @SuppressWarnings("rawtypes")
        @NonNull
        public Builder fromPullRefresh(boolean fromPullRefresh) {
            mFromPullRefresh = fromPullRefresh;
            return this;
        }

        /**
         * @SelfDocumented
         */
        @SuppressWarnings("rawtypes")
        @NonNull
        public Builder direction(@Nullable QueryDirection direction) {
            mDirection = direction;
            return this;
        }

        /**
         * @SelfDocumented
         */
        @SuppressWarnings("rawtypes")
        @NonNull
        public Builder inclusive(boolean inclusive) {
            mInclusive = inclusive;
            return this;
        }

        /**
         * @SelfDocumented
         */
        @SuppressWarnings("rawtypes")
        @NonNull
        public Builder searchQuery(@Nullable String searchQuery) {
            mSearchQuery = searchQuery;
            return this;
        }

        /**
         * @SelfDocumented
         */
        @NonNull
        public abstract QUERY build();
    }
}
