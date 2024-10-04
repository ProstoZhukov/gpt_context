package ru.tensor.sbis.design.context_menu.viewholders

import android.view.View
import ru.tensor.sbis.design.context_menu.R
import ru.tensor.sbis.design.context_menu.ClickableItem
import ru.tensor.sbis.design.context_menu.Item
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.dividers.TextDivider
import ru.tensor.sbis.design.context_menu.dividers.TextDividerAlignment
import ru.tensor.sbis.design.context_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * ViewHolder разделителя с заголовком [SbisMenu].
 *
 * @author ma.kolpakov
 */
internal class TextDividerViewHolder(view: View, styleHolder: SbisMenuStyleHolder) : BaseViewHolder(view, styleHolder) {
    private val textView: SbisTextView = view.findViewById(R.id.context_menu_divider_text)
    private val leftLine: View = view.findViewById(R.id.context_menu_divider_line_left)
    private val rightLine: View = view.findViewById(R.id.context_menu_divider_line_right)

    override fun bind(item: Item, clickListener: ((item: ClickableItem) -> Unit)?) {
        if (item !is TextDivider) return
        if (item.title != null) {
            textView.text = item.title
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }
        when (item.alignment) {
            TextDividerAlignment.LEFT -> {
                leftLine.visibility = View.GONE
                textView.visibility = View.VISIBLE
                rightLine.visibility = View.VISIBLE
            }
            TextDividerAlignment.CENTER -> {
                leftLine.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                rightLine.visibility = View.VISIBLE
            }
            TextDividerAlignment.RIGHT -> {
                leftLine.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                rightLine.visibility = View.GONE
            }
        }
    }
}