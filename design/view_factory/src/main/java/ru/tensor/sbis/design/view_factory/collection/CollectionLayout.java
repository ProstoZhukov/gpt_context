package ru.tensor.sbis.design.view_factory.collection;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * Интерфейс для макета, отображающего коллекцию элементов со счетчиком оставшихся,
 * который делегирует свое построение другому объекту.
 * @param <T> - тип view - элемента коллекции
 *
 * @author am.boldinov
 */
public interface CollectionLayout<T> extends DelegatingLayout {

    /**
     * Добавить view в макет.
     * @param child                 - дочерний элемент для добавления в макет
     * @param index                 - позиция элемента
     * @param params                - параметры макета элемента
     * @param preventRequestLayout  - предотвратить вызов пересчета макета
     */
    void addChildInLayout(View child, int index, ViewGroup.LayoutParams params,
                          boolean preventRequestLayout);

    /**
     * Запросить экземпляр item view.
     */
    @NonNull
    T takeItemView();

    /**
     * Вернуть экземпляр item view.
     */
    void releaseItemView(@NonNull T itemView);

    /**
     * Запросить экземпляр diff view.
     */
    @NonNull
    TextView takeDiffView();

    /**
     * Вернуть экземпляр diff view.
     */
    void releaseDiffView(@NonNull TextView diffView);

}
