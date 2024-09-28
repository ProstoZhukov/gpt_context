package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Реализует логику стандарта:
 *  - Если на экране показано начало реестра, то все строки плавно сдвигаются вниз, а затем появляются новые (с анимацией - fade)
 *  - Если реестр прокручен вниз (мы не видим начало реестра), строки добавляются вверх таблицы без анимации и без сдвига.
 *  Новые строки мы увидим, когда прокрутим реестр вверх.
 * Опционально, поддержано требования модуля Задач: скрол к добавленному элементу.
 */
internal class ScrollerToPosition : RecyclerView.AdapterDataObserver() {

    private lateinit var list: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private var firstVisibleItemPositionBeforeUpdate = 0

    /**
     * Нужно ли прокручивать список к первому из добавленных. Требуется в Задачах и в списке логов.
     */
    var moveToAdded = false

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        scrollIfNeed(positionStart, itemCount)
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        scrollIfNeed(fromPosition, itemCount)
    }

    private fun scrollIfNeed(positionStart: Int, itemCount: Int) {
        when {
            moveToAdded && itemCount == 1 -> {
                list.scrollToPosition(positionStart)
            }
            firstVisibleItemPositionBeforeUpdate == 0 -> {
//                list.scrollToPosition(0)
                // TODO: 19.01.21 https://online.sbis.ru/opendoc.html?guid=9e5ca78a-1ab1-4710-ac59-09753dbaf7af
            }
        }
    }

    fun setListAndLayoutManager(list: RecyclerView, layoutManager: LinearLayoutManager) {
        this.list = list
        this.layoutManager = layoutManager
    }

    fun rememberFirstVisibleItemPosition() {
        firstVisibleItemPositionBeforeUpdate =
            layoutManager.findFirstCompletelyVisibleItemPosition()
    }
}