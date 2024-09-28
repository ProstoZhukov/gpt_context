package ru.tensor.sbis.pushnotification.controller.base

import android.content.Context
import ru.tensor.sbis.pushnotification.controller.PushNotificationController
import ru.tensor.sbis.pushnotification.di.PushNotificationComponentProvider

/**
 * Базовый класс обработчика пуш-уведомлений, предоставляющий все зависимости по умолчанию, необходимые для
 * обработки и показа пушей.
 *
 * @property context контекст приложения
 * @property notificationManager менеджер для публикации, удаления уведомлений в шторку
 * @property pushBuildingHelper утилита по созданию и стилизации пушей по умолчанию
 * @property pushIntentHelper утилита для обработки действий по нажатию на пуши
 * @property repository интерфейс для работы с кешем пуш-уведомлений
 *
 * @author am.boldinov
 */
abstract class AbstractNotificationController(
    protected val context: Context
) : PushNotificationController {

    private val component by lazy { PushNotificationComponentProvider.get(context) }

    protected val notificationManager get() = component.getNotificationManager()
    protected val pushBuildingHelper get() = component.getPushBuildingHelper()
    protected val pushIntentHelper get() = component.getPushIntentHelper()
    protected val repository get() = component.getPushNotificationRepository()

}