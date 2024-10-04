package ru.tensor.sbis.design.selection.ui.list.items.multi.share.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.model.share.dialog.DialogSelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import ru.tensor.sbis.design.R as RDesign

/**
 * Хэлпер для отображения диалогов селектора через [DialogMultiSelectorItemViewHolder]
 *
 * @author vv.chekurda
 */
internal class DialogMultiSelectorViewHolderHelper :
    ViewHolderHelper<DialogSelectorItemModel, DialogMultiSelectorItemViewHolder> {

    private var colorsProvider: DialogColorsProvider? = null

    override fun createViewHolder(parentView: ViewGroup): DialogMultiSelectorItemViewHolder =
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.selection_list_dialog_item, parentView, false)
            .let { DialogMultiSelectorItemViewHolder(it, getColorsProvider(it.context)) }

    override fun bindToViewHolder(data: DialogSelectorItemModel, viewHolder: DialogMultiSelectorItemViewHolder) {
        viewHolder.bind(data)
    }

    private fun getColorsProvider(context: Context): DialogColorsProvider {
        colorsProvider = colorsProvider ?: DialogColorsProviderImpl(context)
        return colorsProvider!!
    }
}

/**
 * Реализация провайдера цветов для диалога
 */
private class DialogColorsProviderImpl(
    context: Context,
    @ColorInt override val outgoingIconColor: Int =
        ContextCompat.getColor(context, R.color.selection_dialog_item_gray_to_black_text_color),
    @ColorInt override val incomingUnreadIconColor: Int =
        ContextCompat.getColor(context, RDesign.color.text_color_accent_3),
    @ColorInt override val errorIconColor: Int =
        ContextCompat.getColor(context, RDesign.color.text_color_error),
    @ColorInt override val dialogTitle: Int =
        ContextCompat.getColor(context, RDesign.color.text_color_link_2)
) : DialogColorsProvider