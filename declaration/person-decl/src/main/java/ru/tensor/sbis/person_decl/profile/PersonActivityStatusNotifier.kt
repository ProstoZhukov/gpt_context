package ru.tensor.sbis.person_decl.profile

import androidx.annotation.MainThread
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus
import ru.tensor.sbis.person_decl.profile.model.ProfileActivityStatus
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Предназначен для подписки на обновление статусов активности и запросов обновления статусов для зарегистрированных
 * uuid персон.
 * Обновление статуса для всех зарегистрированных uuid осуществляется вручную, посредством метода [updateStatuses].
 * Нет гарантии, что любой запрос обновления будет выполнен, и выполнен немедленно, поскольку предусмотрена фильтрация
 * слишком частых запросов обновлений, т.к. это требует обращения к облаку.
 *
 * @author us.bessonov
 */
interface PersonActivityStatusNotifier : Feature {

    /**
     * Задать список uuid персон, обновление статуса активности которых будет запрашиваться.
     * Для запроса обновлений зарегистрированных статусов активности, используйте [updateStatuses].
     * При регистрации неизвестных ранее uuid, сразу выполняется запрос обновления.
     */
    @MainThread
    fun register(personUuids: List<UUID>)

    /**
     * @see [register]
     */
    @MainThread
    fun register(vararg personUuids: UUID)

    /**
     * Получить [Flow] с обновлениями статуса активности для персоны с заданным uuid.
     * Для запроса обновлений зарегистрированных статусов активности, используйте [updateStatuses].
     */
    @MainThread
    fun observe(uuid: UUID): Flow<ProfileActivityStatus>

    /**
     * Получить статус активности персоны с заданным uuid.
     * Если статус активности отсутствует в in-memory кэше, вернёт значение по умолчанию - [ActivityStatus.UNKNOWN].
     */
    @MainThread
    fun getStatus(uuid: UUID): ProfileActivityStatus

    /**
     * Запросить обновление статусов для зарегистрированных uuid персон.
     *
     * @param force Требуется ли принудительное обновление.
     * Непринудительное обновление возможно не чаще, чем раз в минуту. Принудительное обновление может производиться
     * чаще, но при условии, что список зарегистрированных персон изменился после последнего обновления.
     *
     * @see [register]
     */
    @MainThread
    fun updateStatuses(force: Boolean)
}