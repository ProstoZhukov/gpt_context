package ru.tensor.sbis.communication_decl.complain.data

import java.io.Serializable
import java.util.UUID

/**
 * Sealed класс спользуемых кейсов для отправки жалобы.
 *
 * @author da.zhukov
 */
sealed interface ComplainUseCase : Serializable {

    /**
     * Жалоба на сообщение.
     * @param messageUuid идентификатор сообщения.
     * @param conversationUuid идентификатор переписки.
     * @param isChat является ли чатом.
     */
    data class ConversationMessage(
        val messageUuid: UUID,
        val conversationUuid: UUID,
        val isChat: Boolean
    ) : ComplainUseCase

    /**
     * Жалоба на переписку.
     * @param uuid идентификатор переписки.
     * @param isChat является ли чатом.
     */
    data class Conversation(
        val uuid: UUID,
        val isChat: Boolean
    ) : ComplainUseCase

    /**
     * Жалоба на комментарий.
     * @param commentUuid идентификатор комментария.
     * @param documentUuid идентификатор документа.
     */
    data class Comment(
        val commentUuid: UUID,
        val documentUuid: UUID
    ) : ComplainUseCase

    /**
     * Жалоба на персону(сотрудника).
     * @param uuid идентификатор персоны.
     */
    data class User(
        val uuid: UUID
    ) : ComplainUseCase

    /**
     * Жалоба на группу социальной сети.
     * @param uuid идентификатор группы.
     */
    data class Group(
        val uuid: UUID
    ) : ComplainUseCase

    /**
     * Жалоба на обсуждение группы социальной сети.
     * @param forumUuid идентификатор обсуждения группы социальной сети.
     * @param groupUuid идентификатор группы социальной сети.
     */
    data class Forum(
        val forumUuid: UUID,
        val groupUuid: UUID
    ) : ComplainUseCase

    /**
     * Жалоба на новость.
     * @param uuid идентификатор новости.
     */
    data class News(
        val uuid: UUID
    ) : ComplainUseCase

    /**
     * Жалоба на отзыв в sabyget.
     * @param reviewUuid идентификатор отзыва на заведение sabyget.
     * @param placeUuid идентификатор заведения sabyget.
     */
    data class SabyReview(
        val reviewUuid: UUID,
        val placeUuid: UUID
    ) : ComplainUseCase

    /**
     * Жалоба на заведение в sabyget.
     * @param placeUuid идентификатор заведения в sabyget.
     */
    data class SabyPlace(
        val placeUuid: UUID
    ) : ComplainUseCase
}