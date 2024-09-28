package ru.tensor.sbis.base_components.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.UiThread;
import ru.tensor.sbis.base_components.R;
import ru.tensor.sbis.base_components.adapter.checkable.impl.AbstractCheckableListAdapter;
import ru.tensor.sbis.design.theme.global_variables.InlineHeight;

/**
 * AbstractTwoWayPaginationAdapter is common adapter for two way pagination.
 *
 * @param <DM> - DataModel
 *
 * @author am.boldinov
 */
@UiThread
public abstract class BaseTwoWayPaginationAdapter<DM>
        extends AbstractCheckableListAdapter<DM, AbstractViewHolder<DM>> {

    public static final int HOLDER_EMPTY = -3;
    public static final int HOLDER_PROGRESS = -4;
    public static final int HOLDER_BOTTOM_PADDING = -5;

    protected int mOffset;
    protected boolean mShowOlderLoadingProgress;
    protected boolean mShowNewerLoadingProgress;
    protected boolean mWithBottomEmptyHolder = true;

    /**
     * There is always one additional item (either {@link #HOLDER_PROGRESS} or
     * {@link #HOLDER_BOTTOM_PADDING} depend on {@link #mShowOlderLoadingProgress} flag).
     * See {@link #getItemViewType(int)} method for details.
     */
    @Override
    public int getItemCount() {
        return !getContent().isEmpty()
                ? getContent().size() + mOffset + (mShowOlderLoadingProgress || mWithBottomEmptyHolder ? 1 : 0)
                : 0;
    }

    @Override
    public int getItemViewType(int position) {
        int itemPosition = position - mOffset;
        if ((itemPosition == -1 && mShowNewerLoadingProgress) || itemPosition == mContent.size()) {
            if (itemPosition == -1 || mShowOlderLoadingProgress) {
                return HOLDER_PROGRESS;
            } else {
                return HOLDER_BOTTOM_PADDING;
            }
        } else {
            return getItemType(getItem(position));
        }
    }

    /**
     * @param dataList - List of DM, that should be shown in RecyclerView
     * @param offset   - count of "erased from list" elements from 0 adapter position to first item in {@link #mContent}
     */
    public void setData(@Nullable List<DM> dataList, int offset) {
        mOffset = offset;
        setContent(dataList, true);
    }

    public void setDataWithoutNotify(@Nullable List<DM> dataList, int offset) {
        mOffset = offset;
        if (dataList != null) {
            setContent(dataList, false);
        } else {
            setContent(null, true);
        }
    }

    /**
     * @param showOlderLoadingProgress - true to show progress ViewHolder after real data, false to hide it
     */
    public void showOlderLoadingProgress(boolean showOlderLoadingProgress) {
        final boolean previousShowProgress = mShowOlderLoadingProgress;
        mShowOlderLoadingProgress = showOlderLoadingProgress;
        if (previousShowProgress != showOlderLoadingProgress) {
            int progressItemPosition = getContent().size() + mOffset;
            if (!mWithBottomEmptyHolder) {
                if (!showOlderLoadingProgress) {
                    notifyItemRemoved(progressItemPosition);
                } else {
                    notifyItemInserted(progressItemPosition);
                }
            } else {
                notifyItemChanged(progressItemPosition);
            }
        }
    }

    public void showNewerLoadingProgress(boolean showNewerLoadingProgress) {
        final boolean previousShowProgress = mShowNewerLoadingProgress;
        mShowNewerLoadingProgress = showNewerLoadingProgress;
        if (previousShowProgress != showNewerLoadingProgress) {
            notifyItemChanged(mOffset);
        }
    }

    /**
     * @param position - adapter position
     * @return DM for position or null if position not valid
     */
    @Nullable
    @Override
    public DM getItem(int position) {
        int itemPosition = position - mOffset;
        return itemPosition >= 0 && itemPosition < getContent().size() ? mContent.get(itemPosition) : null;
    }

    @Override
    protected int getFirstItemPosition() {
        return mOffset;
    }

    @SuppressWarnings("unused")
    public void removeItem(int position) {
        final int itemPosition = position - mOffset;
        if (itemPosition >= 0 && itemPosition < getContent().size()) {
            getContent().remove(itemPosition);
        }
    }

    @Override
    protected boolean isItemSelectable(int holderType) {
        return holderType != HOLDER_EMPTY && holderType != HOLDER_PROGRESS && holderType != HOLDER_BOTTOM_PADDING;
    }

    /**
     * Override this method if you want empty ViewHolder different from default (idk why would you want it)
     *
     * @param parent same parent as in {@link #onCreateViewHolder(ViewGroup, int)}
     * @return empty ViewHolder for your list
     */
    @NonNull
    private AbstractViewHolder<DM> createEmptyViewHolder(@NonNull ViewGroup parent) {
        return provideEmptyAbstractViewHolder(parent, 300);
    }

    /**
     * Override this method if you want progress ViewHolder different from default
     *
     * @param parent same parent as in {@link #onCreateViewHolder(ViewGroup, int)}
     * @return progress ViewHolder for your list
     */
    @NonNull
    protected AbstractViewHolder<DM> createProgressViewHolder(@NonNull ViewGroup parent) {
        return new AbstractViewHolder<>(LayoutInflater.from(parent.getContext()).inflate(R.layout.base_components_progress_list_item, parent, false));
    }

    /**
     * Override this method if you want empty ViewHolder alike bottom padding different from default
     * This empty ViewHolder is needed to escape some problems with list and bottom navigation
     *
     * @param parent same parent as in {@link #onCreateViewHolder(ViewGroup, int)}
     * @return empty ViewHolder alike bottom padding for your list
     */
    @NonNull
    protected AbstractViewHolder<DM> createBottomPaddingHolder(@NonNull ViewGroup parent) {
        return provideEmptyAbstractViewHolder(parent, getHolderBottomPaddingHeight(parent.getContext()));
    }

    /**
     * Creates and returns ViewHolder for concrete viewType.
     * Calling super is strongly recommended to support empty and progress ViewHolders
     *
     * @param parent   - parent View
     * @param viewType - type that was calculated by method {@link #getItemViewType(int)}
     * @return ViewHolder to display
     */
    @NonNull
    @Override
    public AbstractViewHolder<DM> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HOLDER_EMPTY:
                return createEmptyViewHolder(parent);

            case HOLDER_PROGRESS:
                return createProgressViewHolder(parent);

            case HOLDER_BOTTOM_PADDING:
                return createBottomPaddingHolder(parent);

            default:
                throw new IllegalArgumentException("Invalid viewType");
        }
    }

    @Override
    public void onViewRecycled(@NonNull AbstractViewHolder<DM> holder) {
        super.onViewRecycled(holder);
        holder.recycle();
    }

    @Override
    protected int getPositionForItem(@NonNull DM item) {
        int position = super.getPositionForItem(item);
        return position != NO_POSITION ? position + mOffset : NO_POSITION;
    }

    /**
     * Provides a new instance of {@link AbstractViewHolder} class with the specified height.
     *
     * @param parent {@link ViewGroup} to get {@link android.content.Context} instance
     * @param height a fixed size in pixels
     */
    private AbstractViewHolder<DM> provideEmptyAbstractViewHolder(@NonNull ViewGroup parent, int height) {
        View item = new View(parent.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        item.setLayoutParams(params);
        return new AbstractViewHolder<>(item);
    }

    /**
     * @param dataModel - object from dataList that should be used to evaluate concrete itemViewType
     * @return itemViewType for concrete dataModel
     */
    protected abstract int getItemType(@Nullable DM dataModel);

    /**
     * Возвращать размер холдера для нижнего отсупа списка.
     * По-умолчанию возвращается размер нижней навигационной панели.
     * Переопределение может потребоваться, если вместо ННП используются другая View или группа.
     *
     * @param context контекст
     * @return значение размера в px
     */
    @Px
    protected int getHolderBottomPaddingHeight(@NonNull Context context) {
        return InlineHeight.XL.getDimenPx(context);
    }
}
