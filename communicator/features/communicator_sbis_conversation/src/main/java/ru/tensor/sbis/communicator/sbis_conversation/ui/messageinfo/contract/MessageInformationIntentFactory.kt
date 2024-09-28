package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.contract

import android.content.Context
import android.content.Intent
import java.util.*

/**
 * Intent фабрика для запуска активности экрана информации о сообщении.
 *
 * @author vv.chekurda
 */
interface MessageInformationIntentFactory {

    /**
     * Создать intent активности экрана информации о сообщении.
     *
     * @param context       контекст.
     * @param dialogUuid    идентификатор диалога.
     * @param messageUuid   идентификатор сообщения.
     * @param isGroupDialog true, если сообщение принадлежит групповой переписке.
     * @param isChannel     true, если сообщение принадлежит каналу.
     * @return [Intent]
     */
    fun createIntent(
        context: Context,
        dialogUuid: UUID,
        messageUuid: UUID,
        isGroupDialog: Boolean,
        isChannel: Boolean
    ): Intent
}