package ru.tensor.sbis.appdesign.skeletonview.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @author us.merzlikina
 */
class RecyclerViewAdapter(private val items: List<RecyclerViewListItem>) : RecyclerView.Adapter<RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size
}