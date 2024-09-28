package ru.tensor.sbis.base_components.adapter.universal;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import ru.tensor.sbis.design.view_ext.swipereveallayout.updated.BindingViewBinderHelper;

/**
 * SelfDocumented
 * @author am.boldinov
 */
@SuppressWarnings({"rawtypes", "unused"})
public abstract class UniversalBindingSwipeAdapter<T extends UniversalBindingItem, VH extends UniversalViewHolder>
        extends UniversalBindingAdapter<T, VH> {

    @NonNull
    protected final BindingViewBinderHelper<String> mSwipeHelper = new BindingViewBinderHelper<>();
    protected boolean mStateRestored;

    @Override
    public void onSavedInstanceState(@NonNull Bundle outState) {
        super.onSavedInstanceState(outState);
        mSwipeHelper.saveStates(outState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mStateRestored = true;
        mSwipeHelper.restoreStates(savedInstanceState);
    }

    protected abstract boolean isSwipeableViewHolder(int holderViewType);

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (isSwipeableViewHolder(holder.getItemViewType())) {
            ((UniversalBindingSwipeHolder) holder).bindSwipeHelper(getItem(position));
            ((UniversalBindingSwipeHolder) holder).closeIfNeed();
        }
        super.onBindViewHolder(holder, position);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onViewDetachedFromWindow(@NotNull VH holder) {
        super.onViewDetachedFromWindow(holder);
        if (isSwipeableViewHolder(holder.getItemViewType())
                && ((UniversalBindingSwipeHolder) holder).isOpenedSwipeRevealLayout()) {
            ((UniversalBindingSwipeHolder) holder).closeIfNeed();
        }
    }

    @Override
    protected void setContent(@NonNull List<T> newContent, @NonNull DiffUtil.DiffResult diffResult,
                              @Nullable ListUpdateCallback listUpdateCallback) {
        if (mStateRestored) {
            mStateRestored = false;
        } else {
            mSwipeHelper.closeAll();
        }
        super.setContent(newContent, diffResult, listUpdateCallback);
    }

    @Override
    public void clearItems() {
        super.clearItems();
        mSwipeHelper.closeAll();
    }

    @SuppressWarnings("unused")
    protected void closeIfNeedAfterUpdating(int position) {
        T item = getItem(position);
        if (item != null) {
            mSwipeHelper.closeLayout(item.getItemTypeId());
        }
    }
}
