package ru.tensor.sbis.pushnotification.controller

import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage


/**
 * Обработчик входящих пуш уведомлений по конкретному типу,
 * при поступлении на устройство они буферизируются за
 * определенный промежуток времени и далее управление
 * передается этому обработчику.
 *
 * Поддерживает только обработку получения списка пуш уведомлений.
 *
 * @author am.boldinov
 */
fun interface PushActionController {

    /**
     * Обрабатывает набор пуш-сообщений по типам, на которые был подписан обработчик.
     *
     * @param list список пуш уведомлений
     */
    fun handle(list: MutableList<PushNotificationMessage>)
}
