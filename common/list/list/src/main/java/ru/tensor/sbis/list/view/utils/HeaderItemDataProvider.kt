package ru.tensor.sbis.list.view.utils

/**
 * Интерфейс, который предоставляет данные об элементе в самом верху списка. Эти данные будут переданы в медиатор шапки списка
 * @author ma.kolpakov
 */
interface HeaderItemDataProvider {
    fun provideData(): Any
}