package ru.tensor.sbis.date_picker

import androidx.recyclerview.widget.RecyclerView

/**
 * @author mb.kruglova
 */
internal class DatePickerScrollListener(
    private val onScrollStateChangedCallback: (recyclerView: RecyclerView, newState: Int) -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        onScrollStateChangedCallback(recyclerView, newState)
    }
}