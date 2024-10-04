package ru.tensor.sbis.design.context_menu

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.context_menu.dividers.Divider
import ru.tensor.sbis.design.context_menu.dividers.SlimDivider
import ru.tensor.sbis.design.context_menu.dividers.TextDivider
import ru.tensor.sbis.design.context_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.context_menu.viewholders.BaseViewHolder
import ru.tensor.sbis.design.context_menu.viewholders.CustomViwViewHolder
import ru.tensor.sbis.design.context_menu.viewholders.DividerViewHolder
import ru.tensor.sbis.design.context_menu.viewholders.ItemViewHolder
import ru.tensor.sbis.design.context_menu.viewholders.TextDividerViewHolder
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import timber.log.Timber
import kotlin.math.max
import android.R as RAndroid

/**
 * Адаптер для [SbisMenu].
 *
 * @author ma.kolpakov
 */
internal class MenuAdapter(
    private val hideDefaultDividers: Boolean,
    private val hasTitle: Boolean,
    private val styleHolder: SbisMenuStyleHolder,
    @Px
    private val minItemWidth: Int,
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var items = mutableListOf<Item>()

    /** Максимальная ширина элемента. */
    var maxItemWidth: Int? = minItemWidth

    /** Слушатель кликов для элементов. */
    var clickListener: ((item: ClickableItem) -> Unit)? = null

    /** Задать список элементов. */
    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: Iterable<Item>, context: Context) {
        if (newItems.none()) {
            Timber.e("Компонент Меню 'SbisMenu' получил пустой список элементов.")
            return
        }
        items.clear()

        val filteredItems = newItems.filter {
            (it is BaseItem && it.hidden).not()
        }

        val itemWithDivider = mutableListOf<Item>()
        var isPreviousDivider = hasTitle

        if (!hideDefaultDividers) {
            filteredItems.forEachIndexed { index, item ->
                val itemIsDivider = item is Divider || item is TextDivider
                // НЕ добавляем разделитель если это первый элемент в меню, и вокруг пользовательского разделителя
                if (!itemIsDivider && !isPreviousDivider && index > 0) {
                    itemWithDivider.add(SlimDivider)
                }
                isPreviousDivider = itemIsDivider
                itemWithDivider.add(item)
            }
            items.addAll(itemWithDivider)
        } else {
            items.addAll(filteredItems)
        }
        measureMaxItemWidth(context)
        notifyDataSetChanged()
    }

    @ViewType
    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is SbisMenu,
            is DefaultItem -> ITEM

            is MenuItem -> ITEM
            is Divider -> DIVIDER
            is TextDivider -> DIVIDER_TEXT
            is CustomViewItem -> CUSTOM_VIEW
            else -> {
                error("Unsupported item type ${item.javaClass.name}")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, @ViewType viewType: Int): BaseViewHolder {
        return when (viewType) {
            ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.context_menu_item,
                    parent,
                    false
                )
                view.findViewById<View>(R.id.context_menu_item_root).background = getItemBackground(styleHolder)
                ItemViewHolder(view, styleHolder)
            }

            DIVIDER_TEXT -> {
                TextDividerViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.context_menu_text_divider,
                        parent,
                        false
                    ),
                    styleHolder
                )
            }

            DIVIDER -> {
                DividerViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.context_menu_divider,
                        parent,
                        false
                    ),
                    styleHolder
                )
            }

            CUSTOM_VIEW -> {
                CustomViwViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.context_menu_custom_item,
                        parent,
                        false
                    ).apply {
                        rootView.background = getItemBackground(styleHolder)
                    },
                    styleHolder
                )
            }

            else -> {
                error("Unsupported view type $viewType")
            }
        }.also {
            it.setWidth(maxItemWidth?.coerceAtLeast(minItemWidth))
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = items[position]
        holder.bind(
            item,
            clickListener
        )
    }

    override fun getItemCount() = items.count()

    private fun getItemBackground(styleHolder: SbisMenuStyleHolder): StateListDrawable {
        val stateListDrawable = StateListDrawable()

        stateListDrawable.addState(
            intArrayOf(RAndroid.attr.state_pressed),
            ColorDrawable(styleHolder.itemBackgroundPressedColor)
        )
        stateListDrawable.addState(intArrayOf(), ColorDrawable(styleHolder.itemBackgroundColor))
        return stateListDrawable
    }

    /**
     * Определить размер максимального элемента списка и записать в [maxItemWidth].
     */
    private fun measureMaxItemWidth(context: Context) {
        val customViewWidth = items.filterIsInstance<CustomViewItem>().maxOfOrNull {
            it.factory.invoke(context).let { view ->
                view.measure(specs, specs)
                view.measuredWidth
            }
        } ?: 0
        val biggestItemIndex = findBiggestItemIndex(context)
        val biggestItem = items[biggestItemIndex]
        val biggestItemLayout = getLayoutByItem(biggestItem)
        val biggestItemView = LayoutInflater.from(context).inflate(biggestItemLayout, null, false)
        val biggestItemViewHolder = getViewHolderByItem(biggestItem, biggestItemView)
        val biggestItemWidth = getItemViewWidthByViewHolder(biggestItemViewHolder, biggestItem)
        maxItemWidth = maxOf(customViewWidth, biggestItemWidth)
    }

    private fun getLayoutByItem(item: Item) =
        if (item is TextDivider) R.layout.context_menu_text_divider
        else R.layout.context_menu_item

    private fun getViewHolderByItem(item: Item, view: View) =
        if (item is TextDivider) TextDividerViewHolder(view, styleHolder)
        else ItemViewHolder(view, styleHolder)

    private fun getItemViewWidthByViewHolder(viewHolder: BaseViewHolder, item: Item): Int = with(viewHolder) {
        bind(item) {}
        itemView.measure(specs, specs)
        return itemView.measuredWidth
    }

    private fun findBiggestItemIndex(context: Context): Int {
        var maxTextWidth = 0
        var biggestItemIndex = 0

        /**
         * Проходим по элементам, если в них есть текст,
         * замеряем его размер и таким образом находим элемент с самым длинным текстом =>
         * узнаем индекс самого широкого элемента списка (без расчета [CustomViewItem]).
         */
        items.forEachIndexed { index, item ->
            val width = when (item) {
                is MenuItem -> getTextWidth(context, item)
                is DefaultItem -> getTextWidth(context, item)
                is TextDivider -> {
                    SbisTextView(context, R.style.ContextMenuDividerText).measureText(item.title).toInt()
                }

                else -> null
            }
            if (width != null) {
                if (maxTextWidth < width) biggestItemIndex = index
                maxTextWidth = maxOf(maxTextWidth, width)
            }
        }
        return biggestItemIndex
    }

    private fun getTextWidth(context: Context, item: BaseItem) = max(
        SbisTextView(context, R.style.ContextMenuItemText).measureText(item.title?.getString(context)),
        SbisTextView(context, R.style.ContextMenuItemComment).measureText(item.discoverabilityTitle)
    ).toInt()

    companion object {
        val specs = MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
    }

}
