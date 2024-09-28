package ru.tensor.sbis.communication_decl.communicator.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.edo_decl.document.Document
import ru.tensor.sbis.edo_decl.document.DocumentType
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Interface which provides ConversationActivityIntent and ConversationFragment.
 *
 * @author vv.chekurda
 */
interface ConversationProvider : Feature {

    /**
     * Creates intent to open Conversation Activity
     *
     * @param dialogUuid - uuid of related document.
     * @param messageUuid - text of sending message.
     * @param folderUuid - uuid of folder.
     * @param participantsUuids - array of dialog participants uuids.
     * @param files - array of file URIs.
     * @param text - text to share
     * @param document - related document
     * @param type - conversation type
     * @param archivedDialog - is dialog archived
     * @param isGroupConversation - is group dialog
     */
    fun getConversationActivityIntent(
        dialogUuid: UUID? = null,
        messageUuid: UUID? = null,
        folderUuid: UUID? = null,
        participantsUuids: ArrayList<UUID>? = null,
        files: ArrayList<Uri>? = null,
        text: String? = null,
        document: Document? = null,
        type: ConversationType? = null,
        isChat: Boolean? = null,
        archivedDialog: Boolean? = null,
        isGroupConversation: Boolean? = null
    ): Intent

    /**
     * Creates intent to open Conversation Activity
     *
     * @param dialogUuid - uuid of related document
     */
    fun getConversationActivityIntent(
        dialogUuid: UUID,
        type: ConversationType = ConversationType.REGULAR
    ): Intent =
        getConversationActivityIntent(
            dialogUuid,
            null,
            null,
            null,
            null,
            null,
            null,
            type,
            isChat = false,
            archivedDialog = false
        )

    /**
     * Creates intent to open Conversation Activity by another intent extras
     * from MainActivity
     *
     * @param extras - extras for conversation intent
     */
    fun getConversationActivityIntent(extras: Bundle): Intent

    /**
     * Создает intent для открытия диалога по документу.
     *
     * @param documentUuid - UUID связанного документа.
     * @param documentType - тип документа.
     * @param documentTitle - заголовок документа для отображения в плашке, когда от контроллера ещё нет данных.
     * @param startNewDialogAnyway - true, если необходимо создать новый диалог, даже если для этого документа уже существует другой диалог.
     */
    fun getConversationActivityIntentForDocument(
            documentUuid: String,
            documentType: DocumentType = DocumentType.UNKNOWN,
            documentTitle: String? = null,
            startNewDialogAnyway: Boolean = false
    ): Observable<Intent>

    /**
     * Creates intent to open Conversation Activity for a new conversation
     *
     * @param folderUuid - uuid of folder
     * @param files - array of file URIs
     * @param text - text to share
     * @param areRecipientsSelected - are recipients already selected
     */
    fun getNewDialogConversationActivityIntent(
        folderUuid: UUID?,
        files: ArrayList<Uri>?,
        text: String?,
        areRecipientsSelected: Boolean = false
    ): Intent

    /**
     * Получить [Intent] для открытия экрана создания нового диалога.
     * Первый этап - выбор получателей, после подтверждения выбора откроется новый диалог.
     *
     * @param folderUuid папка, в которой нужно создать диалог, можно передать null, если нужно создать в корневой.
     * @param document - related document
     * @param type - conversation type
     */
    fun getDialogCreationActivityIntent(
        folderUuid: UUID? = null,
        document: Document? = null,
        type: ConversationType? = null
    ): Intent

    /**
     * Creates Conversation Fragment
     *
     * @param dialogUuid - uuid of related document.
     * @param messageUuid - text of sending message.
     * @param folderUuid - uuid of folder.
     * @param participantsUuids - array of dialog participants uuids.
     * @param files - array of file URIs.
     * @param text - text to share
     * @param document - related document
     * @param type - conversation type
     * @param archivedDialog - is dialog archived
     */
    fun getConversationFragment(
        dialogUuid: UUID?,
        messageUuid: UUID?,
        folderUuid: UUID?,
        participantsUuids: ArrayList<UUID>?,
        files: ArrayList<Uri>?,
        text: String?,
        document: Document?,
        type: ConversationType?,
        isChat: Boolean,
        archivedDialog: Boolean
    ): Fragment

    /**
     * Создать фрагмент переписки (чат / диалог).
     * Параметры диалога будут взяты из [arguments]
     */
    fun getConversationFragment(arguments: Bundle): Fragment

    /**
     * Получить [Intent] для запуска Activity переписки.
     * @param arg @see [ConversationParams].
     */
    fun getConversationActivityIntent(arg: ConversationParams): Intent

    /**
     * Получить [Fragment] переписки.
     * @param arg @see [ConversationParams].
     */
    fun getConversationFragment(arg: ConversationParams): Fragment

    companion object {
        /**
         * Временно помещена константа, так как преимущественно используется в модуле сообщений,
         * но есть странный вариант использования в модуле ограничений (schedule).
         * По задаче https://online.sbis.ru/opendoc.html?guid=f4187db6-59ec-4482-a2b6-1cb67c2547ba
         * должна быть переделана схема работы и константу можно будет спрятать в модуле сообщений.
         */
        const val DIALOG_PARTICIPANTS_ACTIVITY_CODE = 10
        const val CONVERSATION_PARTICIPANTS_ACTIVITY_EXTRA_UUID_KEY = "profile_uuid_extra_key"
        const val CONVERSATION_PARTICIPANTS_ACTIVITY_EXTRA_STRING_KEY = "dialog_title_extra_key"
    }
}