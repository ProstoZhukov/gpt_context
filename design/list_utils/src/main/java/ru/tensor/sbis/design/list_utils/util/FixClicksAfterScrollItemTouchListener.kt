package ru.tensor.sbis.design.list_utils.util

import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView

/**
 * Предназначен для исправления срабатывания кликов после прокрутки списка только со второго раза, либо после ожидания
 * полного завершения прокрутки.
 * Проблема актуальна при использовании [RecyclerView] в [CoordinatorLayout] и проявляется в том, что хотя визуально
 * прокрутка останавливается, но [RecyclerView] переходит в состояние SCROLL_STATE_IDLE с ощутимой задержкой.
 * Описание проблемы: https://issuetracker.google.com/issues/66996774?pli=1
 *
 * @author us.bessonov
 *
 * TODO: 5/13/2020 Проверить актуальность проблемы после повышения версий библиотек https://online.sbis.ru/opendoc.html?guid=4c260f6a-6be7-4e34-b519-b66a3dece946
 */
class FixClicksAfterScrollItemTouchListener : RecyclerView.SimpleOnItemTouchListener() {

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean = with(rv) {
        if (scrollState == RecyclerView.SCROLL_STATE_SETTLING && e.actionMasked == MotionEvent.ACTION_DOWN &&
            (!canScrollVertically(-1) || !canScrollVertically(1))
        ) {
            stopScroll()
        }

        false
    }
}