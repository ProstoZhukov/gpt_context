package ru.tensor.sbis.design.selection.ui.list.items.multi.region

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.list.items.multi.DefaultMultiSelectorViewHolder
import ru.tensor.sbis.design.selection.ui.list.items.single.MAX_TITLE_LINES
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.region.RegionSelectorItemModel
import androidx.core.view.isInvisible

/**
 * Базовая реализация [RecyclerView.ViewHolder] для отображения информации в компоненте выбора
 *
 * @author ma.kolpakov
 */
internal class RegionMultiSelectorItemViewHolder(
    view: View,
    clickDelegate: SelectionClickDelegate<SelectorItemModel>
) : DefaultMultiSelectorViewHolder<RegionSelectorItemModel>(view, clickDelegate) {

    private val counter: TextView = view.findViewById(R.id.counter)

    override fun bind(data: RegionSelectorItemModel) {
        super.bind(data)

        with(data.meta.formattedCounter) {
            counter.isInvisible = this == null
            counter.text = this
        }
    }

    override fun isSubtitleVisible(data: RegionSelectorItemModel): Boolean =
        !data.meta.isSelected && data.subtitle != null

    override fun getTitleMaxLines() = MAX_TITLE_LINES
}