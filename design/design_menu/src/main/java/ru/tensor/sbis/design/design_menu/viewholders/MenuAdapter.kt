package ru.tensor.sbis.design.design_menu.viewholders

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.design_menu.CustomViewMenuItem
import ru.tensor.sbis.design.design_menu.R
import ru.tensor.sbis.design.design_menu.SbisMenu
import ru.tensor.sbis.design.design_menu.SbisMenuItem
import ru.tensor.sbis.design.design_menu.SbisMenuNested
import ru.tensor.sbis.design.design_menu.api.BaseMenuItem
import ru.tensor.sbis.design.design_menu.api.ClickableItem
import ru.tensor.sbis.design.design_menu.api.MenuItem
import ru.tensor.sbis.design.design_menu.databinding.MenuCustomItemBinding
import ru.tensor.sbis.design.design_menu.databinding.MenuTextDividerBinding
import ru.tensor.sbis.design.design_menu.dividers.Divider
import ru.tensor.sbis.design.design_menu.dividers.LineDivider
import ru.tensor.sbis.design.design_menu.dividers.TextDivider
import ru.tensor.sbis.design.design_menu.dividers.TextLineDivider
import ru.tensor.sbis.design.design_menu.utils.ContainerType
import ru.tensor.sbis.design.design_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.design_menu.view.MenuItemView
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.HorizontalPosition
import timber.log.Timber

/**
 * Адаптер для [SbisMenu].
 *
 * @author ra.geraskin
 */
