package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantMapOfStringString
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsContactItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.asNative
import ru.tensor.sbis.mvp.data.model.PagedListResult

/**
 * Маппер, преобразующий модель контроллера [ListResultOfThemeParticipantMapOfStringString] в UI модель [ChatSettingsItem].
 * Используется для настроек админов чата.
 *
 * @author dv.baranov
 */
internal class ChatSettingsListMapper(context: Context) :
    BaseModelMapper<ListResultOfThemeParticipantMapOfStringString, PagedListResult<ChatSettingsItem>>(context) {

    override fun apply(rawList: ListResultOfThemeParticipantMapOfStringString): PagedListResult<ChatSettingsItem> =
        PagedListResult(
            rawList.result.map { ChatSettingsContactItem(it.asNative) },
            rawList.haveMore,
        )
}
