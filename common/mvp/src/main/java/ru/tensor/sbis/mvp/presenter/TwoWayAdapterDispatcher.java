package ru.tensor.sbis.mvp.presenter;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Legacy-код
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface TwoWayAdapterDispatcher<DM> {

    /**
     * @SelfDocumented
     */
    void updateDataList(@Nullable List<DM> dataList, int offset);

    /**
     * @SelfDocumented
     */
    void updateDataListWithoutNotification(@Nullable List<DM> dataList, int offset);

    /**
     * @SelfDocumented
     */
    void notifyItemsInserted(int position, int count);

    /**
     * @SelfDocumented
     */
    void notifyItemsChanged(int position, int count);

    /**
     * @SelfDocumented
     */
    void notifyItemsChanged(int position, int count, @Nullable Object payload);

    /**
     * @SelfDocumented
     */
    void notifyItemsRemoved(int position, int count);

    /**
     * @SelfDocumented
     */
    void notifyDataSetChanged();
}
