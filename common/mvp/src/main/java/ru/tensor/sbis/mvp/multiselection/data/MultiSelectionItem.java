package ru.tensor.sbis.mvp.multiselection.data;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

import kotlin.Unit;
import ru.tensor.sbis.mvp.multiselection.adapter.MultiSelectionViewHolder;

/**
 * Legacy-код
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface MultiSelectionItem {

    /**
     * @SelfDocumented
     */
    int getItemCount();

    /**
     * @SelfDocumented
     */
    UUID getUUID();

    /**
     * @SelfDocumented
     */
    boolean isChecked();

    /**
     * @SelfDocumented
     */
    void setIsChecked(boolean isChecked);

    /**
     * @SelfDocumented
     */
    @SuppressWarnings("rawtypes")
    @NonNull
    Class<? extends MultiSelectionViewHolder> getViewHolderClass();

    /**
     * @SelfDocumented
     */
    @LayoutRes
    int getHolderLayoutResId();

    /**
     * @SelfDocumented
     */
    @Nullable
    String getItemType();

    @Nullable
    default Object getChangePayload(Object newItem) {
        if (supportChangeAnimation(newItem)) {
            return null;
        } else {
            return Unit.INSTANCE;
        }
    }

    @SuppressWarnings("SameReturnValue")
    default boolean supportChangeAnimation(Object other) {
        return true;
    }

    default boolean hasTheSameContent(Object other) {
        return equals(other);
    }
}
