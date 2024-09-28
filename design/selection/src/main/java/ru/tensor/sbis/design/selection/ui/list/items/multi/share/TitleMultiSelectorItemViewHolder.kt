package ru.tensor.sbis.design.selection.ui.list.items.multi.share

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.model.share.TitleItemModel

/**
 * Реализация [RecyclerView.ViewHolder] для отображения заголовка
 *
 * @author ma.kolpakov
 */
internal class TitleMultiSelectorItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val title: TextView = view.findViewById(R.id.title)

    fun bind(data: TitleItemModel) {
        title.text = data.title
    }
}
