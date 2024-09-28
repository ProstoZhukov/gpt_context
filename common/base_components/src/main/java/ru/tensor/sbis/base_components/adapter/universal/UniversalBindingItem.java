package ru.tensor.sbis.base_components.adapter.universal;

import android.util.SparseArray;

import org.jetbrains.annotations.Nullable;

import androidx.annotation.NonNull;
import kotlin.Unit;
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListItem;

/**
 * SelfDocumented
 * @author sa.nikitin, am.boldinov
 */
public abstract class UniversalBindingItem implements ListItem {

    @NonNull
    private final String mItemTypeId;
    @NonNull
    private final SparseArray<Object> mBindingVariables;

    public UniversalBindingItem(@NonNull String itemTypeId) {
        mItemTypeId = itemTypeId;
        mBindingVariables = createBindingVariables();
    }

    /** SelfDocumented */
    public abstract int getViewType();

    /**
     * Возвращает список (id перменной - переменная) связываемых с помощью data binding переменных.
     * Прокидывается в разметку через view holder
     *
     * @return SparseArray, где ключ - идентификатор BR переменной, а значение - объект переменной
     */
    @NonNull
    protected abstract SparseArray<Object> createBindingVariables();

    /** SelfDocumented */
    @NonNull
    public SparseArray<Object> getBindingVariables() {
        return mBindingVariables;
    }

    /** SelfDocumented */
    @Nullable
    public Object getChangePayload(@Nullable Object other) {
        if (supportChangeAnimation(other)) {
            return null;
        } else {
            return Unit.INSTANCE;
        }
    }

    /**
     * @return true если при изменении содержимого необходимо анимировать элемент списка, false иначе
     */
    protected boolean supportChangeAnimation(@Nullable Object other) {
        return true;
    }

    // region ListItem impl

    @NonNull
    @Override
    public String getItemTypeId() {
        return mItemTypeId;
    }

    @Nullable
    @Override
    public Boolean areContentsTheSame(@Nullable Object other) {
        return equals(other);
    }

    // endregion

}
