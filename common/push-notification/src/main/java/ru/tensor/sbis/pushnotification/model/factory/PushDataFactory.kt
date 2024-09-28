package ru.tensor.sbis.pushnotification.model.factory

import ru.tensor.sbis.pushnotification.model.PushData
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage

/**
 * Фабрика по созданию прикладных моделей с данными по пуш-сообщению.
 *
 * @author am.boldinov
 */
interface PushDataFactory<T : PushData> {

    /**
     * Создает прикладную модель данных.
     * Реаилизация метода подразумевает парсинг прикладных json данных по пушу
     * и создание готовой модели для отображения.
     *
     * @param message поступившее на устройство пуш-сообщение
     */
    fun create(message: PushNotificationMessage): T
}