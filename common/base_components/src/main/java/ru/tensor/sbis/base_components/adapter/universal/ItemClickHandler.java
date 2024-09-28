package ru.tensor.sbis.base_components.adapter.universal;

import androidx.annotation.NonNull;

/**
 * SelfDocumented
 * @author am.boldinov
 */
public interface ItemClickHandler<T extends UniversalBindingItem> {

    void onItemClick(@NonNull T item);
}
