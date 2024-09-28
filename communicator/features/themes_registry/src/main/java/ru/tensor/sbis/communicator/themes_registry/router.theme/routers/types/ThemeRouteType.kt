package ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types

import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.generated.ChatType
import ru.tensor.sbis.communicator.generated.DocumentType

/**
 * Типы навигационного маршрута реестра диалогов.
 * @property predicate условие типа.
 *
 * @author vv.chekurda
 */
internal enum class ThemeRouteType(
    private val predicate: ConversationModel.() -> Boolean
) {
    CONVERSATION( { isConversation } ),

    CONSULTATION( { isConsultation }),

    NEWS( { isNews && documentUuid != null } ),

    PROFILE( { isUserProfile } ),

    ARTICLE_DISCUSSION( { isArticleDiscussion && documentUuid != null } ),

    QUESTION_DISCUSSION( { isQuestionDiscussion && documentUuid != null } ),

    VIOLATION( { isViolation }),

    SOCNET_EVENT( { isSocnetEvent && documentUrl != null } ),

    WEB_VIEW( { isGroupSuggestion } ),

    NOTIFICATION( { isNotice } ),

    UNKNOWN( { false } );

    /**
     * Проверить на принадлежность модели переписки к текущему типу маршрута.
     */
    fun isTheSameType(conversation: ConversationModel): Boolean =
        predicate.invoke(conversation)
}

/**
 * true, если это карточка нарушения.
 */
internal val ConversationModel.isViolation
    get() = documentType == DocumentType.INFRACTION

/**
 * true, если это профиль сотрудника.
 */
internal val ConversationModel.isUserProfile
    get() = isSocnetEvent && documentType == DocumentType.PERSON_PROFILE

/**
 * true, если обсуждение статьи.
 */
internal val ConversationModel.isArticleDiscussion
    get() = documentType == DocumentType.GROUP_DISCUSSION_TOPIC

/**
 * true, если обсуждение вопроса.
 */
internal val ConversationModel.isQuestionDiscussion
    get() = documentType == DocumentType.GROUP_DISCUSSION_QUESTION

/**
 * true, если предложение.
 */
internal val ConversationModel.isGroupSuggestion
    get() = documentType == DocumentType.GROUP_SUGGESTIONS

/**
 * true, если переписка.
 */
internal val ConversationModel.isConversation
    get() = !isNews && !isNotice && (!isSocnetEvent || documentUrl == null) && !isGroupSuggestion && !isConsultation &&
        !isQuestionDiscussion && !isArticleDiscussion

/**
 * true, если консультация.
 */
internal val ConversationModel.isConsultation
    get() = chatType == ChatType.CONSULTATION