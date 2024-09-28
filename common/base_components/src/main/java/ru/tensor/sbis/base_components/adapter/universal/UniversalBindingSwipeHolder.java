package ru.tensor.sbis.base_components.adapter.universal;

import android.util.SparseArray;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import ru.tensor.sbis.design.view_ext.swipereveallayout.updated.UpdatedSwipeHolderInterface;
import ru.tensor.sbis.design.view_ext.swipereveallayout.updated.UpdatedSwipeRevealLayout;
import ru.tensor.sbis.design.view_ext.swipereveallayout.updated.UpdatedViewBinderHelper;

/**
 * @deprecated использовать {@link ru.tensor.sbis.base_components.adapter.universal.swipe.UniversalSwipeableHolder}
 *
 * @author am.boldinov
 */
@SuppressWarnings("ALL")
public abstract class UniversalBindingSwipeHolder<DM extends UniversalBindingItem>
        extends UniversalViewHolder<DM>
        implements UpdatedSwipeHolderInterface {

    @NonNull
    private final UpdatedSwipeRevealLayout mRevealLayout;
    @Nullable
    private final UpdatedViewBinderHelper<String> mViewBinderHelper;

    protected UniversalBindingSwipeHolder(@NonNull ViewDataBinding binding, @Nullable UpdatedViewBinderHelper<String> viewBinderHelper) {
        this(binding, viewBinderHelper, 0, null);
    }

    protected UniversalBindingSwipeHolder(@NonNull ViewDataBinding binding,
                                          @Nullable UpdatedViewBinderHelper<String> viewBinderHelper,
                                          int clickHandlerVariableId,
                                          @Nullable Object clickHandler) {
        super(binding, clickHandlerVariableId, clickHandler);
        mRevealLayout = itemView.findViewById(getRevealLayoutId());
        mViewBinderHelper = viewBinderHelper;
    }

    public UniversalBindingSwipeHolder(@NonNull ViewDataBinding binding,
                                       @Nullable SparseArray<Object> variables,
                                       @Nullable UpdatedViewBinderHelper<String> viewBinderHelper) {
        super(binding, variables);
        mRevealLayout = itemView.findViewById(getRevealLayoutId());
        mViewBinderHelper = viewBinderHelper;
    }

    @IdRes
    protected abstract int getRevealLayoutId();

    @NonNull
    @Override
    public UpdatedSwipeRevealLayout getSwipeRevealLayout() {
        return mRevealLayout;
    }

    public void closeIfNeed() {
        mRevealLayout.close(false);
    }

    public boolean isOpenedSwipeRevealLayout() {
        return mRevealLayout.isOpened();
    }

    public void bindSwipeHelper(DM dataModel) {
        if (mViewBinderHelper != null) {
            mViewBinderHelper.bind(mRevealLayout, dataModel.getItemTypeId());
        }
    }

}
