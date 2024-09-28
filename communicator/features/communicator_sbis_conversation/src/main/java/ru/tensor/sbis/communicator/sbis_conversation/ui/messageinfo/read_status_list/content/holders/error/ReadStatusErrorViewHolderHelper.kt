package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.error

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.communicator.base.conversation.R
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Inject

/**
 * Вспомогательный класс для создания холдеров ошибки сети для списка статусов прочитанности
 * @see [DataBindingViewHolderHelper]
 *
 * @author vv.chekurda
 */
internal class ReadStatusErrorViewHolderHelper @Inject constructor()
    : ViewHolderHelper<Any, ViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): ViewHolder =
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.communicator_base_conersation_item_list_paging_error, parentView, false)
            .let(::NetworkErrorViewHolder)

    override fun bindToViewHolder(data: Any, viewHolder: ViewHolder) = Unit

    private class NetworkErrorViewHolder(view: View) : ViewHolder(view)
}