package ru.tensor.sbis.localfeaturetoggle.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.localfeaturetoggle.R
import ru.tensor.sbis.localfeaturetoggle.data.Feature

/**
 * Адаптер списка фичей.
 *
 * @author mb.kruglova
 */
internal class LocalFeatureToggleAdapter(
    var onSwitchItemClicked: (Feature, isChecked: Boolean) -> Unit = { _, _ -> }
) :
    RecyclerView.Adapter<LocalFeatureToggleAdapter.LocalFeatureToggleViewHolder>() {

    private val features: MutableList<Feature> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalFeatureToggleViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.local_feature_toggle_item, parent, false)
        return LocalFeatureToggleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocalFeatureToggleViewHolder, position: Int) {
        holder.title.text = features[position].description
        holder.switch.isChecked = features[position].isActivated
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            onSwitchItemClicked(features[position], isChecked)
        }
    }

    override fun onViewRecycled(holder: LocalFeatureToggleViewHolder) {
        holder.switch.setOnCheckedChangeListener(null)
    }

    override fun getItemCount(): Int = features.size

    fun setList(newFeatures: List<Feature>) {
        features.clear()
        features.addAll(newFeatures)
        notifyItemRangeInserted(0, newFeatures.size)
    }

    class LocalFeatureToggleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.local_feature_toggle_title)
        val switch: SwitchCompat = itemView.findViewById(R.id.local_feature_toggle_switch_view)
    }
}