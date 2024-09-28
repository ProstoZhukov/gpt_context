package ru.tensor.sbis.base_components;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

/**
 * Коллбек для работы со списками в Diff Util
 */
public abstract class BaseDiffCallback<T> extends DiffUtil.Callback {

    @Nullable
    protected final List<T> mOldList;
    @Nullable
    protected final List<T> mNewList;

    public BaseDiffCallback(@Nullable List<T> oldList,
                            @Nullable List<T> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList == null ? 0 : mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList == null ? 0 : mNewList.size();
    }

    @SuppressWarnings({"ConstantConditions", "RedundantSuppression"})
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (mOldList == null || mNewList == null) {
            return false;
        }
        final T oldItem = mOldList.get(oldItemPosition);
        final T newItem = mNewList.get(newItemPosition);
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if (mOldList == null || mNewList == null) {
            return false;
        }
        final T oldItem = mOldList.get(oldItemPosition);
        final T newItem = mNewList.get(newItemPosition);
        return areItemsTheSame(oldItem, newItem);
    }

    protected abstract boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem);

}