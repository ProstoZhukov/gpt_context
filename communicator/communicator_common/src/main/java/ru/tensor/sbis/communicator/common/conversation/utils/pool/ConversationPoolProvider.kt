package ru.tensor.sbis.communicator.common.conversation.utils.pool

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Инициализатор пула View для реестра сообщений.
 *
 * @author vv.chekurda
 */
interface ConversationViewPoolInitializer : Feature {

    /**
     * Проинициализировать пул view реестра сообщений.
     *
     * @param fragment фрагмент, который запрашивает иницилизацию.
     */
    fun initViewPool(fragment: Fragment)
}