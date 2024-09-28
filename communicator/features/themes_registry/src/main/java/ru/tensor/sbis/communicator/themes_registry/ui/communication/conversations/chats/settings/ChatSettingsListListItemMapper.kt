package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantListItemMapOfStringString
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsContactItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.asNative
import ru.tensor.sbis.mvp.data.model.PagedListResult

/**
 * Маппер, преобразующий модель контроллера [ListResultOfThemeParticipantListItemMapOfStringString] в UI модель [ChatSettingsItem].
 * Используется для настроек участников чата.
 *
 * @author dv.baranov
 */
internal class ChatSettingsListListItemMapper(context: Context) :
    BaseModelMapper<ListResultOfThemeParticipantListItemMapOfStringString, PagedListResult<ChatSettingsItem>>(context) {

    override fun apply(rawList: ListResultOfThemeParticipantListItemMapOfStringString): PagedListResult<ChatSettingsItem> =
        PagedListResult(
            rawList.result
                .filter { it.themeParticipant != null }
                .map { ChatSettingsContactItem(it.themeParticipant!!.asNative) },
            rawList.haveMore,
        )
}
