package ru.tensor.sbis.design.view_factory.collection;

/**
 * Интерфейс для макета, который делегирует свое построение другому объекту.
 *
 * @author am.boldinov
 */
public interface DelegatingLayout {

    /**
     * Задать размеры для макета.
     * @param measuredWidth     - ширина макета
     * @param measuredHeight    - высота макета
     */
    void setMeasuredDimensions(int measuredWidth, int measuredHeight);

}
