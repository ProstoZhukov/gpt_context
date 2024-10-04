package ru.tensor.sbis.design.chips.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import ru.tensor.sbis.design.chips.R
import ru.tensor.sbis.design.chips.SbisChipsView
import ru.tensor.sbis.design.chips.item.SbisChipsItemView
import ru.tensor.sbis.design.chips.models.SbisChipsBackgroundStyle
import ru.tensor.sbis.design.chips.models.SbisChipsItem
import ru.tensor.sbis.design.chips.models.SbisChipsStyle
import ru.tensor.sbis.design.chips.models.SbisChipsViewMode
import ru.tensor.sbis.design.theme.global_variables.InlineHeight

/**
 * Адаптер списка компонента [SbisChipsView].
 *
 * @author ps.smirnyh
 */
internal class SbisChipsAdapter : ListAdapter<SbisChipsItem, ViewHolder>(SbisChipsDiffCallback()) {

    private var positionByIndex: Map<Int, Int> = emptyMap()
    internal var selectedKeys: List<Int> = emptyList()
    internal var style: SbisChipsBackgroundStyle = SbisChipsBackgroundStyle.Accented(SbisChipsStyle.DEFAULT)
    internal var isReadOnly = false
    internal var viewMode = SbisChipsViewMode.FILLED
    internal var size = InlineHeight.X3S
    internal var clickListener: (id: Int, isSelected: Boolean) -> Unit = { _, _ -> }
    internal var multiline = false

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return SbisChipsHolder(
            SbisChipsItemView(parent.context).apply {
                id = R.id.chips_item_view
                layoutParams = FlexboxLayoutManager.LayoutParams(
                    FlexboxLayoutManager.LayoutParams.WRAP_CONTENT,
                    FlexboxLayoutManager.LayoutParams.WRAP_CONTENT
                ).apply {
                    flexShrink = 0f
                    alignSelf = AlignItems.FLEX_START
                }
            }
        ).apply {
            itemView.setOnClickListener {
                clickListener(itemId.toInt(), !it.isSelected)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder as SbisChipsHolder
        val item: SbisChipsItem = getItem(position)
        val view = holder.itemView as SbisChipsItemView
        holder.bind(item)
        view.isSelected = selectedKeys.contains(item.id)
        view.style = style
        view.isEnabled = !isReadOnly
        view.viewMode = viewMode
        view.size = size
        view.isNeedShrink = multiline
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id.toLong()
    }

    override fun submitList(list: List<SbisChipsItem>?) {
        super.submitList(list)
        val map = mutableMapOf<Int, Int>()
        list?.forEachIndexed { index, sbisChipsItem ->
            map[sbisChipsItem.id] = index
        }
        positionByIndex = map
    }

    /** Обновить элементы с id [keys]. */
    internal fun updateElements(keys: Set<Int>) {
        keys.forEach { id ->
            positionByIndex[id]?.let(::notifyItemChanged)
        }
    }
}