package ru.tensor.sbis.common.util

import android.content.Context
import android.content.res.Resources
import ru.tensor.sbis.common.R
import ru.tensor.sbis.common.util.date.BaseDateUtils
import ru.tensor.sbis.common.util.date.DateFormatTemplate
import ru.tensor.sbis.common.util.date.DateFormatUtils
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus
import ru.tensor.sbis.person_decl.profile.model.Gender
import ru.tensor.sbis.person_decl.profile.model.ProfileActivityStatus
import java.util.*
import java.util.concurrent.TimeUnit

object ActivityStatusUtil {

    private const val MOSCOW_TIME_ZONE_GMT_SHIFT_MINUTES = 3 * 60

    /**
     * Получение статуса активности профиля для отображения в тулбаре карточки профиля.
     * Если пользователь онлайн - "В сети"
     * Если был в течение дня - "Был/Была в HH:mm +4 MSK"
     * Если был в течение года - "Был/Была dd.MM в HH:mm +4 MSK"
     * Если ранее - "Был/Была более года назад"
     * Если актиновсти не было - "Не был/а активен/активна"
     *
     *
     * @param resources ресурсы
     * @param status статус активности пользователя
     * @param gender пол персоны
     * @return отформатированный текст для отображения
     */
    @JvmStatic
    fun getActivityStateText(resources: Resources, status: ProfileActivityStatus, gender: Gender): String {
        when (status.activityStatus) {
            ActivityStatus.ONLINE_WORK,
            ActivityStatus.ONLINE_HOME -> return resources.getString(R.string.common_profile_user_online_work_and_home)
            else                       -> {
                val activityStateTextBuilder = StringBuilder()

                if (status.lastActivity == 0L) {
                    return activityStateTextBuilder.append(resources.getString(
                        when {
                            gender === Gender.FEMALE -> R.string.common_woman_last_activity_time_unknown
                            else -> R.string.common_man_last_activity_time_unknown
                        }))
                        .toString()
                }

                // разница между часовыми поясами текущего пользователя и пользователя по которому получаем статус
                val activityOffset = TimeUnit.MINUTES.toMillis(status.timeZoneMinutes) -
                        BaseDateUtils.getTimeZoneOffset().toLong()
                val activityDate = Date(status.lastActivity + activityOffset)
                activityStateTextBuilder.append(resources.getString(
                    when {
                        gender === Gender.FEMALE -> R.string.common_profile_user_activity_status_woman
                        else -> R.string.common_profile_user_activity_status_man
                    }))

                when {
                    DateFormatUtils.isTheSameDay(Date(), activityDate) -> activityStateTextBuilder
                        .append(resources.getString(R.string.common_time_at))
                        .append(DateFormatUtils.format(activityDate, DateFormatTemplate.ONLY_TIME))
                        .appendTimeZone(activityOffset, status.timeZoneMinutes, resources)

                    activityDate.after(Calendar.getInstance().also { it.add(Calendar.YEAR, -1) }.time) -> activityStateTextBuilder
                        .append(" ")
                        .append(DateFormatUtils.format(activityDate, DateFormatTemplate.DATE_WITHOUT_YEAR))
                        .append(resources.getString(R.string.common_time_at))
                        .append(DateFormatUtils.format(activityDate, DateFormatTemplate.ONLY_TIME))
                        .appendTimeZone(activityOffset, status.timeZoneMinutes, resources)

                    else -> activityStateTextBuilder
                        .append(resources.getString(R.string.common_profile_user_offline_more_than_year))
                }

                return activityStateTextBuilder.toString()
            }
        }
    }

    @JvmStatic
    fun getActivityStateText(context: Context, activityStatus: ProfileActivityStatus, gender: Gender): String =
        getActivityStateText(context.resources, activityStatus, gender)

    @JvmStatic
    fun getActivityStateDeniedText(context: Context): String =
        context.getString(R.string.common_person_last_activity_time_denied)

    private fun StringBuilder.appendTimeZone(activityOffset: Long, timeZoneMinutes: Long, resources: Resources) {
        // не добавляем ничего если у пользователей одинаковые часовые пояса
        if (activityOffset == 0L) return
        val moscowTimeZoneMinutes = timeZoneMinutes - MOSCOW_TIME_ZONE_GMT_SHIFT_MINUTES
        val sign = if (moscowTimeZoneMinutes > 0) "+" else ""
        val timeZone = when {
            moscowTimeZoneMinutes == 0L       -> ""
            // учитываем часовые пояса со сдвигом на полчаса
            moscowTimeZoneMinutes % 60 == 30L -> "${moscowTimeZoneMinutes.toInt() / 60}.5"
            else                              -> "${moscowTimeZoneMinutes.toInt() / 60}"
        }
        append(" $sign$timeZone")
            .append(resources.getString(R.string.common_profile_moscow_timezone))
    }
}