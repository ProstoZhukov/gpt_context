package ru.tensor.sbis.mvp.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.HashMap;
import java.util.List;

import ru.tensor.sbis.common.generated.CommandStatus;

/**
 * Обертка данных с информацией о пагинации
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"JavaDoc", "CanBeFinal", "unused", "RedundantSuppression"})
public class PagedListResult<DATA> {

    protected List<DATA> mDataList;
    protected CommandStatus mStatus;
    protected boolean mHasMore;
    protected boolean mFullyCached;
    protected HashMap<String, String> mMetadata;

    public PagedListResult(@NonNull List<DATA> dataList,
                           boolean hasMore,
                           HashMap<String, String> metadata) {
        this(dataList, null, hasMore);
        mMetadata = metadata;
    }

    public PagedListResult(@NonNull List<DATA> dataList,
                           boolean hasMore) {
        this(dataList, null, hasMore);
    }

    public PagedListResult(@NonNull List<DATA> dataList,
                           @Nullable CommandStatus status,
                           boolean hasMore) {
        this(dataList, status, hasMore, true);
    }

    public PagedListResult(@NonNull List<DATA> dataList,
                           @Nullable CommandStatus status,
                           boolean hasMore,
                           boolean fullyCached) {
        mDataList = dataList;
        mStatus = status;
        mHasMore = hasMore;
        mFullyCached = fullyCached;
    }

    /**
     * @SelfDocumented
     */
    @NonNull
    public List<DATA> getDataList() {
        return mDataList;
    }

    /**
     * @SelfDocumented
     */
    public void setDataList(@NonNull List<DATA> dataList) {
        mDataList = dataList;
    }

    /**
     * @SelfDocumented
     */
    public CommandStatus getCommandStatus() {
        return mStatus;
    }

    /**
     * @SelfDocumented
     */
    public boolean hasMore() {
        return mHasMore;
    }

    /**
     * @SelfDocumented
     */
    public void setHasMore(boolean hasMore) {
        mHasMore = hasMore;
    }

    /**
     * @SelfDocumented
     */
    public boolean isFullyCached() {
        return mFullyCached;
    }

    /**
     * @SelfDocumented
     */
    public HashMap<String, String> getMetaData() {
        return mMetadata;
    }

    /**
     * @SelfDocumented
     */
    public void addItem(@Nullable DATA item) {
        mDataList.add(item);
    }

    /**
     * @SelfDocumented
     */
    public void addItems(@NonNull List<? extends DATA> items) {
        mDataList.addAll(items);
    }

    /**
     * @SelfDocumented
     */
    @SuppressWarnings("rawtypes")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PagedListResult that = (PagedListResult) o;

        return new EqualsBuilder()
                .append(mDataList, that.mDataList)
                .append(mStatus, that.mStatus)
                .append(mHasMore, that.mHasMore)
                .append(mFullyCached, that.mFullyCached)
                .append(mMetadata, that.mMetadata)
                .build();
    }
}
