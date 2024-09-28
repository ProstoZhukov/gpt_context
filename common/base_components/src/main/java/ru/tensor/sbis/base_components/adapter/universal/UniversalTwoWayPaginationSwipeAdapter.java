package ru.tensor.sbis.base_components.adapter.universal;

import android.os.Bundle;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder;
import ru.tensor.sbis.design.view_ext.swipereveallayout.updated.BindingViewBinderHelper;

/**
 * @deprecated использовать {@link ru.tensor.sbis.base_components.adapter.universal.swipe.UniversalTwoWayPaginationSwipeableAdapter}
 *
 * @author am.boldinov
 */

@SuppressWarnings("deprecation")
public abstract class UniversalTwoWayPaginationSwipeAdapter<DM extends UniversalBindingItem> extends UniversalTwoWayPaginationAdapter<DM> {

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

    @CallSuper
    protected boolean isSwipeableViewHolder(int holderViewType) {
        return holderViewType != HOLDER_BOTTOM_PADDING && holderViewType != HOLDER_EMPTY && holderViewType != HOLDER_PROGRESS;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder<DM> holder, int position) {
        if (isSwipeableViewHolder(holder.getItemViewType())) {
            ((UniversalBindingSwipeHolder) holder).bindSwipeHelper(getItem(position));
            ((UniversalBindingSwipeHolder) holder).closeIfNeed();
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull AbstractViewHolder<DM> holder) {
        super.onViewDetachedFromWindow(holder);
        if (isSwipeableViewHolder(holder.getItemViewType())
                && ((UniversalBindingSwipeHolder) holder).isOpenedSwipeRevealLayout()) {
            ((UniversalBindingSwipeHolder) holder).closeIfNeed();
        }
    }

    @Override
    public void setContent(List<DM> newContent) {
        setContent(newContent, true);
    }

    @Override
    public void setContent(List<DM> newContent, boolean notifyDataSetChanged) {
        if (mStateRestored) {
            mStateRestored = false;
        } else {
            mSwipeHelper.closeAll();
        }
        super.setContent(newContent, notifyDataSetChanged);
    }
}
