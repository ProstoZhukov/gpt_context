package ru.tensor.sbis.modalwindows.dialogalert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.modalwindows.R

/**@SelfDocumented*/
class SimpleStringAdapter(val items: List<CharSequence>, val listener: DialogItemClickListener):
        RecyclerView.Adapter<SimpleStringAdapter.StringViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.modalwindows_item_single_line, parent, false)
        return StringViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: StringViewHolder, position: Int) {
        holder.itemView.findViewById<SbisTextView>(R.id.modalwindows_list_item_text).text = items[position]
        holder.itemView.setOnClickListener { listener.onItemClicked(position) }
    }

    /**@SelfDocumented*/
    class StringViewHolder(view: View): RecyclerView.ViewHolder(view)
}