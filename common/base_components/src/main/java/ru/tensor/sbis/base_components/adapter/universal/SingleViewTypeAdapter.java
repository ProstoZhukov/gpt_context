package ru.tensor.sbis.base_components.adapter.universal;

import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

/**
 * SelfDocumented
 * @author sa.nikitin
 */
@SuppressWarnings("rawtypes")
public class SingleViewTypeAdapter<T extends UniversalBindingItem, CH> extends UniversalBindingAdapter<T, UniversalViewHolder> {

    @LayoutRes
    private final int mItemLayoutResId;
    private int mClickHandlerVariableId;
    @Nullable
    private CH mClickHandler;

    public SingleViewTypeAdapter(@LayoutRes int itemLayoutResId) {
        this(itemLayoutResId, 0, null);
    }

    public SingleViewTypeAdapter(@LayoutRes int itemLayoutResId,
                                 int clickHandlerVariableId, @Nullable CH clickHandler) {
        mItemLayoutResId = itemLayoutResId;
        mClickHandlerVariableId = clickHandlerVariableId;
        mClickHandler = clickHandler;
    }

    @SuppressWarnings("unused")
    public void setClickHandlerVariableId(int clickHandlerVariableId) {
        mClickHandlerVariableId = clickHandlerVariableId;
    }

    /** SelfDocumented */
    public void setClickHandler(@Nullable CH clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public @NotNull UniversalViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new UniversalViewHolder<>(createBinding(mItemLayoutResId, parent),
                mClickHandlerVariableId, mClickHandler);
    }
}
