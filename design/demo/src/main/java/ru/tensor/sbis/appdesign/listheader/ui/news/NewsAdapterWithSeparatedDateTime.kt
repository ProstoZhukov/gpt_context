package ru.tensor.sbis.appdesign.listheader.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.list_header.ItemDateView
import ru.tensor.sbis.design.list_header.ListDateViewUpdater

/**
 * @author ra.petrov
 */
class NewsAdapterWithSeparatedDateTime(
    private val content: List<NewsModel>,
    private val formattedDateProvider: ListDateViewUpdater
) : NewsAdapter(content, formattedDateProvider) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.date_header_list_item_separated, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = content[position]
        holder.title.text = news.title
        holder.body.text = news.body
        holder.date.setFormattedDateTime(formattedDateProvider.getFormattedDate(position))
        (holder.itemView.findViewById(R.id.time) as ItemDateView).setFormattedDateTime(
            formattedDateProvider.getFormattedDate(
                position
            )
        )
    }
}