package ru.tensor.sbis.list.view.utils

/**
 * Интерфейс, Медиатора который вызывается при скролле списка и предоставляет провайдер данных из самого верхнего элемента.
 * На нем можно реализовать логику прилипающего элемента с датой
 * @author ma.kolpakov
 */
interface HeaderItemMediator {
    fun updateWithData(dataProvider: HeaderItemDataProvider, canScroll: Boolean)
}