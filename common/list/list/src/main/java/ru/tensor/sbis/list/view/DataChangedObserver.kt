package ru.tensor.sbis.list.view

/**
 * Слушатель события вставки единственного элемента на первое место(нулевой индекс) списка.
 */
interface DataChangedObserver {
    /**
     * Вызывается когда прозошла вставка единственного элемента на первое место(нулевой индекс) списка.
     */
    fun onItemRangeInserted(positionStart: Int, itemCount: Int, provider: ItemVisibilityPositionProvider)

    interface ItemVisibilityPositionProvider {
        fun findFirstCompletelyVisibleItemPosition(): Int
        fun findLastCompletelyVisibleItemPosition(): Int
    }
}
