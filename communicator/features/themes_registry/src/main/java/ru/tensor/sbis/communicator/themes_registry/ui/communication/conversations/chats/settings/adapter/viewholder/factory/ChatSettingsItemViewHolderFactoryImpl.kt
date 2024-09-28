package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.factory

import android.view.ViewGroup
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsContactItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsContactItemViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsFooterItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsFooterItemViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsHeaderItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsHeaderItemViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.base.ChatSettingsItemViewHolder

/**
 * Реализация [ChatSettingsItemViewHolderFactory], поддерживающая создание вьюхолдеров для экрана
 * настроек чата.
 *
 * @param container требуется для генерации [ViewGroup.LayoutParams] из xml атрибутов.
 *
 * @author dv.baranov
 */
internal class ChatSettingsItemViewHolderFactoryImpl(
    private val container: ViewGroup,
) : ChatSettingsItemViewHolderFactory {

    override fun createViewHolder(
        itemType: Class<out ChatSettingsItem>,
    ): ChatSettingsItemViewHolder<*> =
        when (itemType) {
            ChatSettingsContactItem::class.java -> ChatSettingsContactItemViewHolder(container)
            ChatSettingsHeaderItem::class.java -> ChatSettingsHeaderItemViewHolder(container)
            ChatSettingsFooterItem::class.java -> ChatSettingsFooterItemViewHolder(container)
            else -> error(
                "Cannot create viewHolder for ${ChatSettingsItem::javaClass} " +
                    "(you cannot use this type for chat settings screen)",
            )
        }
}
