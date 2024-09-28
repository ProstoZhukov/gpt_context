package ru.tensor.sbis.person_decl.status.bl

import androidx.annotation.WorkerThread
import ru.tensor.sbis.person_decl.status.model.ServiceAccessErrorStatus
import ru.tensor.sbis.person_decl.status.model.Status

/**
 * Интерфейс описывает методы БЛ для модуля "Статус"
 */
interface StatusDataSource {

    /** получить текущий статус пользователя */
    fun getStatus(): Status
    /**
     * @param callback эта функция будет вызвана при внесении изменений
     */
    fun addOnChangeCallback(callback: Runnable)

    /**
     * @param callback удаление подписки на внесение изменений
     */
    fun removeOnChangeCallback(callback: Runnable)

    /**
     * @param callback эта функция будет вызвана при ошибке сервиса
     */
    fun addServiceAccessErrorCallback(callback: (ServiceAccessErrorStatus, String) -> Unit)

    /**
     * @param callback удаление подписки на ошибку сервиса
     */
    fun removeServiceAccessErrorCallback(callback: (ServiceAccessErrorStatus, String) -> Unit)

    /** Добавить подписку [callback] на ошибку установки статуса */
    fun addOnSetCurrentErrorCallback(callback: (Int, String) -> Unit)

    /** Удалить подписку [callback] на ошибку установки статуса */
    fun removeOnSetCurrentErrorCallback(callback: (Int, String) -> Unit)

    /** Добавить подписку [callback] на изменение типа статуса */
    fun addOnStatusTypeChangedCallback(callback: (Boolean, Long) -> Unit?)

    /** Удалить подписку [callback] на изменение типа статуса */
    fun removeOnStatusTypeChangedCallback(callback: (Boolean, Long) -> Unit?)

    /** Добавить подписку [callback] на доступность изменения статуса */
    fun addStatusChangeabilitySettingChangeCallback(callback: (Boolean) -> Unit?)

    /** Удалить подписку [callback] на доступность изменения статуса */
    fun removeStatusChangeabilitySettingChangeCallback(callback: (Boolean) -> Unit?)

    /**
     * метод позволяет получить список доступных статусов
     * @return список статусов
     */
    @WorkerThread
    fun getAvailableStatuses(): List<Status>

    /**
     * метод позволяет вызывать коллбек [OnGetCurrentStatusChangedCallback]  и получить текущий статус пользователя
     * @return статус
     */
    @WorkerThread
    fun getCurrentStatusCallback(): Status

    /**
     * Получение статуса-"заглушки" (https://online.sbis.ru/doc/8aa43638-0ce0-4992-aa5d-17056d401244)
     * @return статус
     */
    @WorkerThread
    fun getStubStatus(): Status

    /** Проверка возможности изменения статуса */
    @WorkerThread
    fun getStatusChangeAvailability(): Boolean
}
