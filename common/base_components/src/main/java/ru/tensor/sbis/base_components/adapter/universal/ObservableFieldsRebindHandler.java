package ru.tensor.sbis.base_components.adapter.universal;

import androidx.annotation.NonNull;

/**
 * SelfDocumented
 * @author sa.nikitin, am.boldinov
 */
public interface ObservableFieldsRebindHandler<T extends UniversalBindingItem> {

    /**
     * Заменить наблюдаемые поля нового элемента наблюдаемыми полями старого элемента
     * для сохранения привязки модели данных к разметке
     * @param oldItem элемент старого списка
     * @param newItem элемент нового списка
     */
    void rebind(@NonNull T oldItem, @NonNull T newItem);
}
