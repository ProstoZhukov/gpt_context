package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.factory

import android.view.View
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.base.ChatSettingsItemViewHolder

/**
 * Фабрика, определяющая создание вьюхолдеров (а соответственно и [View]) для выбранных элементов конкретных
 * [ChatSettingsItem].
 *
 * @author dv.baranov
 */
internal interface ChatSettingsItemViewHolderFactory {

    /**
     * Создать вьюхолдер для поддерживаемых типов [ChatSettingsItem].
     */
    fun createViewHolder(
        itemType: Class<out ChatSettingsItem>,
    ): ChatSettingsItemViewHolder<*>
}
