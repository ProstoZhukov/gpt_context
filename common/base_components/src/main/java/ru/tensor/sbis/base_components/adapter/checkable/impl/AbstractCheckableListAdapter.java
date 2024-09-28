package ru.tensor.sbis.base_components.adapter.checkable.impl;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.base_components.adapter.AbstractSelectableListAdapter;
import ru.tensor.sbis.base_components.adapter.checkable.CheckHelper;
import ru.tensor.sbis.base_components.adapter.checkable.CheckableListAdapter;
import ru.tensor.sbis.base_components.adapter.checkable.CheckableViewHolder;
import ru.tensor.sbis.base_components.adapter.selectable.SelectableViewHolder;

/**
 * Base class for checkable list adapter.
 *
 * @param <T>  - type of items
 * @param <VH> - type of view holder
 *
 * @author am.boldinov
 */
@SuppressWarnings("unused")
@UiThread
public abstract class AbstractCheckableListAdapter<T, VH extends RecyclerView.ViewHolder & CheckableViewHolder & SelectableViewHolder>
        extends AbstractSelectableListAdapter<T, VH>
        implements CheckableListAdapter<T> {

    /**
     * Constant, signals that position not determined.
     */
    public static final int NO_POSITION = -1;

    /**
     * Object, delegates business logic of list.
     */
    @Nullable
    protected CheckHelper<T> mCheckHelper;

    /**
     * Attached recycler view. It's necessary to find and update
     * view holders according to new check state.
     */
    @Nullable
    protected RecyclerView mRecyclerView;

    @Nullable
    protected CheckHelper<T> getCheckHelper() {
        return mCheckHelper;
    }

    @Override
    public void onAttachedToRecyclerView(@SuppressWarnings("NullableProblems") RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@SuppressWarnings("NullableProblems") RecyclerView recyclerView) {
        mRecyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * ==============================================
     * [ Helper attaching ]
     * ==============================================
     */

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void attachHelper(@NonNull CheckHelper<T> helper) {
        if (mCheckHelper != helper) {
            boolean hasChecks = mCheckHelper != null && mCheckHelper.getCheckedCount() > 0;
            if (hasChecks || helper.getCheckedCount() > 0) {
                notifyDataSetChanged();
            }
            mCheckHelper = helper;
        }
    }

    @Override
    public void detachHelper(@NonNull CheckHelper<T> helper) {
        if (mCheckHelper == helper) {
            mCheckHelper = null;
        }
    }

    /**
     * Delegates binding to abstract method with known check state.
     */
    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.updateCheckState(isChecked(position), false);
    }

    @Override
    public void setContent(List<T> newContent) {
        setContent(newContent, true);
    }

    @CallSuper
    @Override
    public void setContent(List<T> newContent, boolean notifyDataSetChanged) {
        super.setContent(newContent, notifyDataSetChanged);
        if (mCheckHelper != null) {
            mCheckHelper.onContentChanged();
        }
    }

    @CallSuper
    @Override
    public void addContent(@NonNull List<T> contentToAdd) {
        super.addContent(contentToAdd);
        if (mCheckHelper != null) {
            mCheckHelper.onContentChanged();
        }
    }

    /* ==============================================
                [ Drive checks methods ]
      ============================================== */

    /**
     * Mark item on specified position as checked/unchecked.
     *
     * @param position - position to change state
     * @param checked  - state
     */
    public void setChecked(int position, boolean checked) {
        if (mCheckHelper != null) {
            mCheckHelper.setChecked(getItem(position), checked);
        }
    }

    /**
     * Mark specified item as checked/unchecked.
     *
     * @param item    - item to change state
     * @param checked - state
     */
    public void setChecked(T item, boolean checked) {
        if (mCheckHelper != null) {
            mCheckHelper.setChecked(item, checked);
        }
    }

    /**
     * Returns check-state of item on specified position.
     *
     * @param position - position to check state
     * @return check state of item
     */
    public boolean isChecked(int position) {
        if (mCheckHelper != null) {
            final T item = getItem(position);
            if (item != null) {
                return mCheckHelper.isChecked(item);
            }
        }
        return false;
    }

    /**
     * Mark all items as unchecked.
     */
    public void clearChecks() {
        if (mCheckHelper != null) {
            mCheckHelper.clearChecks();
        }
    }

    /**
     * Mark all presented items as checked.
     */
    public void checkAll() {
        if (mCheckHelper != null) {
            mCheckHelper.checkAll();
        }
    }

    /**
     * Invert checks of all presented items.
     */
    public void invertAll() {
        if (mCheckHelper != null) {
            mCheckHelper.invertAll();
        }
    }

    /**
     * ==============================================
     * [ Helper callbacks ]
     * ==============================================
     */

    @Override
    public void onChecked(@NonNull T item, boolean checked) {
        int position = getPositionForItem(item);
        notifyItemCheckedStateChanged(position, checked);
    }

    @Override
    public void onClearChecks() {
        applyToVisible(false);
    }

    @Override
    public void onCheckAll() {
        applyToVisible(true);
    }

    @Override
    public void onInvertCheckAll() {
        if (mCheckHelper != null) {
            final int count = getItemCount();
            for (int pos = 0; pos < count; pos++) {
                T item = getItem(pos);
                if (item != null) {
                    notifyItemCheckedStateChanged(pos, !mCheckHelper.isChecked(item));
                }
            }
        }
    }

    /**
     * Apply specified state to all visible items.
     *
     * @param checked - state to apply
     */
    protected final void applyToVisible(boolean checked) {
        if (mCheckHelper != null) {
            final int count = getItemCount();
            for (int pos = 0; pos < count; pos++) {
                T item = getItem(pos);
                if (item != null && mCheckHelper.isChecked(item) != checked) {
                    notifyItemCheckedStateChanged(pos, checked);
                }
            }
        }
    }

    /**
     * Update state of just attached view holder.
     *
     * @param holder - attached holder
     */
    @CallSuper
    @Override
    public void onViewAttachedToWindow(@NotNull VH holder) {
        super.onViewAttachedToWindow(holder);
        final int position = holder.getBindingAdapterPosition();
        holder.updateCheckState(isChecked(position), false);
    }

    /**
     * Returns list of presented checked items.
     *
     * @return list of checked items
     */
    @NonNull
    @Override
    public List<T> getChecked() {
        return getItemsWithSpecifiedState(true);
    }

    /**
     * Returns list of presented unchecked items.
     *
     * @return list of unchecked items
     */
    @NonNull
    @Override
    public List<T> getUnchecked() {
        return getItemsWithSpecifiedState(false);
    }

    /**
     * Returns count of checked items.
     *
     * @return count of checked items
     */
    public int getCheckedCount() {
        if (mCheckHelper != null) {
            return mCheckHelper.getCheckedCount();
        }
        return 0;
    }

    @NonNull
    private List<T> getItemsWithSpecifiedState(boolean checked) {
        if (mCheckHelper != null) {
            List<T> filtered = new ArrayList<>();
            for (int i = getFirstItemPosition(); i < getItemCount(); ++i) {
                T item = getItem(i);
                if (item != null && mCheckHelper.isChecked(item) == checked) {
                    filtered.add(item);
                }
            }
            return filtered;
        }
        return Collections.emptyList();
    }

    /** SelfDocumented */
    protected int getFirstItemPosition() {
        return 0;
    }

    /* ==============================================
                 [ Notify mechanism ]
      ============================================== */

    /**
     * Notify view holder for item on specified position about changing check-state.
     *
     * @param position - position to update state
     * @param checked  - state
     */
    protected void notifyItemCheckedStateChanged(int position, boolean checked) {
        if (position != NO_POSITION) {
            if (mRecyclerView != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
                if (holder instanceof CheckableViewHolder) {
                    ((CheckableViewHolder) holder).updateCheckState(checked, true);
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showCheckMode() {
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void hideCheckMode() {
        notifyDataSetChanged();
    }

}
