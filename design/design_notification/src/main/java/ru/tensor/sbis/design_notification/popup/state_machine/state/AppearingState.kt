package ru.tensor.sbis.design_notification.popup.state_machine.state

import android.os.Handler
import androidx.core.os.HandlerCompat
import ru.tensor.sbis.design_notification.popup.state_machine.ActionHide
import ru.tensor.sbis.design_notification.popup.state_machine.ActionPush
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationAction
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationStateMachine
import ru.tensor.sbis.design_notification.popup.state_machine.util.PopupWindowViewDisplayManager
import ru.tensor.sbis.design_notification.popup.state_machine.util.ShowViewAction

/**
 * Состояние анимированного отображения панели
 *
 * @author us.bessonov
 */
internal class AppearingState(
    stateMachine: PopupNotificationStateMachine,
    private val hideRunnable: Runnable,
    private val showViewAction: ShowViewAction,
    private val handler: Handler,
    private val popupWindowViewDisplayManager: PopupWindowViewDisplayManager
) : PopupNotificationState(stateMachine) {

    /**
     *  Не меняем состояние, а целевое событие обрабатываем после окончания анимации, следом за переходом в
     *  DisplayedState
     */
    override fun consume(action: PopupNotificationAction): PopupNotificationState = when (action) {
        is ActionPush -> {
            continueAppearingAndThen {
                stateMachine.show(hideRunnable)
                stateMachine.push(action.notification, action.duration)
            }
        }
        is ActionHide -> {
            if (HandlerCompat.hasCallbacks(handler, hideRunnable)) {
                // Если скрытие уже запланировано, то выполняем скрытие по окончании показа.
                continueAppearingAndThen {
                    stateMachine.show(hideRunnable)
                    stateMachine.hide()
                }
            } else {
                // Если скрытие не было запланировано, или уже произошло, то переходим в скрытое состояние.
                DisplayedState(stateMachine, hideRunnable, handler, popupWindowViewDisplayManager)
                    .also { handler.post(stateMachine::hide) }
            }
        }
    }

    private fun continueAppearingAndThen(actionOnShown: () -> Unit) =
        AppearingState(stateMachine, hideRunnable, showViewAction, handler, popupWindowViewDisplayManager)
            .also { showViewAction.onShown = actionOnShown }
}