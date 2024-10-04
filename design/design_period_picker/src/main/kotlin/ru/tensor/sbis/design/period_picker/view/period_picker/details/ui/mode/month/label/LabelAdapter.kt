package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.label

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * Адаптер меток в шапке выбора периода.
 *
 * @author mb.kruglova
 */
internal class LabelAdapter :
    RecyclerView.Adapter<LabelViewHolder>() {

    private var labels: MutableList<LabelModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view = SbisTextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER
        }

        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(labels[position])
    }

    override fun getItemCount() = labels.size

    /** @SelfDocumented */
    @SuppressLint("NotifyDataSetChanged")
    internal fun reload(newItems: List<LabelModel>) {
        labels.clear()
        labels.addAll(newItems)
        notifyDataSetChanged()
    }
}