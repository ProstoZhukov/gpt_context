package ru.tensor.sbis.mvp.multiselection.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import ru.tensor.sbis.common.util.UUIDUtils;
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;

/**
 * Legacy-код
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public class MultiSelectionDiffCallback extends DiffUtil.Callback {

    @Nullable
    private final List<MultiSelectionItem> mOldList;
    @Nullable
    private final List<MultiSelectionItem> mNewList;

    public MultiSelectionDiffCallback(@Nullable List<MultiSelectionItem> mOldList,
                                      @Nullable List<MultiSelectionItem> mNewList) {
        this.mOldList = mOldList;
        this.mNewList = mNewList;
    }

    @Override
    public int getOldListSize() {
        return mOldList == null ? 0 : mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList == null ? 0 : mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return !(mOldList == null || mNewList == null) &&
                UUIDUtils.equals(mOldList.get(oldItemPosition).getUUID(),
                        mNewList.get(newItemPosition).getUUID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (mOldList == null || mNewList == null) {
            return false;
        }

        MultiSelectionItem oldItem = mOldList.get(oldItemPosition);
        MultiSelectionItem newItem = mNewList.get(newItemPosition);

        return oldItem.isChecked() == newItem.isChecked() && oldItem.hasTheSameContent(newItem);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        if (mOldList == null || mNewList == null) {
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
        final MultiSelectionItem oldItem = mOldList.get(oldItemPosition);
        final MultiSelectionItem newItem = mNewList.get(newItemPosition);
        return oldItem.getChangePayload(newItem);
    }
}