internal class MenuAdapter(
    private val hideDefaultDividers: Boolean,
    private val selectionEnabled: Boolean,
    private val hasTitle: Boolean,
    private val styleHolder: SbisMenuStyleHolder,
    @Px
    private val minItemWidth: Int,
    private val containerType: ContainerType,
    private val twoLinesItemsTitle: Boolean
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var items = mutableListOf<MenuItem>()
    private var markerAlignment = HorizontalPosition.LEFT

    /** Максимальная ширина элемента. */
    var maxItemWidth: Int? = minItemWidth

    /** Слушатель кликов для элементов. */
    var clickListener: ((item: ClickableItem) -> Unit)? = null

    /** Есть ли в списке элементов элемент подменю. */
    private var hasMenuItems = false

    fun getItemPosition(item: MenuItem?): Int {
        return items.indexOf(item)
    }

    /** Задать список элементов. */
    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: Iterable<MenuItem>, context: Context) {
        if (newItems.none()) {
            Timber.e("Компонент Меню 'SbisMenu' получил пустой список элементов.")
            return
        }
        items.clear()
        items.addAll(prepareItems(newItems.toList()))
        hasMenuItems = items.filterIsInstance<SbisMenu>().any()
        updateMarkerAlignment()
        maxItemWidth = measureMaxItemWidth(context)
        notifyDataSetChanged()
    }

    @ViewType
    override fun getItemViewType(position: Int): Int = when (val item = items[position]) {
        is SbisMenu, is SbisMenuItem -> ITEM
        is Divider -> DIVIDER
        is CustomViewMenuItem -> CUSTOM_VIEW
        else -> error("Unsupported menu item type ${item.javaClass.name}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, @ViewType viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM -> {
                val view = MenuItemView(
                    parent.context,
                    styleHolder,
                    markerAlignment,
                    containerType,
                    twoLinesItemsTitle
                ).apply {
                    layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                }
                ItemViewHolder(view, styleHolder)
            }

            DIVIDER -> {
                val binding = MenuTextDividerBinding.inflate(inflater, parent, false)
                DividerViewHolder(binding, styleHolder)
            }

            CUSTOM_VIEW -> {
                val binding = MenuCustomItemBinding.inflate(inflater, parent, false)
                CustomViewHolder(binding, styleHolder)
            }

            else -> error("Unsupported view type of sbis menu $viewType")

        }.also {
            maxItemWidth?.let { maxItemWidth ->
                it.setWidth(maxItemWidth.coerceAtLeast(minItemWidth))
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, selectionEnabled, hasMenuItems, clickListener)
    }

    override fun getItemCount() = items.count()

    /**
     * Подготовка элементов меню для добавления в список.
     */
    private fun prepareItems(rawItems: List<MenuItem>): List<MenuItem> {
        val expandedMenuItems = expandNestedMenu(rawItems)
        val filteredMenuItems = filterHiddenItems(expandedMenuItems)
        return if (!hideDefaultDividers) addDefaultDividersToItemList(filteredMenuItems)
        else filteredMenuItems
    }

    /**
     * Фильтрация "спрятанных" элементов.
     */
    private fun filterHiddenItems(rawItems: List<MenuItem>) = rawItems.filter {
        (it is BaseMenuItem && it.hidden).not()
    }

    /**
     * Добавление стандартных разделителей между элементами.
     */
    private fun addDefaultDividersToItemList(rawItems: List<MenuItem>): List<MenuItem> {
        val itemWithDivider = mutableListOf<MenuItem>()
        var isPreviousDivider = hasTitle
        rawItems.forEachIndexed { index, item ->
            val itemIsDivider = item is Divider
            // НЕ добавляем разделитель если это первый элемент в меню, и вокруг пользовательского разделителя
            if (!itemIsDivider && !isPreviousDivider && index > 0) {
                itemWithDivider.add(LineDivider)
            }
            isPreviousDivider = itemIsDivider
            itemWithDivider.add(item)
        }
        return itemWithDivider
    }

    /**
     * Разворачивание всех вложенных меню в одноуровневый список, с присвоением
     * элементам соответствующего уровня иерархии hierarchyLevel.
     */
    private fun expandNestedMenu(items: List<MenuItem>, hierarchyLevel: Int = 0): List<MenuItem> {
        val expandedList = mutableListOf<MenuItem>()
        items.forEach { menuItem ->
            when (menuItem) {
                is SbisMenuNested -> {
                    expandedList.add(menuItem.toSbisMenuItem(hierarchyLevel))
                    expandedList.addAll(expandNestedMenu(menuItem.children, hierarchyLevel + 1))
                }

                is BaseMenuItem -> expandedList.add(menuItem.apply { this.hierarchyLevel = hierarchyLevel })

                else -> expandedList.add(menuItem)

            }
        }
        return expandedList
    }

    /**
     * Определить размер максимального элемента списка и записать в [maxItemWidth].
     */
    private fun measureMaxItemWidth(context: Context): Int {
        val customViewWidth = items.filterIsInstance<CustomViewMenuItem>().maxOfOrNull {
            it.factory.invoke(context).let { view ->
                view.measure(specs, specs)
                view.measuredWidth
            }
        } ?: 0
        val biggestItemWidth = measureMaxItemsWidth(context)
        return maxOf(customViewWidth, biggestItemWidth)
    }

    private fun measureMaxItemsWidth(context: Context): Int {
        var maxWidth = 0

        items.forEachIndexed { index, item ->
            val width = when (item) {
                is BaseMenuItem -> MenuItemView(
                    context,
                    styleHolder,
                    markerAlignment,
                    containerType,
                    twoLinesItemsTitle
                ).apply {
                    setParams(item, hasMenuItems, selectionEnabled)
                }.measureWidth(styleHolder.maxItemWidth)

                is TextLineDivider -> {
                    SbisTextView(context, R.style.MenuDividerText).measureText(item.text).toInt()
                }

                is TextDivider -> {
                    SbisTextView(context, R.style.MenuDividerText).measureText(item.text).toInt()
                }

                else -> 0
            }
            maxWidth = maxOf(maxWidth, width)

        }
        return maxWidth
    }

    private fun updateMarkerAlignment() {
        // Считается, что иконки всех элементов располагаются с одной и той же стороны.
        // Маркеры всегда должны располагаться с противоположной стороны от иконок.

        markerAlignment = HorizontalPosition.LEFT

        val iconsAlignment = items
            .filterIsInstance<SbisMenuItem>()
            .filter { it.icon != null }
            .getOrNull(0)?.settings?.iconAlignment ?: return

        markerAlignment = iconsAlignment.swap()
    }

    private fun HorizontalPosition.swap() =
        if (this == HorizontalPosition.LEFT) HorizontalPosition.RIGHT else HorizontalPosition.LEFT

    /**
     * Метод расчёта высоты, которую  будут занимать [View]-элементы меню. Применяется для предварительного расчёта
     * максимальной высоты меню в панельке.
     *
     * TODO( Удалить метод, когда будет решена проблема FitToContent для штоки по задаче:
     *  https://dev.sbis.ru/opendoc.html?guid=a4d4000a-c28f-4098-bae5-dfe8d2d42805&client=3 )
     */
    internal fun measureFullMenuHeightForPanel(ctx: Context): Int {
        var totalHeight = 0
        val itemView = MenuItemView(ctx, styleHolder, markerAlignment, containerType, twoLinesItemsTitle)
        val dividerHeight = with(styleHolder) { dividerHeight + dividerMarginBottom + dividerMarginTop }
        items.forEach { item ->
            totalHeight += when (item) {
                is BaseMenuItem ->
                    itemView.apply {
                        setParams(item, hasMenuItems, selectionEnabled)
                        measure(
                            MeasureSpecUtils.makeExactlySpec(Resources.getSystem().displayMetrics.widthPixels),
                            MeasureSpecUtils.makeUnspecifiedSpec()
                        )
                    }.measuredHeight

                is Divider -> dividerHeight

                is CustomViewMenuItem -> item.factory(ctx).apply {
                    measure(MeasureSpecUtils.makeUnspecifiedSpec(), MeasureSpecUtils.makeUnspecifiedSpec())
                }.measuredHeight

                else -> 0
            }
        }
        return totalHeight
    }

    companion object {
        val specs = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    }
}