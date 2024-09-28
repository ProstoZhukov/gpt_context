package ru.tensor.sbis.mvp.layoutmanager.sticky

/**
 * Интерфейс поставщика информации о заголовке элемента списка по его позиции
 *
 * @author sa.nikitin
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface StickyHeaderInfoProvider<I : StickyHeaderInfo> {

    /**
     * Метод возвращает информацию о заголовке элемента списка по его позиции
     *
     * @param position позиция элемента списка
     *
     * @return информация о заголовке элемента списка
     */
    fun getStickyHeaderInfo(position: Int): I?
}