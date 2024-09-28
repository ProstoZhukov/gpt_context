package ru.tensor.sbis.list.view.decorator

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.background.ColorProvider
import ru.tensor.sbis.list.view.utils.ProgressItem

/**
 * Декоратор для установки фона содержимого, без привязки к вызовам `onBind` конкретных ячеек.
 *
 * @author us.bessonov
 */
internal class ItemBackgroundDecoration(
    private val sectionsHolder: ListDataHolder,
    private val colorProvider: ColorProvider
) : RecyclerView.ItemDecoration() {

    override fun onDraw(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            if (position == NO_POSITION) continue

            configureItemBackground(child, position, colorProvider, sectionsHolder)
        }
    }
}

