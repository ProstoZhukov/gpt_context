package ru.tensor.sbis.design.cylinder.picker.cylinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.cylinder.picker.R

/**
 * @author Subbotenko Dmitry
 */
internal class CylinderViewHolder(view: View) :
    RecyclerView.ViewHolder(view), IBindCylinder {

    private var text: TextView = view.findViewById(R.id.text)

    constructor(view: ViewGroup) : this(
        LayoutInflater.from(view.context).inflate(R.layout.picker_item, view, false) as View
    )

    override fun bind(text: String) {
        this.text.text = text
    }

    override fun bind(text: String, color: Int) {
        bind(text)
        this.text.setTextColor(color)
    }
}