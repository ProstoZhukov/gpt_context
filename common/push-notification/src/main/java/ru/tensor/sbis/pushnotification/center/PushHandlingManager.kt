package ru.tensor.sbis.pushnotification.center

import android.content.Intent
import android.os.Bundle
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.controller.PushActionController
import ru.tensor.sbis.pushnotification.controller.PushHandler
import ru.tensor.sbis.pushnotification.controller.PushNotificationController
import ru.tensor.sbis.pushnotification.util.SwipeOutHelper

/**
 * Интерфейс менеджера для обработки пуш уведомлений
 *
 * @author ev.grigoreva
 */
interface PushHandlingManager {

    /**
     * Устанавливает для заданного типа пуш уведомления немедленную обработку сразу после получения
     */
    fun setInstantHandlingForType(type: PushType)

    /**
     * Отменяет для заданного типа пуш уведомления немедленную обработку сразу после получения
     */
    fun resetInstantHandlingForType(type: PushType)

    /**
     * Регистрирует обработчик пуш-уведомления, который перехватывает сообщение от Messaging Service
     * в том виде, в котором оно поступило на устройство
     */
    fun registerPushHandler(handler: PushHandler)

    /**
     * Удаляет обработчик пуш-уведомления, обработчик перестает перехватывать сообщения от Messaging Service
     */
    fun unregisterPushHandler(handler: PushHandler)

    /**
     * Регистрирует прикладной обработчик пуш-уведомления, в который будут прилетать уже готовые события и данные по пушам.
     *
     * Наиболее предпочтительный вариант использования если необходимо пользователю показать уведомление в шторке,
     * а так же реагировать на программное удаление этого уведомления.
     * Можно использовать один и тот же инстанс обработчика для нескольких типов пушей, если они относятся к одному уведомлению.
     */
    fun registerNotificationController(type: PushType, controller: PushNotificationController)

    /**
     * Удаляет прикладной обработчик пуш-уведомления.
     */
    fun unregisterNotificationController(type: PushType)

    /**
     * Регистрирует прикладной обработчик пуш-уведмления, в который будут прилетать готовые данные по пушам.
     * Можно совмещать с [registerNotificationController] и регистрировать разные обработчики на одни и те же типы.
     *
     * Наиболее предпочтительный вариант использования если необходимо обработать данные по пушу без показа уведомления в шторке,
     * а выполнив какое-то действие (фоновая работа с БД, синхронизация данных, поднятие кастомных overlay экранов)
     */
    fun registerActionController(type: PushType, controller: PushActionController)

    /**
     * Удаляет прикладной обработчик действий над пуш-уведомлением.
     */
    fun unregisterActionController(type: PushType)

    /**
     * Обрабатывает пуш-сообщение от системного Messaging Service.
     * Преобразует поступившее сообщение в типизированную модель данных и рассылает подписчикам, с учетом буферизации.
     */
    fun handleMessage(message: Map<String, String>)

    /**
     * Удаляет все пуш-уведомления из шторки и очищает весь кеш пуш-уведомлений.
     */
    fun cancelAll()

    /**
     * Удаляет пуш-уведомления из шторки и очищает кеш пуш-уведомлений по набору типов.
     */
    fun cancelAll(types: Set<PushType>)

    /**
     * Удаляет пуш-уведомления из шторки и очищает кеш пуш-уведомлений по конкретному типу.
     */
    fun cancelAll(type: PushType)

    /**
     * Инициирует удаление пуш уведомления по конкретному типу на основе параметров.
     * Метод может быть полезен, если необходимо удалить не все пуш-уведомления по типу, а уведомления
     * по конкретным документам.
     * @see [ru.tensor.sbis.pushnotification.contract.PushCancelContract]
     */
    fun cancel(type: PushType, params: Bundle)

    /**
     * Обрабатывает событие смахивания пуш-уведомления в шторке для удаления его из кеша.
     * @see SwipeOutHelper
     * @see [ru.tensor.sbis.pushnotification.receiver.DismissNotificationReceiver]
     */
    fun onSwipeOut(intent: Intent)
}