package ru.tensor.sbis.design.list_utils.util

import android.os.Build
import android.os.Trace
import androidx.recyclerview.widget.RecyclerView

/**
 * Инструмент для трейсинга прокрутки конкретного [RecyclerView].
 *
 * @author us.bessonov
 */
class RecyclerViewScrollTracer(private val tag: String) : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        val sectionName = "[Scroll]$tag"
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            Trace.beginAsyncSection(sectionName, 0)
        } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            Trace.endAsyncSection(sectionName, 0)
        }
    }
}