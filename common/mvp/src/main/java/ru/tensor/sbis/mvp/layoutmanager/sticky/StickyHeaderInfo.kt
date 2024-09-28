package ru.tensor.sbis.mvp.layoutmanager.sticky

/**
 * Интерфейс sticky заголовка, используется в [StickyHeaderLayoutManager]
 *
 * @author sa.nikitin
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface StickyHeaderInfo {

    /**
     * Метод, предоставляющий признак содержательности заголовка
     *
     * @return true, если заголовок не пуст, false - иначе
     */
    fun isNotEmpty(): Boolean
}