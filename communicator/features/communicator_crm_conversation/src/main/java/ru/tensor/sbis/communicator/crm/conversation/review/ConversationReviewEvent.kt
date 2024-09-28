package ru.tensor.sbis.communicator.crm.conversation.review
import ru.tensor.sbis.review.ReviewEvent

/** События чатов/консультации для публикации в сервис оценок */
enum class ConversationReviewEvent : ReviewEvent {
    /** Действие на "Завершить" */
    COMPLETE_CONSULTATION
}