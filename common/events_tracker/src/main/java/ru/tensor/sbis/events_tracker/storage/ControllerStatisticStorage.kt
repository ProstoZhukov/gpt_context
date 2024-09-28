package ru.tensor.sbis.events_tracker.storage

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.platform.generated.ApplicationStatistic
import ru.tensor.sbis.platform.generated.StatisticData
import ru.tensor.sbis.statistic.model.StatisticEvent
import ru.tensor.sbis.statistic.StatisticScope
import ru.tensor.sbis.statistic.StatisticStorage
import ru.tensor.sbis.statistic.model.UserInfo
import timber.log.Timber
import java.util.Date

/**
 * Реализация [StatisticStorage] на базе контроллера.
 *
 * @author kv.martyshenko
 */
internal class ControllerStatisticStorage : StatisticStorage<StatisticEvent> {

    override suspend fun StatisticScope.save(
        event: StatisticEvent,
        userInfo: UserInfo?,
        createdAt: Date,
        durationInMillis: Long?
    ) {
        withContext(Dispatchers.IO) {
            try {
                val statisticData = StatisticData().apply {
                    dateTime = createdAt
                    action = event.action
                    context = event.context
                    functional = event.functional
                    account = userInfo?.clientId
                    user = userInfo?.userId
                    duration = durationInMillis
                }
                ApplicationStatistic.instance().addRow(statisticData)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                Timber.w(e, "Error occurred while sending statistic event")
            }
        }
    }

}