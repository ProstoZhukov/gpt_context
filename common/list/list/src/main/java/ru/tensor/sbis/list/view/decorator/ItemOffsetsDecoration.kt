package ru.tensor.sbis.list.view.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.util.dpToPx
import ru.tensor.sbis.list.R
import ru.tensor.sbis.list.view.ListDataHolder

/**
 * Декоратор для установки стандартных внутренних отступов ячеек.
 *
 * @author us.bessonov
 */
internal class ItemOffsetsDecoration(
    private val sectionsHolder: ListDataHolder
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)

        if (position == RecyclerView.NO_POSITION
            || position >= sectionsHolder.getItems().size
            || sectionsHolder.getItems().isEmpty()
        ) return

        /*
        Устанавливается padding, а не модифицируется `outRect`, поскольку нужны именно внутренние отступы, без привязки
        к `onBind` ячеек. Использование этого метода для такой цели уместно, т.к. выполняется схожая задача, и как раз
        перед тем как элементы будут измерены.
         */
       configureItemPadding(view, position, sectionsHolder)
    }
}