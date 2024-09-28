package ru.tensor.sbis.common_attachments

/**
 * Временный класс, будет удален после того, как удалят [AttachmentsContainer]
 * Состояние видимости панели вложений
 */
enum class AttachmentsViewVisibility {
    /*** Вложение отображается. */
    VISIBLE,
    /*** Вложение отображается частично. */
    PARTIALLY,
    /*** Вложение не отображается. */
    GONE
}