package ru.tensor.sbis.base_components.adapter.universal;

import android.util.SparseArray;

import androidx.annotation.NonNull;

/**
 * Вью модель разделителя
 *
 * @author am.boldinov
 */
public class DividerBindingItem extends UniversalBindingItem {

    public static final int VIEW_TYPE = Integer.MAX_VALUE - 2121345324;

    public DividerBindingItem(int serialNumber) {
        super(DividerBindingItem.class.getCanonicalName().concat(String.valueOf(serialNumber)));
    }

    @SuppressWarnings("unused")
    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @NonNull
    @Override
    protected SparseArray<Object> createBindingVariables() {
        return new SparseArray<>(0);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }
}
