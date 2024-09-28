package ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.conversation

import android.net.Uri
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationParams
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator.common.data.ConversationDetailsParams
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.*
import ru.tensor.sbis.communicator.themes_registry.router.theme.ThemeRouterInitializer
import java.util.*

/**
 * Роутер переписки реестра диалогов.
 *
 * @author vv.chekurda
 */
internal interface ThemeConversationRouter :
    ThemeRouterInitializer {

    /**
     * Идентификатор верхней открытой переписки в стеке.
     */
    val topConversation: UUID?

    /**
     * Показать экран переписки (диалога/чата).
     *
     * @param params параметры для открытия.
     * @param onCloseCallback колбэк о закрытии фрагмента.
     */
    fun showConversationDetailsScreen(
        params: ConversationDetailsParams,
        onCloseCallback: (() -> Unit)? = null
    )

    fun openConversationPreview(params: ConversationParams, list: List<ThemeConversationPreviewMenuAction>)

    /**
     * Показать новый диалог.
     * Участники будут взяты из менеджера выбора получателей.
     *
     * @param folderUuid идентификатор папки, в которой будет создан диалог.
     */
    fun showNewDialog(folderUuid: UUID)

    /**
     * Показать новый диалог для отправки контента "Поделиться".
     * Контент шаринга будет взят из интента.
     *
     * @param participants участники нового диалога.
     */
    fun showNewDialogToShare(participants: List<UUID>, text: String?, files: List<Uri>?)

    /**
     * Показать экран переписки (консультации).
     *
     * @param params параметры для открытия.
     * @param onCloseCallback колбэк о закрытии фрагмента.
     */
    fun showConsultationDetailsScreen(params: CRMConsultationParams, onCloseCallback: (() -> Unit)?)
}