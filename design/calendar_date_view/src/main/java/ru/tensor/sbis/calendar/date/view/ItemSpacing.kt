package ru.tensor.sbis.calendar.date.view

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.calendar.date.R

/**
 * Класс декоратор ячеек . Делает отступ в 1dp снизу и справа от ячейки
 *
 * @author Носков Алексей Евгеньевич
 */
internal class ItemSpacing(context: Context): RecyclerView.ItemDecoration() {

    private val itemSpacing = context.resources.getDimensionPixelSize(R.dimen.design_standard_padding_minimum)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = itemSpacing
        outRect.right = itemSpacing
    }
}