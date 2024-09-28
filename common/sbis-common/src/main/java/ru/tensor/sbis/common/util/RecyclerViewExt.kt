@file:JvmName("RecyclerViewExt")

package ru.tensor.sbis.common.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View

/**
 * @author sa.nikitin
 */

/**
 * Функция, по позиции элемента определяющая, входит ли он целиком в текущую видимую область списка
 */
fun RecyclerView.isCompletelyVisibleItem(itemPosition: Int): Boolean =
    with(layoutManager) {
        when (this) {
            is LinearLayoutManager        ->
                itemPosition >= findFirstCompletelyVisibleItemPosition()
                        && itemPosition <= findLastCompletelyVisibleItemPosition()
            is StaggeredGridLayoutManager ->
                findFirstCompletelyVisibleItemPositions(null).find { itemPosition >= it } != null
                        && findLastCompletelyVisibleItemPositions(null).find { itemPosition <= it } != null
            else                                                       -> false
        }
    }

/**
 * Находит первый элемент в списке, у которого видно начало (либо единственный видимый)
 */
fun RecyclerView.findFirstBeginningVisibleOrSingleItemPosition(): Int =
    with(layoutManager) {
        when (this) {
            is LinearLayoutManager        ->
                // ищем первый полностью видимый элемент списка, если такой есть
                findFirstCompletelyVisibleItemPosition().let {
                    when {
                        it > RecyclerView.NO_POSITION -> it
                        // возможно, элемент списка слишком большой и не помещается полностью
                        // тогда выбираем последний видимый элемент (первый, у которого видно начало, либо единственный)
                        else                          -> findLastVisibleItemPosition()
                    }
                }
            is StaggeredGridLayoutManager -> {
                // ищем первый видимый элемент с минимальной позицией
                val minFirstVisible = findFirstVisibleItemPositions(null).minNonNegative() ?: RecyclerView.NO_POSITION
                // ищем последний видимый элемент с минимальной позицией
                val minLastVisible = findLastVisibleItemPositions(null).minNonNegative() ?: RecyclerView.NO_POSITION

                if (minFirstVisible == minLastVisible) {
                    // первый видимый элемент с минимальной позицией отображается во весь столбец/строку
                    minFirstVisible
                } else {
                    // ищем первый полностью видимый элемент с минимальной позицией, если такой есть
                    when (val firstCompletelyVisible = findFirstCompletelyVisibleItemPositions(null).minNonNegative()) {
                        // ни один элемент не помещается полностью
                        // выбираем последний видимый элемент с минимальной позицией (первый, у которого видно начало)
                        null -> minLastVisible
                        // выбираем минимальный элемент из первого полностью видимого и последнего видимого
                        else -> minLastVisible.coerceAtMost(firstCompletelyVisible)
                    }
                }
            }
            else -> throw UnsupportedOperationException("Unknown layout manager")
        }
    }

/**
 * Возвращает наименьший положительный элемент или null.
 */
private fun IntArray.minNonNegative(): Int? {
    if (isEmpty()) return null
    var min = if (this[0] >= 0) this[0] else null
    for (i in 1..lastIndex) {
        val e = this[i]
        if (e >= 0 && (min == null || min > e)) min = e
    }
    return min
}

/**
 * Находит первый полностью видимый элемент в списке
 */
fun RecyclerView.findFirstCompletelyVisibleItemPosition(): Int =
    with(layoutManager) {
        when (this) {
            is LinearLayoutManager        -> findFirstCompletelyVisibleItemPosition()
            is StaggeredGridLayoutManager -> findFirstCompletelyVisibleItemPositions(null).minOrNull() ?: RecyclerView.NO_POSITION
            else                                                       -> throw UnsupportedOperationException("Unknown layout manager")
        }
    }

/**
 * Находит первый частично видимый элемент в списке
 */
fun RecyclerView.findFirstVisibleItemPosition(): Int =
    with(layoutManager) {
        when (this) {
            is LinearLayoutManager        -> findFirstVisibleItemPosition()
            is StaggeredGridLayoutManager -> findFirstVisibleItemPositions(null).minOrNull() ?: RecyclerView.NO_POSITION
            else                                                       -> throw UnsupportedOperationException("Unknown layout manager")
        }
    }

/**
 * Находит последний частично видимый элемент в списке
 */
fun RecyclerView.findLastVisibleItemPosition(): Int =
    with(layoutManager) {
        when (this) {
            is LinearLayoutManager        -> findLastVisibleItemPosition()
            is StaggeredGridLayoutManager -> findLastVisibleItemPositions(null).maxOrNull() ?: RecyclerView.NO_POSITION
            else                                                       -> throw UnsupportedOperationException("Unknown layout manager")
        }
    }

/**
 * Находит последний полностью видимый элемент в списке
 */
fun RecyclerView.findLastCompletelyVisibleItemPosition(): Int =
    with(layoutManager) {
        when (this) {
            is LinearLayoutManager        -> findLastCompletelyVisibleItemPosition()
            is StaggeredGridLayoutManager -> findLastCompletelyVisibleItemPositions(null).maxOrNull() ?: RecyclerView.NO_POSITION
            else                          -> throw UnsupportedOperationException("Unknown layout manager")
        }
    }

/**
 * Присоединить SnapHelper с отслеживанием позиции к RecyclerView
 */
inline fun RecyclerView.attachSnapHelper(
    snapHelper: SnapHelper,
    crossinline onSnapItemChanged: ((newSnapPosition: Int, newSnapView: View) -> Unit)
) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener = object : SnapOnScrollListener(snapHelper) {
        override fun onSnapItemChanged(newSnapPosition: Int, newSnapView: View) {
            onSnapItemChanged(newSnapPosition, newSnapView)
        }
    }
    addOnScrollListener(snapOnScrollListener)
}

