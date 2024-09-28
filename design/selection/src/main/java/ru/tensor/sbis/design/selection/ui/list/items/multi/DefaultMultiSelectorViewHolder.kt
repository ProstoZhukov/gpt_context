package ru.tensor.sbis.design.selection.ui.list.items.multi

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.list.items.single.DefaultSingleSelectorViewHolder
import ru.tensor.sbis.design.selection.ui.list.items.single.MAX_LINES_DEFAULT
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

internal const val MAX_LINES_WITH_SUBTITLE = MAX_LINES_DEFAULT
internal const val MAX_LINES_WITHOUT_SUBTITLE = 2

/**
 * Реализация [RecyclerView.ViewHolder] для работы с базовыми параметрами [SelectorItemModel] при множественном выборе
 *
 * @author ma.kolpakov
 */
internal open class DefaultMultiSelectorViewHolder<DATA : SelectorItemModel>(
    view: View,
    clickDelegate: SelectionClickDelegate<SelectorItemModel>,
) : DefaultSingleSelectorViewHolder<DATA>(view) {

    private var iconView: View

    init {
        view.findViewById<View>(R.id.selectionIconClickArea).setOnClickListener {
            clickDelegate.onAddButtonClicked(data)
        }
        iconView = view.findViewById(R.id.selectionIcon)
    }

    override fun bind(data: DATA) {
        super.bind(data)
        itemView.isSelected = data.meta.isSelected
    }

    override fun getTitleMaxLines(): Int =
        if (isSubtitleVisible(data)) MAX_LINES_WITH_SUBTITLE else MAX_LINES_WITHOUT_SUBTITLE
}