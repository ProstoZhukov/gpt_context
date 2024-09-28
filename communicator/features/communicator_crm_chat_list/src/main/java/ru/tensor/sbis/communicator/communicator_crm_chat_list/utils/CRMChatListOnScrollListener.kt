package ru.tensor.sbis.communicator.communicator_crm_chat_list.utils

import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin.commonSingletonComponentProvider

/**
 * Слушатель для скрытия/показа ННП при скроллинге.
 *
 * @author da.zhukov
 */
internal class CRMChatListOnScrollListener : RecyclerView.OnScrollListener() {

    private val scrollHelper by lazy { commonSingletonComponentProvider.get().scrollHelper }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        scrollHelper.onScroll(dy, recyclerView.computeVerticalScrollOffset())
    }
}