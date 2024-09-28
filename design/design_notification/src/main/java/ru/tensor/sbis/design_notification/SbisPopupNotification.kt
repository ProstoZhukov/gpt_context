package ru.tensor.sbis.design_notification

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design_notification.popup.SbisInfoNotificationFactory
import ru.tensor.sbis.design_notification.popup.SbisNotificationFactory
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationStateMachine
import ru.tensor.sbis.design_notification.popup.state_machine.util.DisplayDuration

/**
 * Сервис для отображения компонента "Панель-информер".
 *
 *  - [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=панель-информер_v2&g=1)
 *
 * @author us.bessonov
 */
object SbisPopupNotification {

    private val stateMachine = PopupNotificationStateMachine()

    /**
     * Отображает панель-информер стандартного вида.
     *
     * @param fragment фрагмент (возможно, диалоговый), поверх которого должна отображаться панель.
     * Если указать фрагмент не на переднем плане, то панель-информер может оказаться перекрыта.
     *
     * @see [SbisInfoNotificationFactory]
     */
    fun push(
        type: SbisPopupNotificationStyle,
        message: String,
        icon: String? = null,
        duration: DisplayDuration = DisplayDuration.Default
    ) = push(SbisInfoNotificationFactory(type, message, icon), duration)

    /**
     * Отображает панель-информер, [View] которой создаётся посредством [notification].
     *
     */
    fun push(
        notification: SbisNotificationFactory,
        duration: DisplayDuration = DisplayDuration.Default
    ) = stateMachine.push(notification, duration)

    /**
     * Скрывает текущую панель-информер.
     */
    fun hide() {
        stateMachine.hide()
    }

    /**
     * Отображает [Toast] с указанным текстом.
     */
    @JvmStatic
    @JvmOverloads
    fun pushToast(context: Context, message: CharSequence, duration: Int = Toast.LENGTH_LONG): Toast =
        ToastManager.pushToast(context, message, duration)

    /**
     * Отображает [Toast] с текстом, определённом в строковом ресурсе.
     */
    @JvmStatic
    @JvmOverloads
    fun pushToast(context: Context, @StringRes message: Int, duration: Int = Toast.LENGTH_LONG): Toast =
        pushToast(context, context.getString(message), duration)

    // region DEPRECATED

    /**
     * Отображает панель-информер стандартного вида.
     *
     * @param fragment фрагмент (возможно, диалоговый), поверх которого должна отображаться панель.
     * Если указать фрагмент не на переднем плане, то панель-информер может оказаться перекрыта.
     *
     * @see [SbisInfoNotificationFactory]
     */
    @Deprecated(
        "Неактуально",
        ReplaceWith("push(SbisPopupNotificationStyle, String, String, DisplayDuration)")
    )
    @JvmOverloads
    fun push(
        fragment: Fragment,
        type: SbisPopupNotificationStyle,
        message: String,
        icon: String? = null,
        duration: DisplayDuration = DisplayDuration.Default
    ) = push(fragment, SbisInfoNotificationFactory(type, message, icon), duration)

    /**
     * Отображает панель-информер, [View] которой создаётся посредством [notification].
     *
     * @param fragment фрагмент (возможно, диалоговый), поверх которого должна отображаться панель.
     * Если указать фрагмент не на переднем плане, то панель-информер может оказаться перекрыта.
     */
    @Deprecated(
        "Неактуально",
        ReplaceWith("push(SbisNotificationFactory, DisplayDuration)")
    )
    fun push(
        fragment: Fragment,
        notification: SbisNotificationFactory,
        duration: DisplayDuration = DisplayDuration.Default
    ) = push(fragment.requireContext(), notification, duration)

    /**
     * Отображает панель-информер стандартного вида.
     * Метод нужно использовать только если нет доступа к [Fragment], и устраивает отображение панели в окне [Activity].
     *
     * @see [SbisInfoNotificationFactory]
     */
    @Deprecated(
        "Неактуально",
        ReplaceWith("push(SbisPopupNotificationStyle, String, String, DisplayDuration)")
    )
    @JvmOverloads
    fun push(
        context: Context,
        type: SbisPopupNotificationStyle,
        message: String,
        icon: String? = null,
        duration: DisplayDuration = DisplayDuration.Default
    ) = push(context, SbisInfoNotificationFactory(type, message, icon), duration)

    /**
     * Отображает панель-информер, [View] которой создаётся посредством [notification].
     * Метод нужно использовать только если нет доступа к [Fragment], и устраивает отображение панели в окне [Activity].
     */
    @Deprecated(
        "Неактуально",
        ReplaceWith("push(SbisNotificationFactory, DisplayDuration)")
    )
    fun push(
        context: Context,
        notification: SbisNotificationFactory,
        duration: DisplayDuration = DisplayDuration.Default
    ) = push(notification, duration)

    // endregion
}