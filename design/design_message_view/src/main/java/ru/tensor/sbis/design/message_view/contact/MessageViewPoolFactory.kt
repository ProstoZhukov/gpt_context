package ru.tensor.sbis.design.message_view.contact

import android.content.Context
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.design.cloud_view.content.utils.BaseMessageResourceHolder
import ru.tensor.sbis.design.cloud_view.content.utils.MessageResourcesHolder
import ru.tensor.sbis.design.message_view.ui.MessageView
import ru.tensor.sbis.design.message_view.utils.MessageViewPool

/**
 * Фабрика для создания пула [MessageViewPool] для [MessageView].
 *
 * @author vv.chekurda
 */
interface MessageViewPoolFactory {

    /**
     * Создать [MessageViewPool] для [MessageView].
     *
     * @param context контекст
     * @param mediaPlayer плеер для проигрывания медиа сообщений
     * @param messageResourcesHolder вспомогательная реализация для переопределения цветов для контента.
     */
    fun createMessageViewPool(
        context: Context,
        mediaPlayer: MediaPlayer? = null,
        messageResourcesHolder: MessageResourcesHolder = BaseMessageResourceHolder(context)
    ): MessageViewPool
}