package ru.tensor.sbis.base_components.adapter.selection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.R
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.design_dialogs.fragment.CustomBottomSheetDialog

/**
 * Created by kabramov on 20.03.2018.
 *
 * Simple adapter to show selecting items
 * mostly will be used in [CustomBottomSheetDialog]
 */
abstract class CommonSelectionAdapter<Model> : RecyclerView.Adapter<CommonSelectionAdapter.SimpleCommonSelectionViewHolder<Model>>, CommonSelectionItem<Model> {

    /**@SelfDocumented*/
    var items: List<Model>

    /**@SelfDocumented*/
    var selectedItem: Model? = null

    /**@SelfDocumented*/
    var itemClickListener: OnItemClickListener<Model>? = null
        set(listener) {
            field = OnItemClickListener { item ->
                    listener!!.onSelectionItemClick(item)
                    if (item !== selectedItem) {
                        selectedItem = item
                        notifyDataSetChanged()
                    }
                }
        }

    /**@SelfDocumented*/
    @ColorRes
    var textColor: Int = 0

    /**@SelfDocumented*/
    @ColorRes
    var backgroundColor: Int = 0

    constructor() {
        items = emptyList()
    }

    constructor(items: List<Model>) {
        this.items = items
    }

    @Suppress("unused")
    constructor(listener: OnItemClickListener<Model>) : this() {
        itemClickListener = listener
    }

    constructor(items: List<Model>,
                listener: OnItemClickListener<Model>) : this(items) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleCommonSelectionViewHolder<Model> {
        return SimpleCommonSelectionViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.base_components_view_menu_with_check_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SimpleCommonSelectionViewHolder<Model>, position: Int) {
        holder.bind(getItem(position), this, itemClickListener, selectedItem, textColor, backgroundColor)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): Model {
        return items[position]
    }

    /**@SelfDocumented*/
    class SimpleCommonSelectionViewHolder<Model> constructor(itemView: View) : AbstractViewHolder<Model>(itemView) {

        private val layout: LinearLayout = itemView.findViewById(R.id.base_components_popup_menu_layout)
        private val checkIcon: TextView = itemView.findViewById(R.id.base_components_popup_menu_item_check)
        private val titleText: TextView = itemView.findViewById(R.id.base_components_popup_menu_item_title)

        fun bind(dataModel: Model,
                 listener: CommonSelectionItem<Model>,
                 itemClickListener: OnItemClickListener<Model>?,
                 selectedItem: Model?,
                 textColor: Int,
                 backgroundColor: Int) {
            super.bind(dataModel)
            checkIcon.visibility = if (dataModel == selectedItem) View.VISIBLE else View.INVISIBLE
            titleText.text = listener.getItemTitle(dataModel)
            if (backgroundColor != 0) {
                layout.setBackgroundColor(backgroundColor)
            }
            if (textColor != 0) {
                titleText.setTextColor(textColor)
            }
            if (itemClickListener != null)
                itemView.setOnClickListener { itemClickListener.onSelectionItemClick(dataModel) }
        }

    }

    /**@SelfDocumented*/
    fun interface OnItemClickListener<in Model> {

        /**@SelfDocumented*/
        fun onSelectionItemClick(item: Model)

    }

}
