package ru.tensor.sbis.mvp.layoutmanager.sticky

/**
 * Интерфейс, описывающий поведение объекта, ответственного за обновления sticky заголовка
 *
 * @author sa.nikitin
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface StickyHeaderUpdater<I : StickyHeaderInfo> {

    /**
     * Обновить информацию sticky заголовка
     *
     * @param stickyHeaderInfo Информация sticky заголовка
     */
    fun updateStickyHeader(stickyHeaderInfo: I)
}

/**
 * Пустая реализация интерфейса, описывающего обновление sticky заголовка.
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class StickyHeaderEmptyUpdater<I : StickyHeaderInfo> : StickyHeaderUpdater<I> {
    override fun updateStickyHeader(stickyHeaderInfo: I) {

    }
}