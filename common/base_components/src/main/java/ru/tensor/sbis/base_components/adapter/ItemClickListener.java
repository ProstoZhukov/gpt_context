package ru.tensor.sbis.base_components.adapter;

/**
 * Интерфейс обработчика клика по элементу списка.
 *
 * @author am.boldinov
 */
public interface ItemClickListener {

    /**
     * Обработка нажатия указанного элемента списка.
     * @param position - позиция элемента в списке
     */
    void onItemClick(int position);
}
