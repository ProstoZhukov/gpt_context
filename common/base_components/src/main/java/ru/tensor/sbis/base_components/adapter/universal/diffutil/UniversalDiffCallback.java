package ru.tensor.sbis.base_components.adapter.universal.diffutil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import ru.tensor.sbis.base_components.adapter.universal.ObservableFieldsRebindHandler;
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem;
import ru.tensor.sbis.common.util.CommonUtils;

/**
 * SelfDocumented
 * @author sa.nikitin, am.boldinov
 */
public class UniversalDiffCallback<T extends UniversalBindingItem> extends DiffUtil.Callback {

    @NonNull
    private final List<T> mOldList;
    @NonNull
    private final List<T> mNewList;
    @Nullable
    private final ObservableFieldsRebindHandler<T> mObservableFieldsRebindHandler;

    public UniversalDiffCallback(@NonNull List<T> oldList, @NonNull List<T> newList) {
        this(oldList, newList, null);
    }

    public UniversalDiffCallback(@NonNull List<T> oldList, @NonNull List<T> newList,
                                 @Nullable ObservableFieldsRebindHandler<T> observableFieldsRebindHandler) {
        mOldList = oldList;
        mNewList = newList;
        mObservableFieldsRebindHandler = observableFieldsRebindHandler;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final T oldItem = getOldItem(oldItemPosition);
        final T newItem = getNewItem(newItemPosition);
        final boolean equals = oldItem.equals(newItem);
        if (equals) {
            if (needRebindOldItemToNewContent()) {
                mNewList.set(newItemPosition, oldItem);
            }
            if (mObservableFieldsRebindHandler != null) {
                mObservableFieldsRebindHandler.rebind(oldItem, newItem);
            }
        }
        return equals;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return CommonUtils.equal(getOldItem(oldItemPosition).getItemTypeId(),
                getNewItem(newItemPosition).getItemTypeId());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        final T oldItem = getOldItem(oldItemPosition);
        final T newItem = getNewItem(newItemPosition);
        return oldItem.getChangePayload(newItem);
    }

    // TODO сделать по умолчанию true по задаче  https://online.sbis.ru/opendoc.html?guid=8bb53e5a-accd-491b-be82-461596203db0, написать утилиту для поиска ошибочных айтемов
    /**
     * Вызывается в случае если старый и новый item имеют одинаковый контент
     *
     * @return true если необходимо положить ссылку на старый item в новый список
     */
    protected boolean needRebindOldItemToNewContent() {
        return false;
    }

    @NonNull
    protected T getOldItem(int position) {
        return mOldList.get(position);
    }

    @NonNull
    protected T getNewItem(int position) {
        return mNewList.get(position);
    }
}
