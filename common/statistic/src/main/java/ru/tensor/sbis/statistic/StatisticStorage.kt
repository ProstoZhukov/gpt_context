package ru.tensor.sbis.statistic

import ru.tensor.sbis.statistic.model.UserInfo
import java.util.Date

/**
 * Контракт хранилища статистики.
 *
 * @author kv.martyshenko
 */
interface StatisticStorage<EVENT> {

    /**
     * Метод сохранения события статистики.
     *
     * @param event событие.
     * @param userInfo информация о действующем пользователе.
     * @param createdAt дата создания события.
     * @param durationInMillis длительность события в миллисекундах.
     */
    suspend fun StatisticScope.save(
        event: EVENT,
        userInfo: UserInfo?,
        createdAt: Date,
        durationInMillis: Long? = null
    )

}