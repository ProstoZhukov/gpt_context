package ru.tensor.sbis.design.chips.list

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager

/**
 * [RecyclerView.ItemDecoration] для отступа между чипсами в списке.
 *
 * @author ps.smirnyh
 */
internal class SbisChipsItemDecorator(@Px private val offset: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val indexItem = parent.getChildAdapterPosition(view)
        if (parent.layoutManager is LinearLayoutManager) {
            if (indexItem > 0) {
                outRect.set(offset, 0, 0, 0)
            }
        } else {
            val lm = parent.layoutManager as? FlexboxLayoutManager ?: return
            val flexLines = lm.flexLines
            var isLastInLine = false
            val isFirstLine = flexLines.firstOrNull()?.let { indexItem < it.itemCount } ?: false
            flexLines.forEach { line ->
                isLastInLine = isLastInLine || indexItem == line.firstIndex + line.itemCount - 1
            }
            outRect.set(
                0,
                if (isFirstLine) 0 else offset,
                if (isLastInLine) 0 else offset,
                0
            )
        }
    }
}