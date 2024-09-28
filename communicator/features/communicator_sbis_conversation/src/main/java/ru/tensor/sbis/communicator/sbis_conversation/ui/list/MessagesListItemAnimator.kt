package ru.tensor.sbis.communicator.sbis_conversation.ui.list

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

/**
 * Аниматор для списка сообщений.
 *
 * На текущий момент должен анимировать только раскрытие и сворачивание контента в ячейках списка.
 * Для таких ячеек необходимо передавать [ExpandPayload] в качестве пэйлоада при раскрытии или сворачивании
 */
internal class MessagesListItemAnimator : DefaultItemAnimator() {

    private var expandPosition = 0

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        dispatchAnimationFinished(holder)
        return false
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        if (oldHolder === newHolder && preInfo is ExpandItemHolderInfo) {
            // Позиция раскрывающейся ячейки будет сброшена на следующий ранлуп,
            // чтобы успели запуститься анимации смещения более ранних сообшений в animateMove
            oldHolder.itemView.post {
                expandPosition = 0
            }
        }
        dispatchAnimationFinished(oldHolder)
        dispatchAnimationFinished(newHolder)
        return false
    }

    override fun animateMove(holder: RecyclerView.ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        if (expandPosition > 0 && holder.bindingAdapterPosition < expandPosition) {
            return super.animateMove(holder, fromX, fromY, toX, toY)
        }
        dispatchAnimationFinished(holder)
        return false
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        dispatchAnimationFinished(holder)
        return false
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        dispatchAnimationFinished(oldHolder)
        dispatchAnimationFinished(newHolder)
        return false
    }

    override fun recordPreLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder,
        changeFlags: Int,
        payloads: MutableList<Any>
    ): ItemHolderInfo {
        var info = super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
        // При сворачивании или разворачивании контента в ячейке будут сдвинуты более ранние сообщения -
        // необходимо разрешить анимацию animateMove для них.
        // Передаем информацию в animateChange, где позиция будет сброшена
        if (changeFlags == FLAG_CHANGED && payloads.firstOrNull() == ExpandPayload && viewHolder.bindingAdapterPosition > 0) {
            expandPosition = viewHolder.bindingAdapterPosition
            info = ExpandItemHolderInfo().apply {
                left = info.left
                right = info.right
                top = info.top
                bottom = info.bottom
            }
        }
        return info
    }

    object ExpandPayload

    private class ExpandItemHolderInfo : ItemHolderInfo()
}