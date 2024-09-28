package ru.tensor.sbis.appdesign.skeletonview.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.appdesign.R

/**
 * @author us.merzlikina
 */
class RecyclerViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.skeleton_view_recycler_item, parent, false)) {

    private val image: TextView = itemView.findViewById(R.id.avatarImage)
    private val title: TextView = itemView.findViewById(R.id.titleView)
    private val description: TextView = itemView.findViewById(R.id.descriptionView)

    fun bind(listItem: RecyclerViewListItem) {
        image.setText(listItem.avatarResId)
        title.setText(listItem.titleResId)
        description.setText(listItem.descriptionResId)
    }
}