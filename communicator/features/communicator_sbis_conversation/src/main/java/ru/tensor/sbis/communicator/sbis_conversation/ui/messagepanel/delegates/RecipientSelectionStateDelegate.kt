package ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel.delegates

import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResult

/**
 * Делегат для распознавания открыт/не открыт выбор получателей из панели сообщений,
 * из-за сломанного свайпбэком жизненного цикла - невозможно адекватно восстанавливать клавиатуру при повороте экрана
 * или возвращении на него, т.к. у активностей в стеке при повороте экрана отрабатывает даже onResume -> onPause,
 * а при открытии новой активности с опусканием клавиатуры из панели - onKeyboardCloseMeasure не срабатывает
 */
internal class RecipientSelectionStateDelegate: ConversationRecipientSelectionState {

    private var isRecipientSelectionFinished: Boolean = true

    /** @SelfDocumented */
    override fun isRecipientSelectionClosed(): Boolean = isRecipientSelectionFinished

    /** @SelfDocumented */
    override fun onRecipientSelectionResult(selectionResult: RecipientSelectionResult) {
        isRecipientSelectionFinished = selectionResult.isCanceled || selectionResult.isSuccess
    }
}

/** @SelfDocumented */
internal interface ConversationRecipientSelectionState {

    fun onRecipientSelectionResult(selectionResult: RecipientSelectionResult)

    fun isRecipientSelectionClosed(): Boolean
}