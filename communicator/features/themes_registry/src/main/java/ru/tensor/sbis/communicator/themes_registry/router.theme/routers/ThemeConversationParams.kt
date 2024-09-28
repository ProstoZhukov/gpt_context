package ru.tensor.sbis.communicator.themes_registry.router.theme.routers

import ru.tensor.sbis.communicator.common.data.theme.ConversationModel

/**
 * Модель параметров переписки для роутинга
 *
 * @property model            модель переписки
 * @property isChatTab        true, если текущая вкладка - чаты
 * @property isSearchEmpty    true, если строка поиска пустая
 * @property isArchivedDialog true, если переписка из архива (является удаленной)
 *
 * @author vv.chekurda
 */
internal data class ThemeConversationParams(
    val model: ConversationModel,
    val isChatTab: Boolean = false,
    val isSearchEmpty: Boolean = true,
    val isArchivedDialog: Boolean = false,
)