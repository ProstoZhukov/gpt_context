package ru.tensor.sbis.list.view.calback

/**
 * Колбек выполненного жеста перемещения элемента в списке.
 */
interface ItemMoveCallback {

    /**
     * Событие об окончании перетаскивания viewHolder с места [previousIndex] на место [newIndex].
     * На этапе работы этого колбека, палец еще не отпустил перетаскиваемое view, и dragNDrop еще не завершился.
     * @return Boolean обработано ли перемещение. Если вернет false, то элемент вернется на прежнее место.
     */
    fun move(
        previousIndex: Int,
        newIndex: Int
    ): Boolean = false

    /**
     * Событие об окончании dragNDrop. Палец отпустил перетаскиваемый view, и он остается в [targetIndex] -
     * последний индекс, полученный из [move].
     */
    fun onDrop(
        targetIndex: Int
    ) = Unit

    /**
     * Палец начал перетягивание view, [draggableItemIndex] - его стартовая позиция в списке.
     */
    fun onStartDrag(
        draggableItemIndex: Int
    ) = Unit
}