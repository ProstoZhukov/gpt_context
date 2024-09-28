package ru.tensor.sbis.communication_decl.complain.data

/**
 * Тип сущности для жалобы.
 *
 * @author da.zhukov
 */
enum class ComplainEntityType {
    /** Пользователь */
    USER,
    /** Группа социальной сети */
    GROUP,
    /** Новость */
    NEWS,
    /** Обсуждение группы социальной сети */
    FORUM,
    /** Сообщение или комментарий */
    MESSAGE,
    /** Отзыв на заведение sabyget */
    SABYGET_REVIEW,
    /** Заведение sabyget */
    SABYGET,
    /** Диалог */
    DIALOG,
    /** Чат */
    CHAT
}