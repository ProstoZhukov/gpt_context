package ru.tensor.sbis.communicator.communicator_files.mapper

import android.view.ViewGroup
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileData
import ru.tensor.sbis.communicator.communicator_files.utils.CommunicatorFileClickListener
import ru.tensor.sbis.communicator.communicator_files.utils.CommunicatorFilesAttachmentViewPool
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import ru.tensor.sbis.communicator.communicator_files.R

/**
 * Класс `CommunicatorFilesHolderHelper` является вспомогательным классом для управления
 * ViewHolder элементами, используемыми в RecyclerView.
 *
 * @property viewPool Пул представлений, используемый для управления и переиспользования представлений элементов.
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesHolderHelper(
    private val viewPool: CommunicatorFilesAttachmentViewPool,
) : ViewHolderHelper<CommunicatorFileData, CommunicatorFilesHolder> {

    override fun createViewHolder(parentView: ViewGroup): CommunicatorFilesHolder {
        return CommunicatorFilesHolder(parentView.context, viewPool.getCommunicatorFilesItemView())
    }

    override fun bindToViewHolder(data: CommunicatorFileData, viewHolder: CommunicatorFilesHolder) {
        viewHolder.bind(data)
        viewHolder.itemView.setTag(R.id.attachment_origin_tag, data.actionData.first().attachmentOrigin)
    }
}