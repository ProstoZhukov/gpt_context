package ru.tensor.sbis.crud4.view.items

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import ru.tensor.sbis.crud4.R
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.design.checkbox.SbisCheckboxView
import ru.tensor.sbis.design.theme.global_variables.BorderThickness
import ru.tensor.sbis.design.theme.global_variables.MarkerColor
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import ru.tensor.sbis.service.DecoratedProtocol

/**
 * Базовая реализация ViewHolderHelper для дефолтной ячейки.
 *
 * @author ma.kolpakov
 */
internal abstract class BaseItemViewHolderHelper<DATA : DecoratedProtocol<IDENTIFIER>, IDENTIFIER>(
    private val actionDelegate: ItemActionDelegate<DATA, IDENTIFIER>,
    private val viewHolderDelegate: () -> ViewHolderDelegate<DATA, IDENTIFIER>,
    private val isSelectMode: Boolean = false
) : ViewHolderHelper<DATA, BaseItemViewHolder<DATA, IDENTIFIER>> {
    override fun createViewHolder(parentView: ViewGroup): BaseItemViewHolder<DATA, IDENTIFIER> {
        val viewHolder = viewHolderDelegate()
        return BaseItemViewHolder(createView(parentView, viewHolder), actionDelegate, viewHolder, isSelectMode)
    }

    override fun bindToViewHolder(data: DATA, viewHolder: BaseItemViewHolder<DATA, IDENTIFIER>) {
        viewHolder.onBind(data)
    }

    private fun createView(parentView: ViewGroup, viewHolder: ViewHolderDelegate<DATA, IDENTIFIER>): View {
        val root = LinearLayout(parentView.context, null).apply {
            id = R.id.crud4_item
        }
        with(root) {
            addView(
                View(parentView.context).apply {
                    id = R.id.crud4_item_marker
                    background = ColorDrawable(MarkerColor.DEFAULT.getValue(parentView.context))
                },
                ViewGroup.MarginLayoutParams(
                    BorderThickness.L.getDimenPx(parentView.context),
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply { marginEnd = Offset.XS.getDimenPx(parentView.context) }
            )

            addView(SbisCheckboxView(parentView.context).apply {
                id = R.id.crud4_item_check_box
            })

            addView(
                viewHolder.createView(parentView),
                ViewGroup.MarginLayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginStart = Offset.XS.getDimenPx(parentView.context) }
            )
        }
        return root
    }
}