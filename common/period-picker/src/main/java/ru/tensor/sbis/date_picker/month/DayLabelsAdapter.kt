package ru.tensor.sbis.date_picker.month

import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.date_picker.month.items.DayLabelVM
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design.view_ext.SimplifiedTextView

/**
 * Адаптер названий дней недели
 *
 * @author us.bessonov
 */
internal class DayLabelsAdapter(private val days: List<DayLabelVM>) :
    RecyclerView.Adapter<DayLabelsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SimplifiedTextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.date_picker_day_size),
                resources.getDimensionPixelSize(R.dimen.date_picker_item_label_height)
            )
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.date_picker_day_label_text_size))
            context.getDataFromAttrOrNull(R.attr.date_picker_labels_color)
                ?.let(::setTextColor)
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount() = days.size

    class ViewHolder(private val view: SimplifiedTextView) : RecyclerView.ViewHolder(view) {

        /** @SelfDocumented */
        fun bind(vm: DayLabelVM) {
            view.text = vm.label
        }

    }
}