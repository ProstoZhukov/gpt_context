package ru.tensor.sbis.base_components.adapter.universal.pager;

import androidx.annotation.NonNull;

import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem;

/**
 * Интерфейс для перепривязки Observable компонентов
 *
 * @param <T> модель из которой необходимо взять значения для привязки в текущую модель
 *
 * @author am.boldinov
 */
public interface Rebindable<T extends UniversalBindingItem> {

    void rebind(@NonNull T t);

}
