package ru.tensor.sbis.communication_decl.communicator.share

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Интерфейс типизированных аргументов для шаринга сообщений.
 *
 * @author da.zhukov
 */
sealed interface ShareMessagesArgs : Parcelable {

    /**
     * Текст для шаринга.
     */
    val shareText: String

    /**
     * Файлы для шаринга.
     */
    val shareFiles: List<Uri>
}

/**
 * Типизированные аргументы для шаринга контактам.
 */
@Parcelize
data class ContactsArgs(
    override val shareText: String,
    override val shareFiles: List<Uri>
) : ShareMessagesArgs

/**
 * Типизированные аргументы для шаринга в диалог.
 */
@Parcelize
data class DialogsArgs(
    override val shareText: String,
    override val shareFiles: List<Uri>
) : ShareMessagesArgs

/**
 * Типизированные аргументы для шаринга в канал.
 */
@Parcelize
data class ChannelsArgs(
    override val shareText: String,
    override val shareFiles: List<Uri>
) : ShareMessagesArgs

/**
 * Интерфейс типизированных аргументов для шаринга сообщений по DirectShare.
 *
 * @author da.zhukov
 */
sealed interface DirectShareArgs : ShareMessagesArgs {

    /**
     * Идентификатор directShare(канал/контакт).
     */
    val directShareUuid: UUID
}

/**
 * Типизированные аргументы для шаринга контакту по DirectShare.
 */
@Parcelize
data class ContactDirectShareArgs(
    override val directShareUuid: UUID,
    override val shareText: String,
    override val shareFiles: List<Uri>
) : DirectShareArgs

/**
 * Типизированные аргументы для шаринга каналу по DirectShare.
 */
@Parcelize
data class ConversationDirectShareArgs(
    override val directShareUuid: UUID,
    override val shareText: String,
    override val shareFiles: List<Uri>
) : DirectShareArgs