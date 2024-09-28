package ru.tensor.sbis.design.selection.ui.list.items.single.region

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.list.items.single.DefaultSingleSelectorViewHolder
import ru.tensor.sbis.design.selection.ui.list.items.single.MAX_TITLE_LINES
import ru.tensor.sbis.design.selection.ui.model.region.RegionSelectorItemModel
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

/**
 * Реализация [RecyclerView.ViewHolder] элемента списка для одиночного выбора
 *
 * @author us.bessonov
 */
internal class RegionSingleSelectorItemViewHolder(
    view: View
) : DefaultSingleSelectorViewHolder<RegionSelectorItemModel>(view) {

    private val counter: TextView = view.findViewById(R.id.counter)
    private val marker: View = view.findViewById(R.id.marker)

    override fun bind(data: RegionSelectorItemModel) {
        super.bind(data)

        marker.isVisible = data.meta.selected

        with(data.meta.formattedCounter) {
            counter.isInvisible = this == null
            counter.text = this
        }
    }

    override fun getTitleMaxLines() = MAX_TITLE_LINES
}