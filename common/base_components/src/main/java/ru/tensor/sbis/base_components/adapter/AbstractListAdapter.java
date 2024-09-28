package ru.tensor.sbis.base_components.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author am.boldinov
 */
@SuppressWarnings("unused")
@UiThread
public abstract class AbstractListAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private final List<ContentChangedObserver> mObservers = new ArrayList<>();

    protected List<T> mContent = new ArrayList<>();

    @Override
    @CallSuper
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        if (observer instanceof ContentChangedObserver) {
            mObservers.add((ContentChangedObserver) observer);
        }
    }

    @Override
    @CallSuper
    public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        if (observer instanceof ContentChangedObserver) {
            mObservers.remove(observer);
        }
    }

    /**
     * Уведомить наблюдателей об изменении содержимого. Данный метод должен
     * быть вызван после каждого изменения коллекции {@link #mContent}.
     */
    public final void notifyContentChanged() {
        for (ContentChangedObserver observer : mObservers) {
            observer.onContentChanged(this);
        }
    }

    public void setContent(@Nullable List<T> newContent) {
        setContent(newContent, true);
    }

    public void setContent(@Nullable List<T> newContent, boolean notifyDataSetChanged) {
        mContent = newContent;
        notifyContentChanged();
        if (notifyDataSetChanged) {
            notifyDataSetChanged();
        }
    }

    public void addContent(@NonNull List<T> contentToAdd) {
        if (mContent != null) {
            int oldItemCount = mContent.size();
            mContent.addAll(contentToAdd);
            notifyContentChanged();
            notifyItemRangeInserted(oldItemCount, contentToAdd.size());
        } else {
            setContent(contentToAdd);
        }
    }

    public void insertItem(int position, @NonNull T item) {
        mContent.add(position, item);
        notifyContentChanged();
        notifyItemInserted(position);
        int nextItemPosition = position + 1;
        if (nextItemPosition < getItemCount()) {
            notifyItemChanged(nextItemPosition);
        }
    }

    public int insertOrUpdateItem(int position, @NonNull T item) {
        int indexOfItem = mContent.indexOf(item);
        if (indexOfItem < 0) {
            mContent.add(position, item);
            notifyContentChanged();
            notifyItemInserted(position);
            int nextItemPosition = position + 1;
            if (nextItemPosition < getItemCount()) {
                notifyItemChanged(nextItemPosition);
            }
            indexOfItem = position;
        } else {
            mContent.remove(item);
            mContent.add(position, item);
            notifyContentChanged();
            notifyItemInserted(indexOfItem);
            notifyItemRangeChanged(indexOfItem + 1, mContent.size());
        }
        return indexOfItem;
    }

    public void insertItems(int position, @NonNull List<T> itemsToInsert) {
        mContent.addAll(position, itemsToInsert);
        notifyContentChanged();
        notifyItemRangeInserted(position, itemsToInsert.size());
    }

    public void updateItem(int position, @NonNull T item) {
        mContent.set(position, item);
        notifyContentChanged();
        notifyItemChanged(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        T item = mContent.remove(fromPosition);
        mContent.add(toPosition, item);
        notifyContentChanged();
        notifyItemMoved(fromPosition, toPosition);
    }

    public void removeItem(@NonNull T item) {
        int indexOfItem = mContent.indexOf(item);
        if (indexOfItem >= 0) {
            mContent.remove(item);
            notifyContentChanged();
            notifyItemRemoved(indexOfItem);
        }
    }

    public void removeItems(int position, @NonNull List<T> itemsToRemove) {
        mContent.removeAll(itemsToRemove);
        notifyContentChanged();
        notifyItemRangeRemoved(position, itemsToRemove.size());
    }

    public void removeItem(int position, @NonNull T item) {
        int indexOfItem = mContent.indexOf(item);
        if (indexOfItem >= 0) {
            if (indexOfItem == position) {
                mContent.remove(position);
                notifyContentChanged();
                notifyItemRemoved(position);
            } else {
                mContent.remove(item);
                notifyContentChanged();
                notifyItemRemoved(indexOfItem);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mContent != null ? mContent.size() : 0;
    }

    @NonNull
    public List<T> getContent() {
        return mContent != null ? mContent : Collections.emptyList();
    }

    public T getItem(int position) {
        return mContent.get(position);
    }

}
