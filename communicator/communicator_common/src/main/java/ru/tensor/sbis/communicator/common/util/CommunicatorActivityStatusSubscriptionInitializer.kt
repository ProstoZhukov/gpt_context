package ru.tensor.sbis.communicator.common.util

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.activity_status.generated.ActivityStatusController
import ru.tensor.sbis.person_decl.profile.PersonActivityStatusNotifier
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus
import ru.tensor.sbis.person_decl.profile.model.ProfileActivityStatus
import java.util.UUID

/**
 * Используется для предварительного создания подписок на обновление статуса, вместо их создания в onBindViewHolder.
 *
 * @author da.zhukov
 */
class CommunicatorActivityStatusSubscriptionInitializer(
    private val personActivityStatusNotifier: PersonActivityStatusNotifier
) {

    /**
     * Задать список uuid персон, обновление статуса активности которых будет запрашиваться.
     * Для запроса обновлений зарегистрированных статусов активности, используется [updateStatuses].
     * При регистрации неизвестных ранее uuid, сразу выполняется запрос обновления.
     */
    fun initialize(uuids: List<UUID>) {
        personActivityStatusNotifier.register(uuids)
        updateStatuses(true)
    }

    /**
     * Получить статус активности персоны с заданным uuid.
     */
    fun getStatus(uuid: UUID): ProfileActivityStatus =
        personActivityStatusNotifier.getStatus(uuid)

    /**
     * Запросить обновление статусов для зарегистрированных uuid персон.
     *
     * @param force Требуется ли принудительное обновление.
     */
    fun updateStatuses(force: Boolean) {
        personActivityStatusNotifier.updateStatuses(force)
    }

    /**
     * Синхронизировать статус активности пользователя с индентификатором [uuid].
     * В случае, если кэш данные утверждают, что пользователь не в сети - запрашиваем синк.
     * Такая механика необходима для сценариев переписки с печатающими пользователями,
     * тк мы не можем ждать следующей синхронизации через N минут.
     */
    fun forceUpdateActivityStatus(uuid: UUID) {
        personActivityStatusNotifier.register(uuid)
        getStatus(uuid).also { currentStatus ->
            if (currentStatus.activityStatus != ActivityStatus.ONLINE_WORK &&
                currentStatus.activityStatus != ActivityStatus.ONLINE_HOME) {
                ActivityStatusController.instance().list(arrayListOf(uuid))
            }
        }
    }

    /**
     * Подписаться на статусы активности пользователя с идентификатором [uuid].
     */
    fun observe(uuid: UUID): Flow<ProfileActivityStatus> =
        personActivityStatusNotifier.observe(uuid)
}