package ru.tensor.sbis.richtext.view;

import android.os.Trace;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.design.collection_view.CollectionView;
import ru.tensor.sbis.richtext.span.view.BaseAttributesVM;
import ru.tensor.sbis.richtext.span.view.CollectionAttributesVM;
import ru.tensor.sbis.richtext.span.view.ViewStubSpan;

/**
 * Adapter для дочерних View внутри {@link RichViewLayout}
 *
 * @author am.boldinov
 */
class RichViewAdapter extends CollectionView.Adapter<RichViewLayout.ViewHolder<BaseAttributesVM>> {

    @Nullable
    private ViewStubSpan[] mData;
    @NonNull
    private final SparseArray<RichViewLayout.ViewHolderFactory> mViewHolderFactoryStore;
    @NonNull
    private final ShareViewClickListener mShareViewClickListener;
    @NonNull
    private final RichViewLayout.ViewHolder.ItemClickListener mItemClickListener = new RichViewLayout.ViewHolder.ItemClickListener() {
        @Override
        public void onItemClick(int position, @NonNull View view) {
            final BaseAttributesVM vm = getItem(position);
            if (vm.getClickableSpan() != null) {
                vm.getClickableSpan().onClick(view);
            } else {
                mShareViewClickListener.onViewClick(vm, position);
            }
        }

        @Override
        public void onListItemClick(int position, int serialIndex, @NonNull View view) {
            final BaseAttributesVM vm = ((CollectionAttributesVM<?>) getItem(position)).getAttributesAt(serialIndex);
            if (vm.getClickableSpan() != null) {
                vm.getClickableSpan().onClick(view);
            } else {
                mShareViewClickListener.onViewClick(vm, position + serialIndex);
            }
        }
    };

    RichViewAdapter() {
        mShareViewClickListener = new ShareViewClickListener();
        mViewHolderFactoryStore = new SparseArray<>();
    }

    @SuppressWarnings("CopyConstructorMissesField")
    RichViewAdapter(@NonNull RichViewAdapter parent) {
        mShareViewClickListener = parent.mShareViewClickListener;
        mViewHolderFactoryStore = parent.mViewHolderFactoryStore;
        setRecycledViewPool(parent.getRecycledViewPool());
    }

    /**
     * RichTextView по умолчанию лежит в layout и не учитывается в операциях адаптера
     */
    @Override
    protected final int getChildStartOffset() {
        return 1;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    protected final RichViewLayout.ViewHolder<BaseAttributesVM> onCreateViewHolder(@NonNull CollectionView parent, int viewType) {
        Trace.beginSection("RichLayout onCreateViewHolder");
        final RichViewLayout.ViewHolder<BaseAttributesVM> viewHolder = mViewHolderFactoryStore.get(viewType)
                .createViewHolder((RichViewParent) parent);
        Trace.endSection();
        return viewHolder;
    }

    @Override
    protected final void onBindViewHolder(@NonNull RichViewLayout.ViewHolder<BaseAttributesVM> holder, int position) {
        Trace.beginSection("RichLayout onBindViewHolder");
        holder.setOnItemClickListener(mItemClickListener);
        holder.bind(getItem(position));
        Trace.endSection();
    }

    @Override
    protected void onRecycleViewHolder(@NonNull RichViewLayout.ViewHolder<BaseAttributesVM> holder) {
        super.onRecycleViewHolder(holder);
        holder.setOnItemClickListener(null);
    }

    @Override
    protected final int getItemViewType(int position) {
        final BaseAttributesVM vm = getItem(position);
        final int viewType = vm.getViewType();
        if (mViewHolderFactoryStore.get(viewType) == null) {
            mViewHolderFactoryStore.append(viewType, vm.createViewHolderFactory());
        }
        return viewType;
    }

    @Override
    public final int getItemCount() {
        return mData != null ? mData.length : 0;
    }

    /**
     * Возвращает модель данных, которая находится на соответствующей позиции
     *
     * @param position позиция элемента в списке адаптера
     */
    @NonNull
    public final BaseAttributesVM getItem(int position) {
        return getItemInternal(position).getAttributes();
    }

    /**
     * Устанавливает новые данные в адаптер для перепривязки дочерних View
     */
    public final void setData(@Nullable ViewStubSpan[] data) {
        mData = data;
        notifyDataChanged();
    }

    /**
     * Устанавливает слушатель на клик по дочерним View внутри {@link RichViewLayout}
     */
    final void setSingleViewClickListener(@Nullable RichViewLayout.SingleViewClickListener listener) {
        mShareViewClickListener.mOriginalClickListener = listener;
    }

    @NonNull
    final ViewStubSpan getItemInternal(int position) {
        if (mData == null || position < 0 || position >= mData.length) {
            throw new IndexOutOfBoundsException("Attempt to get item for position " + position);
        }
        return mData[position];
    }

    private static final class ShareViewClickListener implements RichViewLayout.SingleViewClickListener {

        @Nullable
        private RichViewLayout.SingleViewClickListener mOriginalClickListener;

        @Override
        public void onViewClick(@NonNull BaseAttributesVM item, int serialIndex) {
            if (mOriginalClickListener != null) {
                mOriginalClickListener.onViewClick(item, serialIndex);
            }
        }
    }
}
