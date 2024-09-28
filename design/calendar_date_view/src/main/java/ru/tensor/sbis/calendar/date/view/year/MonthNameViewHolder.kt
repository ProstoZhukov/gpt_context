package ru.tensor.sbis.calendar.date.view.year

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.calendar.date.R

/**
 * ViewHolder для отображения названия месяцев.
 *
 * @author @ra.petrov
 */
internal class MonthNameViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    constructor(parent: ViewGroup) :
            this(LayoutInflater.from(parent.context).inflate(R.layout.month_label_text, parent, false))

    private val monthTextView: TextView = view.findViewById(R.id.month_text)

    /** @SelfDocumented */
    fun bind(label: String) {
        monthTextView.text = label
    }
}