package ru.tensor.sbis.appdesign.navigation

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

/**
 * ViewHolder для экрана навигации.
 *
 * @author va.shumilov
 */
class NavigationSampleDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textView: TextView = itemView.findViewById(android.R.id.text1)
}