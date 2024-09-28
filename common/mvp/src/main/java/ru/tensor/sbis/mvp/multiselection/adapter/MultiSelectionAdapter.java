package ru.tensor.sbis.mvp.multiselection.adapter;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import kotlin.Unit;
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder;
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter;
import ru.tensor.sbis.base_components.adapter.OnItemClickListener;
import ru.tensor.sbis.mvp.multiselection.MultiSelectionItemClickListener;
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;
import timber.log.Timber;

/**
 * Legacy-код
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"JavaDoc", "FieldMayBeFinal", "CanBeFinal", "rawtypes", "unused", "RedundantSuppression"})
public class MultiSelectionAdapter extends BaseTwoWayPaginationAdapter<MultiSelectionItem> {

    private SparseArray<Class<? extends MultiSelectionItem>> itemTypes = new SparseArray<>();
    private SparseArray<Class<? extends MultiSelectionViewHolder>> holderClasses = new SparseArray<>();
    private SparseIntArray holderLayoutIds = new SparseIntArray();

    public static final String PAYLOAD_CHECKED = "payload_checked";

    @Nullable
    private MultiSelectionItemClickListener mListener;

    @Nullable
    private Set<MultiSelectionItem> mCheckedItems;

    private boolean mIsSingleChoice;

    protected boolean mForceShowDividers;

    public MultiSelectionAdapter() {
        setHasStableIds(true);
    }

    public MultiSelectionAdapter(boolean isSingleChoice) {
        this();
        mIsSingleChoice = isSingleChoice;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public AbstractViewHolder<MultiSelectionItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (holderClasses.indexOfKey(viewType) >= 0) {
            try {
                View view = LayoutInflater.from(parent.getContext()).inflate(holderLayoutIds.get(viewType), parent, false);
                MultiSelectionViewHolder holder;
                if (!mIsSingleChoice) {
                    holder = holderClasses.get(viewType)
                            .getConstructor(View.class)
                            .newInstance(view);
                } else {
                    holder = holderClasses.get(viewType)
                            .getConstructor(View.class, boolean.class)
                            .newInstance(view, mIsSingleChoice);
                }
                holder.setOnItemClickListener(mListener);
                return holder;
            } catch (Exception e) {
                Timber.e(e, "Failed to create MultiSelectionItemHolder %s", holderClasses.get(viewType));
            }
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder<MultiSelectionItem> holder, int position) {
        super.onBindViewHolder(holder, position);
        MultiSelectionItem item = getItem(position);
        if (item == null) {
            return;
        }
        item.setIsChecked(mCheckedItems != null && mCheckedItems.contains(item));
        MultiSelectionViewHolder multiSelectionViewHolder = (MultiSelectionViewHolder) holder;
        multiSelectionViewHolder.bind(item);
        multiSelectionViewHolder.updateCheckState();
        multiSelectionViewHolder.showSeparator(mForceShowDividers || position < mOffset + mContent.size() - 1);
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder<MultiSelectionItem> holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.contains(PAYLOAD_CHECKED)) {
            ((MultiSelectionViewHolder) holder).updateCheckState();
        }
    }

    @Override
    public void setContent(List<MultiSelectionItem> newContent, boolean notifyDataSetChanged) {
        if (newContent == null) {
            newContent = Collections.emptyList();
        }
        MultiSelectionDiffCallback callback = new MultiSelectionDiffCallback(mContent, newContent);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        super.setContent(newContent, notifyDataSetChanged);
        diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
                //Элемент мог быть перемещён в секцию выбранных не по событию его выбора пользователем
                //onBindViewHolder для него вызван не был
                if (getCheckedCount() > 0 && toPosition <= getCheckedCount()) {
                    notifyItemChanged(toPosition, Unit.INSTANCE);
                }
                //LinearLayoutManager считает первый элемент anchor point.
                //Поэтому при его перемещении mRecyclerView проскроливается к toPosition.
                if (needToScrollToTop(fromPosition, toPosition)) {
                    mRecyclerView.scrollToPosition(0);
                }
            }

            @Override
            public void onChanged(int position, int count, @Nullable Object payload) {
                notifyItemRangeChanged(position, count, payload);
            }
        });
    }

    /**
     * @SelfDocumented
     */
    public void setCheckedItems(@Nullable Set<MultiSelectionItem> checkedItems) {
        mCheckedItems = checkedItems;
    }

    /**
     * Method to set a OnItemClickListener to adapter
     * <p>
     * NOTE: call this method before set adapter to list or call notifyDataSetChanged() after on your side
     *
     * @param listener OnItemClickListener - listener to list cell callback
     * @see OnItemClickListener
     */
    public void setOnItemClickListener(@Nullable MultiSelectionItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public long getItemId(int position) {
        if (!hasStableIds()) {
            return NO_POSITION;
        } else {
            MultiSelectionItem item = getItem(position);
            return item == null ? NO_POSITION : item.getUUID().getLeastSignificantBits();
        }
    }

    @Override
    protected int getItemType(@Nullable MultiSelectionItem dataModel) {
        if (dataModel == null) {
            return HOLDER_EMPTY;
        } else {
            int itemType = generateItemType(dataModel.getClass());
            holderClasses.put(itemType, dataModel.getViewHolderClass());
            holderLayoutIds.put(itemType, dataModel.getHolderLayoutResId());
            //TODO: ??? throw exception if getViewHolderClass() == null, getHolderLayoutResId() == 0
            return itemType;
        }
    }

    /**
     * Нужно ли подскроллить список в самый верх при перестроении списка в результате работы DiffUtil
     *
     * @param fromPosition с какой позиции сместилась ячейка
     * @param toPosition   на какую позицию сместилась ячейка
     * @return true - нужно подскроллить список в самый верх
     */
    protected boolean needToScrollToTop(int fromPosition, int toPosition) {
        return mRecyclerView != null && fromPosition <= 1;
    }

    @Override
    public int getCheckedCount() {
        if (mCheckedItems != null) {
            return mCheckedItems.size();
        }
        return 0;
    }

    private int generateItemType(Class<? extends MultiSelectionItem> itemClass) {
        int itemType = itemTypes.indexOfValue(itemClass);
        if (itemType < 0) {
            itemType = itemTypes.size();
            itemTypes.put(itemType, itemClass);
        }
        return itemType;
    }


}
