package ru.tensor.sbis.design.view_ext;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author am.boldinov
 */
@SuppressWarnings("JavaDoc")
public abstract class ListWithCounterViewManager<T> {

    @Nullable
    private List<T> mDataList;
    private int mCounter;

    /** @SelfDocumented */
    public void setDataList(@Nullable List<T> dataList) {
        setDataList(dataList, dataList != null ? dataList.size() : 0);
    }

    /** @SelfDocumented */
    public void setDataList(@Nullable List<T> dataList, int counter) {
        mDataList = dataList;
        mCounter = counter;
    }

    /** @SelfDocumented */
    @Nullable
    public List<T> getDataList() {
        return mDataList;
    }

    /** @SelfDocumented */
    public int getCounter() {
        return mCounter;
    }

    /** @SelfDocumented */
    public int getListSize() {
        return mDataList != null ? mDataList.size() : 0;
    }

    /** @SelfDocumented */
    public int getUnknownCount() {
        return mCounter - getListSize();
    }

    /** @SelfDocumented */
    @Nullable
    public T get(int index) {
        if (mDataList != null && index < mDataList.size()) {
            return mDataList.get(index);
        }
        return null;
    }

    @SuppressWarnings("EqualsReplaceableByObjectsCall")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListWithCounterViewManager<?> that = (ListWithCounterViewManager<?>) o;

        if (mCounter != that.mCounter) return false;
        return mDataList != null ? mDataList.equals(that.mDataList) : that.mDataList == null;
    }

    @Override
    public int hashCode() {
        int result = mDataList != null ? mDataList.hashCode() : 0;
        result = 31 * result + mCounter;
        return result;
    }
}
