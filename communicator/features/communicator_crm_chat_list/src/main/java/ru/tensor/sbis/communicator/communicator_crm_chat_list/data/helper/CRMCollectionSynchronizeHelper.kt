package ru.tensor.sbis.communicator.communicator_crm_chat_list.data.helper

import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.CRMChatListCollectionObserver

/**
 * Хелпер для пердотвращения обработки событий старой коллекции.
 *
 * @author da.zhukov
 */
internal class CRMCollectionSynchronizeHelper {

    /** @SelfDocumented */
    var observer: CRMChatListCollectionObserver? = null

    /** @SelfDocumented */
    fun cancel() {
        observer?.cancel()
    }
}