package ru.tensor.sbis.base_components.adapter.universal.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

/**
 * SelfDocumented
 * @author sa.nikitin, am.boldinov
 */
public class DiffResultWrapper<T> {

    @NonNull
    private final T mListData;
    @NonNull
    private final DiffUtil.DiffResult mDiffResult;

    public DiffResultWrapper(@NonNull T listData, @NonNull DiffUtil.DiffResult diffResult) {
        mListData = listData;
        mDiffResult = diffResult;
    }

    @NonNull
    public T getListData() {
        return mListData;
    }

    @NonNull
    public DiffUtil.DiffResult getDiffResult() {
        return mDiffResult;
    }
}
