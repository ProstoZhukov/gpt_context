package ru.tensor.sbis.list.view

import ru.tensor.sbis.list.view.item.AnyItem

interface SelectionManager {
    /**
     * Снять выделение с элемента списка.
     */
    fun cleanSelection()

    /**
     * Установить режим, при котором нажатый элемент будет оставаться выделенным и отобразится индикатор выделения
     * с края элемента. Доступность нажатия на элемент определяется опциями
     * элемента см. [ru.tensor.sbis.list.view.item.Options]
     */
    fun highlightSelection()

    /**
     * Подсветить конкретный элемент в списке
     * @param position Int
     */
    fun highlightItem(position: Int)

    /**
     * Подсветить элемент в списке, первый соответствующий положительному результату сравнения [predicate]
     */
    fun highlightItem(predicate: (AnyItem) -> Boolean)
}