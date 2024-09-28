package ru.tensor.sbis.base_components.adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.common.data.model.base.BaseItem;

/**
 * Legacy-код
 * Created by kabramov on 17.07.2018.
 */
@SuppressWarnings({"rawtypes", "unused"})
public class BaseDelegateAdapter<DM extends BaseItem> extends BaseTwoWayPaginationAdapter<DM> {

    private final SparseArray<BaseViewHolderDelegate> delegateMap = new SparseArray<>();

    public void addDelegate(int itemType, @NonNull BaseViewHolderDelegate delegate) {
        delegateMap.append(itemType, delegate);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public ru.tensor.sbis.base_components.adapter.AbstractViewHolder<DM> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolderDelegate delegate = getDelegate(viewType);
        if (delegate != null) {
            return delegate.onCreateViewHolder(parent, viewType);
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder<DM> holder, int position) {
        super.onBindViewHolder(holder, position);
        BaseViewHolderDelegate delegate = getDelegate(holder.getItemViewType());
        DM item = getItem(position);
        if (delegate != null && item != null) {
            delegate.onBindViewHolder(holder, item);
        }
    }

    @Override
    protected int getItemType(@Nullable DM dataModel) {
        return dataModel != null ? dataModel.getType() : HOLDER_EMPTY;
    }

    @Nullable
    private BaseViewHolderDelegate getDelegate(int itemType) {
        return delegateMap.get(itemType);
    }

}
