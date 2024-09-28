@file:Suppress("unused")

package ru.tensor.sbis.mvp_extensions.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.mvp_extensions.R

/**
 * Адаптер для отображениия элементов меню с иконками.
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class CommonMenuAdapter<Model : CommonMenuAdapter.MenuItemInterface> :
    RecyclerView.Adapter<CommonMenuAdapter.SimpleCommonViewHolder<Model>> {

    interface MenuItemInterface {
        fun getTitle(context: Context): CharSequence
        fun getIcon(context: Context): CharSequence?
        fun getIconColor(context: Context): Int?
        fun getTitleTextColor(context: Context): Int? = null
    }

    /**
     *  Список элементов
     */
    var items: List<Model>
        set(items) {
            field = items
            notifyDataSetChanged()
        }

    private var isMultiLineItems = false

    private var itemClickListener: OnItemClickListener<Model>? = null

    constructor() {
        items = emptyList()
    }

    constructor(items: List<Model>) {
        this.items = items
    }

    constructor(listener: OnItemClickListener<Model>) : this() {
        itemClickListener = listener
    }

    constructor(listener: OnItemClickListener<Model>, isMultiLineItems: Boolean) : this() {
        itemClickListener = listener
        this.isMultiLineItems = isMultiLineItems
    }

    constructor(
        items: List<Model>,
        listener: OnItemClickListener<Model>
    ) : this(items) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleCommonViewHolder<Model> {
        return SimpleCommonViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.mvp_ext_popup_menu_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SimpleCommonViewHolder<Model>, position: Int) {
        holder.bind(getItem(position), itemClickListener, isMultiLineItems)
    }

    override fun onViewRecycled(holder: SimpleCommonViewHolder<Model>) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): Model {
        return items[position]
    }

    /**
     * Реализация дефолтного ViewHolder
     */
    class SimpleCommonViewHolder<Model : MenuItemInterface> internal constructor(itemView: View) :
        AbstractViewHolder<Model>(itemView) {

        private val icon: TextView = itemView.findViewById(R.id.mvp_ext_menu_icon)
        private val title: TextView = itemView.findViewById(R.id.mvp_ext_menu_item_text)

        /**
         * Заполнить элемент списка
         */
        fun bind(dataModel: Model, listener: OnItemClickListener<Model>?, isMultiLineItems: Boolean) {
            super.bind(dataModel)
            dataModel.getIcon(itemView.context)?.let {
                icon.text = it
                icon.visibility = View.VISIBLE
                icon.setTextColor(dataModel.getIconColor(itemView.context) ?: title.currentTextColor)
            } ?: run {
                icon.visibility = View.GONE
            }
            if (isMultiLineItems) title.maxLines = 2
            title.text = dataModel.getTitle(itemView.context)
            title.setTextColor(dataModel.getTitleTextColor(itemView.context) ?: getDefaultTextColor(itemView.context))
            itemView.setOnClickListener { listener?.onItemClick(dataModel) }
        }

        @ColorInt
        private fun getDefaultTextColor(context: Context): Int {
            return ContextCompat.getColor(context, R.color.mvp_default_blue_text_color)
        }

        override fun recycle() {
            super.recycle()
            itemView.setOnClickListener(null)
        }

    }

    /**
     * Слушатель кликов
     */
    interface OnItemClickListener<in Model> {

        /**
         * Событие нажатия на элемент списка
         */
        fun onItemClick(item: Model)

    }

}