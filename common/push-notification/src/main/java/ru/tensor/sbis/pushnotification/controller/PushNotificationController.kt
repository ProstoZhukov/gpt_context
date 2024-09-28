package ru.tensor.sbis.pushnotification.controller

import android.os.Bundle
import androidx.annotation.WorkerThread
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage

/**
 * Обработчик входящих пуш уведомлений по конкретному типу, при поступлении на устройство они буферизируются за
 * определенный промежуток времени и далее управление передается этому обработчику.
 * Кроме этого контроллер обрабатывает события по удалению пуш уведомления, которые перенаправляются в него
 * при попытке удалить пуш программно.
 *
 * @author am.boldinov
 */
interface PushNotificationController {

    /**
     * Возвращает уникальный тег, по которому должно быть опубликовано уведомление
     * в [ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface]
     */
    fun getNotifyTag(): String

    /**
     * Обрабатывает набор пуш-сообщений по типам, на которые был подписан обработчик.
     * В списке могут содержаться новые пуши, пуши об удалении и изменении.
     * В зависимости от типа происходит публикация или удаление с помощью [ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface]
     * Возвращает результат обработки поступившего списка уведомлений
     */
    @WorkerThread
    fun handle(messages: List<PushNotificationMessage>): HandlingResult

    /**
     * Удаляет пуш-уведомления из шторки по конкретному типу с помощью [ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface]
     */
    @WorkerThread
    fun cancelAll(type: PushType)

    /**
     * Удаляет пуш-уведомления из шторки по конкретнопу типу на основе параметров.
     * @see [ru.tensor.sbis.pushnotification.contract.PushCancelContract]
     * @see [ru.tensor.sbis.pushnotification.controller.base.strategy.PushCancelStrategy.getInnerCancelMatcher]
     */
    @WorkerThread
    fun cancel(type: PushType, params: Bundle)
}