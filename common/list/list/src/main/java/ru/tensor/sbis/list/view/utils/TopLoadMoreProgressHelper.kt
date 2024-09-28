package ru.tensor.sbis.list.view.utils

import ru.tensor.sbis.list.view.adapter.SbisAdapter
import ru.tensor.sbis.list.view.utils.ProgressItemPlace.RIGHT
import ru.tensor.sbis.list.view.utils.ProgressItemPlace.TOP

/**
 * Вспомогательный класс для показа индикатора прогресса постраничной подгрузки сверху списка.
 * Показ индикатора будет выполнен за счет добавления элемента с индикатором прогресса в начало списка.
 */
internal class TopLoadMoreProgressHelper(
    var isHorizontal: Boolean = false,
    private val itemProgressTop: ProgressItem = ProgressItem(TOP),
    private val itemProgressRight: ProgressItem = ProgressItem(RIGHT)
) {

    private var _adapter: SbisAdapter? = null

    /**
     * Элемент для отображения прогресса виден.
     */
    private var added = false

    fun isAdded() = added

    /**
     * Установить адаптер, в который будет добавляться/удаляться элемент с индикатором прогресса.
     * @param adapter SbisAdapter
     */
    fun setAdapter(adapter: SbisAdapter) {
        _adapter = adapter
    }

    /**
     * Показывать или нет индикатор.
     * @param has Boolean
     */
    fun hasLoadMore(has: Boolean) {
        _adapter?.apply {
            if (has) {
                if (!added) {
                    addFirst(getItemProgress())
                    added = true
                }

            } else {
                removeFirst(getItemProgress())
                added = false
            }
        }
    }

    private fun getItemProgress() = if (isHorizontal) itemProgressRight else itemProgressTop
}