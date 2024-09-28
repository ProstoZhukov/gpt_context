package ru.tensor.sbis.base_components.adapter.universal;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder;
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter;
import ru.tensor.sbis.base_components.adapter.universal.diffutil.UniversalDiffCallback;
import ru.tensor.sbis.common.util.UUIDUtils;

/**
 * Адаптер с двусторонней пагинацией
 * @author am.boldinov
 */
public class UniversalTwoWayPaginationAdapter<DM extends UniversalBindingItem>
        extends BaseTwoWayPaginationAdapter<DM> {

    @NonNull
    private final ListUpdateCallback mListUpdateCallback = new VirtualListUpdateCallback();
    private boolean mSupportMoveChanges = true;

    @Override
    protected int getItemType(@Nullable DM dataModel) {
        if (dataModel != null) {
            return dataModel.getViewType();
        } else {
            return HOLDER_EMPTY;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder<DM> holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.bind(getItem(position));
    }

    @Override
    public void setContent(List<DM> newContent, boolean notifyDataSetChanged) {
        if (notifyDataSetChanged) {
            if (newContent == null) {
                newContent = Collections.emptyList();
            }
            UniversalDiffCallback<DM> callback = createDiffCallback(newContent);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
            super.setContent(newContent, false);
            diffResult.dispatchUpdatesTo(mListUpdateCallback);
        } else {
            super.setContent(newContent, false);
        }
    }

    public final void showNewerLoadingProgressWithOffset(boolean showNewerLoadingProgress) {
        final boolean previousShowProgress = mShowNewerLoadingProgress;
        mShowNewerLoadingProgress = showNewerLoadingProgress;
        if (previousShowProgress != mShowNewerLoadingProgress) {
            if (mShowNewerLoadingProgress) {
                mOffset++;
                notifyItemInserted(mOffset - 1);
            } else {
                mOffset = Math.max(0, --mOffset);
                notifyItemRemoved(mOffset);
            }
        }
    }

    public final void setSupportMoveChanges(boolean supportMoveChanges) {
        mSupportMoveChanges = supportMoveChanges;
    }

    @NonNull
    protected UniversalDiffCallback<DM> createDiffCallback(@NonNull List<DM> newContent) {
        return new UniversalDiffCallback<DM>(mContent, newContent, getObservableFieldsRebindHandler()) {
            @Override
            protected boolean needRebindOldItemToNewContent() {
                return true;
            }
        };
    }

    @NonNull
    protected static ViewDataBinding createBinding(@LayoutRes int layoutId, ViewGroup parent) {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false);
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    protected static ViewDataBinding createBinding(@NonNull View view) {
        return DataBindingUtil.bind(view);
    }

    @Nullable
    protected ObservableFieldsRebindHandler<DM> getObservableFieldsRebindHandler() {
        return null;
    }

    private class VirtualListUpdateCallback implements ListUpdateCallback {

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(mOffset + position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(mOffset + position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            if (mSupportMoveChanges) {
                notifyItemMoved(mOffset + fromPosition, mOffset + toPosition);
            } else {
                onChanged(fromPosition, 1, null);
                onChanged(toPosition, 1, null);
            }
        }

        @Override
        public void onChanged(int position, int count, @Nullable Object payload) {
            notifyItemRangeChanged(mOffset + position, count, payload);
        }
    }

    protected static class UniversalBindingStubItem extends UniversalBindingItem {

        private static final int VIEW_TYPE = Integer.MIN_VALUE;

        public UniversalBindingStubItem() {
            super(UUIDUtils.NIL_UUID.toString());
        }

        @Override
        public int getViewType() {
            return VIEW_TYPE;
        }

        @NonNull
        @Override
        protected SparseArray<Object> createBindingVariables() {
            return new SparseArray<>(0);
        }
    }

}
