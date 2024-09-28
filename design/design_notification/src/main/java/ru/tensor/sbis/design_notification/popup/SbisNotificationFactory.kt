package ru.tensor.sbis.design_notification.popup

import android.content.Context
import android.view.View

/**
 * Фабрика для создания [View] панели-информера.
 * Реализация должна обеспечивать проверку равенства, реализовав должным образом [equals]. Фабрики, создающие [View] с
 * идентичным содержимым, должны считаться эквивалентными.
 *
 * @author us.bessonov
 */
interface SbisNotificationFactory {

    /** @SelfDocumented */
    fun createView(context: Context, closeCallback: (() -> Unit)?): View
}