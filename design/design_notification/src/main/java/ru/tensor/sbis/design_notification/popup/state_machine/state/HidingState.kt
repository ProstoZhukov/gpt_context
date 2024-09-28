package ru.tensor.sbis.design_notification.popup.state_machine.state

import androidx.annotation.VisibleForTesting
import ru.tensor.sbis.design_notification.popup.state_machine.ActionHide
import ru.tensor.sbis.design_notification.popup.state_machine.ActionPush
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationAction
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationStateMachine
import ru.tensor.sbis.design_notification.popup.state_machine.util.HideViewAction

/**
 * Состояние анимированного скрытия панели
 *
 * @author us.bessonov
 */
internal class HidingState(
    stateMachine: PopupNotificationStateMachine,
    @get:VisibleForTesting
    val hideViewAction: HideViewAction
) : PopupNotificationState(stateMachine) {

    override fun consume(action: PopupNotificationAction): PopupNotificationState {
        return when (action) {
            is ActionPush -> {
                // По окончании анимации скрытия возвращаемся в исходное состояние, из которого отображаем новую панель
                hideViewAction.onHidden = {
                    stateMachine.reset()
                    stateMachine.push(action.notification, action.duration)
                }
                HidingState(stateMachine, hideViewAction)
            }
            // Уже в процессе скрытия, не меняем состояние
            is ActionHide -> HidingState(stateMachine, hideViewAction)
        }
    }

}