package ru.tensor.sbis.design_notification.popup.state_machine

import android.os.Handler
import ru.tensor.sbis.design_notification.popup.SbisNotificationFactory
import ru.tensor.sbis.design_notification.popup.state_machine.util.DisplayDuration

/**
 * События, меняющие состояние [PopupNotificationStateMachine]
 *
 * @author us.bessonov
 */
internal sealed class PopupNotificationAction

/**
 * Событие отображения новой панели
 */
internal class ActionPush(
    val handler: Handler,
    val notification: SbisNotificationFactory,
    val duration: DisplayDuration
) : PopupNotificationAction()

/**
 * Событие скрытия текущей панели
 */
internal object ActionHide : PopupNotificationAction()