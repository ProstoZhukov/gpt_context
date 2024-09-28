package ru.tensor.sbis.crud4.view

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import ru.tensor.sbis.service.DecoratedProtocol

class MoreButtonViewHolderHelper<SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, IDENTIFIER>(
    val actionDelegate: ItemActionDelegate<SOURCE_ITEM, IDENTIFIER>
) :
    ViewHolderHelper<Long, ViewHolder> {
    lateinit var textView: SbisTextView
    override fun createViewHolder(parentView: ViewGroup) = object : ViewHolder(
        FrameLayout(parentView.context).apply {
            isClickable = true
            textView = SbisTextView(parentView.context).apply {
                isClickable = true
                typeface = TypefaceManager.getSbisMobileIconTypeface(context)
                setTextColor(IconColor.DEFAULT.getValue(context))
                setText(ru.tensor.sbis.design.R.string.design_mobile_icon_more)
                gravity = Gravity.CENTER_HORIZONTAL
            }

            addView(textView)
        }
    ) {}

    override fun bindToViewHolder(data: Long, viewHolder: ViewHolder) {
        textView.setOnClickListener {
            actionDelegate.expandFolderClick(data)
        }
    }

}
