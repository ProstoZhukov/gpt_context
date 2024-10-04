package ru.tensor.sbis.design_notification.popup.state_machine.state

import android.os.Handler
import ru.tensor.sbis.design_notification.popup.state_machine.ActionHide
import ru.tensor.sbis.design_notification.popup.state_machine.ActionPush
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationAction
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationStateMachine
import ru.tensor.sbis.design_notification.popup.state_machine.util.PopupWindowViewDisplayManager
import ru.tensor.sbis.design_notification.popup.state_machine.util.ShowViewAction

/**
 * Состояние, в котором панель не отображается
 *
 * @author us.bessonov
 */
internal class DefaultState(
    stateMachine: PopupNotificationStateMachine,
    private val handler: Handler,
    private val popupWindowViewDisplayManager: PopupWindowViewDisplayManager
) : PopupNotificationState(stateMachine) {

    override fun consume(action: PopupNotificationAction) = when (action) {
        is ActionPush -> {
            val hideRunnable = Runnable {
                stateMachine.hide()
            }
            val showViewAction = ShowViewAction {
                // По умолчанию, по окончании анимации показа окажемся в DisplayedState
                stateMachine.show(hideRunnable)
            }
            val onDetached = { stateMachine.reset() }
            popupWindowViewDisplayManager.deployView(
                action.duration,
                action.handler,
                showViewAction,
                hideRunnable,
                onDetached
            ) {
                action.notification.createView(it) {
                    stateMachine.hide()
                }
            }
            AppearingState(stateMachine, hideRunnable, showViewAction, handler, popupWindowViewDisplayManager)
        }
        // Скрывать нечего, не меняем состояние
        is ActionHide -> DefaultState(stateMachine, handler, popupWindowViewDisplayManager)
    }
}