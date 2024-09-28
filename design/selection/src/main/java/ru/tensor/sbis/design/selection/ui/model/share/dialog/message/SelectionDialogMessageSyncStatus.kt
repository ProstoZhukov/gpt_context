package ru.tensor.sbis.design.selection.ui.model.share.dialog.message

/**
 * Статусы синхронизации релевантного сообщения диалога
 *
 * @author vv.chekurda
 */
enum class SelectionDialogMessageSyncStatus {
    /** Синхронизировано */
    SUCCEEDED,

    /** Ошибка синхронизации */
    ERROR,

    /** Любой другой статус */
    OTHER
}