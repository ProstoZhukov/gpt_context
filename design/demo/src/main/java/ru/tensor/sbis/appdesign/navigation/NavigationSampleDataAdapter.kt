package ru.tensor.sbis.appdesign.navigation

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Адаптер с тестовыми данными для навигации.
 *
 * @author ma.kolpakov
 */
class NavigationSampleDataAdapter(
    private val itemType: String
) : RecyclerView.Adapter<NavigationSampleDataViewHolder>() {

    companion object {
        private const val ITEMS_COUNT = 100
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationSampleDataViewHolder {
        return LayoutInflater
            .from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
            .run(::NavigationSampleDataViewHolder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NavigationSampleDataViewHolder, position: Int) {
        holder.textView.text = "$itemType $position"
    }

    override fun getItemCount(): Int {
        return ITEMS_COUNT
    }
}