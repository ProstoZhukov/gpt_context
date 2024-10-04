package ru.tensor.sbis.design_notification.popup.state_machine

import android.os.Handler
import android.os.Looper
import ru.tensor.sbis.design_notification.popup.SbisNotificationFactory
import ru.tensor.sbis.design_notification.popup.state_machine.state.DefaultState
import ru.tensor.sbis.design_notification.popup.state_machine.state.DisplayedState
import ru.tensor.sbis.design_notification.popup.state_machine.state.PopupNotificationState
import ru.tensor.sbis.design_notification.popup.state_machine.util.DisplayDuration
import ru.tensor.sbis.design_notification.popup.state_machine.util.PopupWindowViewDisplayManager

/**
 * Машина состояний, обеспечивающая поведение компонента "Панель-информер"
 *
 * @author us.bessonov
 */
internal class PopupNotificationStateMachine(private val handler: Handler = Handler(Looper.getMainLooper())) {

    private var lastFactory: SbisNotificationFactory? = null

    private val popupWindowViewDisplayManager = PopupWindowViewDisplayManager()

    var state: PopupNotificationState = DefaultState(this, handler, popupWindowViewDisplayManager)
        private set

    /**
     * Отображает очередную панель-информер
     */
    fun push(notification: SbisNotificationFactory, duration: DisplayDuration) {
        if (state !is DefaultState && notification == lastFactory) return
        lastFactory = notification
        consumeAction(ActionPush(handler, notification, duration))
    }

    /**
     * Скрывает текущую панель-информер
     */
    fun hide() {
        consumeAction(ActionHide)
    }

    /**
     * Устанавливает исходное состояние, в котором панель не отображается
     */
    fun reset() {
        state = DefaultState(this, handler, popupWindowViewDisplayManager)
    }

    /**
     * Устанавливает состояние, в котором завершена анимация показа панели
     */
    fun show(hideRunnable: Runnable) {
        state = DisplayedState(this, hideRunnable, handler, popupWindowViewDisplayManager)
    }

    private fun consumeAction(action: PopupNotificationAction) {
        val oldState = state
        state.consume(action)
            .takeIf { state == oldState }
            ?.let { state = it }
    }
}

