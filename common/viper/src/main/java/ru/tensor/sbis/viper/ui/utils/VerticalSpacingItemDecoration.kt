package ru.tensor.sbis.viper.ui.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author ga.malinskiy
 */
class VerticalSpacingItemDecoration constructor(private val verticalSpacing: Int) : RecyclerView.ItemDecoration() {

    private var layoutManager: LinearLayoutManager? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (layoutManager == null) {
            layoutManager = parent.layoutManager as LinearLayoutManager?
        }

        val position = parent.getChildAdapterPosition(view)
        val isFirstRow = position == 0

        if (isFirstRow) {
            outRect.top = verticalSpacing
        }

        outRect.bottom = verticalSpacing
    }
}