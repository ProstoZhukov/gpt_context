package ru.tensor.sbis.base_components.adapter.universal;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.List;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.common.util.DeviceConfigurationUtils;
import ru.tensor.sbis.base_components.adapter.checkable.impl.AbstractCheckableListAdapter;
import ru.tensor.sbis.base_components.adapter.universal.diffutil.UniversalDiffCallback;
import ru.tensor.sbis.common.util.CommonUtils;

/**
 * SelfDocumented
 * @author sa.nikitin, am.boldinov
 */
@SuppressWarnings("rawtypes")
public abstract class UniversalBindingAdapter<
        T extends UniversalBindingItem,
        VH extends UniversalViewHolder>
        extends AbstractCheckableListAdapter<T, VH> {

    /** SelfDocumented */
    @SuppressWarnings("unused")
    protected boolean mIsTablet;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIsTablet = DeviceConfigurationUtils.isTablet(recyclerView.getContext());
    }

    /** SelfDocumented */
    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    /** SelfDocumented */
    @NonNull
    public static ViewDataBinding createBinding(@LayoutRes int layoutId, ViewGroup parent) {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.bind(getItem(position));
    }

    @Override
    protected boolean isMatching(@NonNull T src, @NonNull T item) {
        return CommonUtils.equal(src.getItemTypeId(), item.getItemTypeId());
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void setContent(@Nullable List<T> newContent) {
        if (CommonUtils.isEmpty(newContent)) {
            clearItems();
            return;
        }
        DiffUtil.Callback diffCallback = createDiffUtilCallback(newContent);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        setContent(newContent, diffResult);
    }

    /** SelfDocumented */
    public void updateItem(T updatedItem) {
        for (int i = 0; i < mContent.size(); i++) {
            if (CommonUtils.equals(updatedItem.getItemTypeId(), mContent.get(i).getItemTypeId())) {
                mContent.set(i, updatedItem);
                notifyContentChanged();
                notifyItemChanged(i);
                break;
            }
        }
    }

    /** SelfDocumented */
    public void setContent(@NonNull List<T> newContent, @NonNull DiffUtil.DiffResult diffResult) {
        setContent(newContent, diffResult, null);
    }

    /** SelfDocumented */
    protected void setContent(@NonNull List<T> newContent, @NonNull DiffUtil.DiffResult diffResult,
                              @Nullable ListUpdateCallback listUpdateCallback) {
        if (CommonUtils.isEmpty(newContent)) {
            clearItems();
            return;
        }
        updateList(newContent, diffResult, listUpdateCallback);
    }

    private void updateList(@NonNull List<T> newContent, @NonNull DiffUtil.DiffResult diffResult,
                            @Nullable ListUpdateCallback listUpdateCallback) {
        mContent = newContent;
        notifyContentChanged();
        if (listUpdateCallback != null) {
            diffResult.dispatchUpdatesTo(listUpdateCallback);
        } else {
            diffResult.dispatchUpdatesTo(this);
        }
        if (getCheckHelper() != null) {
            getCheckHelper().onContentChanged();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void addContent(@NonNull List<T> contentToAdd) {
        setContent(contentToAdd);
    }

    /** SelfDocumented */
    public void clearItems() {
        if (mSelectionHelper != null) {
            mSelectionHelper.resetSelection();
        }
        mContent.clear();
        notifyContentChanged();
        notifyDataSetChanged();
    }

    /** SelfDocumented */
    @NonNull
    protected DiffUtil.Callback createDiffUtilCallback(@NonNull List<T> newItems) {
        return new UniversalDiffCallback<>(mContent, newItems);
    }

    /** SelfDocumented */
    @SuppressWarnings("unused")
    public void removeByUuid(@NonNull String uuid) {
        for (int i = 0; i < mContent.size(); i++) {
            if (CommonUtils.equals(uuid, mContent.get(i).getItemTypeId())) {
                mContent.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
}
