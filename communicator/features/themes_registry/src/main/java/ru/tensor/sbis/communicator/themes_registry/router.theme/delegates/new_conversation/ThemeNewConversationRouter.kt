package ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.new_conversation

import ru.tensor.sbis.communicator.themes_registry.router.theme.ThemeRouterInitializer
import java.util.*

/**
 * Роутер создания новой переписки - диалога/чата в реестре диалогов
 *
 * @author vv.chekurda
 */
internal interface ThemeNewConversationRouter :
    ThemeRouterInitializer {

    /**
     * Показать выбор получателей для создания нового диалога
     *
     * @param folderUuid идентификатор папки, в которой будет создан диалог.
     */
    fun showNewDialogRecipientSelection(folderUuid: UUID? = null)

    /**
     * Показать экран создания нового чата
     */
    fun showNewChatCreation()
}