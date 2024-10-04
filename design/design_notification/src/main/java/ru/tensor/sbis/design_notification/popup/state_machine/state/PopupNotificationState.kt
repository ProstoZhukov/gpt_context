package ru.tensor.sbis.design_notification.popup.state_machine.state

import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationAction
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationStateMachine

/**
 * Состояние [PopupNotificationStateMachine]
 *
 * @author us.bessonov
 */
internal abstract class PopupNotificationState(protected val stateMachine: PopupNotificationStateMachine) {

    /**
     * Возвращает очередное состояние после выполнения [action]
     */
    abstract fun consume(action: PopupNotificationAction): PopupNotificationState
}