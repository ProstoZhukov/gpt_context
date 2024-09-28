package ru.tensor.sbis.design.selection.ui.model.share.dialog.message

/**
 * Типы релевантного сообщения диалога
 *
 * @author vv.chekurda
 */
enum class SelectionDialogRelevantMessageType {
    /** Обычное сообщение */
    MESSAGE,

    /** Черновик */
    DRAFT,

    /** Сообщение в процессе отправки */
    SENDING,

    /** Любой другой тип */
    OTHER
}