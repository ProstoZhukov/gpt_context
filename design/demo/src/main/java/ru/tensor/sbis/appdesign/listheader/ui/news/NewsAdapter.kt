package ru.tensor.sbis.appdesign.listheader.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.list_header.DateTimeAdapter
import ru.tensor.sbis.design.list_header.ItemDateView
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import java.util.Date

/**
 * @author ra.petrov
 */
class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView
    val body: TextView
    val date: ItemDateView

    init {
        title = view.findViewById(R.id.title)
        body = view.findViewById(R.id.body)
        date = view.findViewById(R.id.date)
    }
}


open class NewsAdapter(private val content: List<NewsModel>, private val formattedDateProvider: ListDateViewUpdater) : RecyclerView.Adapter<NewsViewHolder>(), DateTimeAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.date_header_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = content[position]
        holder.title.text = news.title
        holder.body.text = news.body
        holder.date.setFormattedDateTime(formattedDateProvider.getFormattedDate(position))
    }

    override fun getItemCount(): Int = content.size

    override fun getItemDateTime(position: Int): Date? = content[position].date?.toDate()
}