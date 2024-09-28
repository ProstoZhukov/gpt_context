package ru.tensor.sbis.list.view.calback

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.SbisList

/**
 * Определение доступности действий, таких как свайп или перетаскивание, над элементом списка в момент начало жеста.
 * @property sectionsHolder SectionsHolder поставляет данные о блоке ячейки и ее опции доступности перемещения.
 * @property itemMoveCallback ItemMoveCallback? определяет, доступно ли действие над ячейкой.
 * Если не задан(null), то действие не доступно.
 */
internal class ItemTouchCallback(private val sectionsHolder: ListDataHolder) :
    ItemTouchHelper.Callback() {

    var itemMoveCallback: ItemMoveCallback? = null

    private var targetPos: Int = -1
    private var lastAction: Int = ACTION_STATE_IDLE

    override fun getMovementFlags(p0: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(
            if (sectionsHolder.isMovable(viewHolder.adapterPosition)) ItemTouchHelper.UP or ItemTouchHelper.DOWN else 0,
            0
        )
    }

    /**
     * Если колбек [itemMoveCallback] задан, получаем из него результат, можно ли двигать элемент сбислиста.
     * Если можно двигать, начинаем реордеринг заданных элементов и уведомляем адаптер о передвижении элемента.
     */
    override fun onMove(
        p0: RecyclerView,
        p1: RecyclerView.ViewHolder,
        p2: RecyclerView.ViewHolder
    ): Boolean {
        return if (itemMoveCallback == null) return false
        else {
            val p1Pos = p1.adapterPosition
            val p2Pos = p2.adapterPosition
            val isMovingEnabled = itemMoveCallback!!.move(
                p1Pos,
                p2Pos
            )
            if (isMovingEnabled) {
                (p0 as SbisList).reorder(p1Pos, p2Pos)
                p0.adapter?.notifyItemMoved(p1Pos, p2Pos)
            }
            targetPos = p2Pos
            isMovingEnabled
        }
    }

    /**
     * Реализация метода onSelectedChanged для обработки изменения drag состояния viewHolder при dragNDrop.
     * Если сейчас drag состояние стало Idle, а было Drag, значит dragNDrop завершился, и можно оповестить
     * слушателей.
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {

        fun isStartDragging() : Boolean = actionState == ACTION_STATE_DRAG && lastAction == ACTION_STATE_IDLE

        fun isStopDragging() : Boolean = actionState == ACTION_STATE_IDLE && lastAction == ACTION_STATE_DRAG

        if (isStartDragging()) {
            viewHolder?.adapterPosition?.let { itemMoveCallback?.onStartDrag(it) }
        } else if (isStopDragging()) {
            if (targetPos != -1) {
                itemMoveCallback?.onDrop(targetPos)
            }
        }
        lastAction = actionState
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) = Unit
}