package ru.tensor.sbis.design_notification.popup.state_machine.state

import android.os.Handler
import ru.tensor.sbis.design_notification.popup.state_machine.ActionHide
import ru.tensor.sbis.design_notification.popup.state_machine.ActionPush
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationAction
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationStateMachine
import ru.tensor.sbis.design_notification.popup.state_machine.util.HideViewAction
import ru.tensor.sbis.design_notification.popup.state_machine.util.PopupWindowViewDisplayManager

/**
 * Состояние, в котором панель видима, и по умолчанию скроется по истечении таймаута
 *
 * @author us.bessonov
 */
internal class DisplayedState(
    stateMachine: PopupNotificationStateMachine,
    private val hideRunnable: Runnable,
    private val handler: Handler,
    private val popupWindowViewDisplayManager: PopupWindowViewDisplayManager
) : PopupNotificationState(stateMachine) {

    override fun consume(action: PopupNotificationAction): PopupNotificationState = when (action) {
        is ActionPush -> {
            /*
            Анимированно скрываем текущую панель, затем возвращаемся в исходное состояние, из которого отображаем новую
            панель
            */
            hideViewAndThen {
                stateMachine.reset()
                stateMachine.push(action.notification, action.duration)
            }
        }
        is ActionHide -> {
            // Анимированно скрываем панель, затем возвращаемся в исходное состояние
            hideViewAndThen {
                stateMachine.reset()
            }
        }
    }

    private fun hideViewAndThen(actionOnHidden: () -> Unit): HidingState {
        handler.removeCallbacks(hideRunnable)
        val hidingView = HideViewAction(actionOnHidden)
            .also { popupWindowViewDisplayManager.hideViews(it) }
        return HidingState(stateMachine, hidingView)
    }
}