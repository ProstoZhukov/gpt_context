package ru.tensor.sbis.events_tracker.storage

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.platform.generated.PageData
import ru.tensor.sbis.platform.generated.PageStatistic
import ru.tensor.sbis.statistic.StatisticScope
import ru.tensor.sbis.statistic.StatisticStorage
import ru.tensor.sbis.statistic.model.StatisticPageEvent
import ru.tensor.sbis.statistic.model.UserInfo
import timber.log.Timber
import java.util.Date

/**
 * Реализация [StatisticStorage] для статистики по страницам на базе контроллера.
 *
 * @author us.bessonov
 */
internal class ControllerPageStatisticStorage : StatisticStorage<StatisticPageEvent> {

    override suspend fun StatisticScope.save(
        event: StatisticPageEvent,
        userInfo: UserInfo?,
        createdAt: Date,
        durationInMillis: Long?
    ) {
        withContext(Dispatchers.IO) {
            try {
                val statisticData = PageData().apply {
                    logEvent = event.logEvent
                    page = event.page
                    timeToRender = durationInMillis?.toInt()
                }
                PageStatistic.instance().addRow(statisticData)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                Timber.w(e, "Error occurred while sending page statistic event")
            }
        }
    }

}