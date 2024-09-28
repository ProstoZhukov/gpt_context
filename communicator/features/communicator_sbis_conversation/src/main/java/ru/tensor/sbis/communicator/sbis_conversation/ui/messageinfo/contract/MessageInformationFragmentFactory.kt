package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.contract

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.presentation.MessageInformationFragment
import java.util.*

/**
 * Фабрика фрагмента информации о сообщении.
 *
 * @author vv.chekurda
 */
interface MessageInformationFragmentFactory {

    /**
     * Создать фрагмент информации о сообщении.
     *
     * @param dialogUuid    идентификатор диалога.
     * @param messageUuid   идентификатор сообщения.
     * @param isGroupDialog true, если сообщение принадлежит групповой переписке.
     * @param isChannel     true, если сообщение принадлежит каналу.
     * @return [MessageInformationFragment]
     */
    fun createMessageInformationFragment(
        dialogUuid: UUID,
        messageUuid: UUID,
        isGroupDialog: Boolean,
        isChannel: Boolean
    ): Fragment

    /**
     * Создать фрагмент информации о сообщении.
     *
     * @param args аргументы фрагмента.
     * @return [MessageInformationFragment]
     */
    fun createMessageInformationFragment(args: Bundle): Fragment
}