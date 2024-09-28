package ru.tensor.sbis.person_decl.profile.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Информация об активности пользователя
 *
 *  @param activityStatus Tекущий статус пользователя
 *  @param lastActivity Время последней активности пользователя
 *  @param timeZoneMinutes Сдвиг таймзоны пользователя относительно GMT в минутах
 *  @param statusDescriptionMen Текст статуса активности (мужской)
 *  @param statusDescriptionWomen Текст текст статуса активности (женский)
 *  @param activityTypeNote Текст местоположения
 */
@Parcelize
data class ProfileActivityStatus(
    val activityStatus: ActivityStatus,
    val lastActivity: Long,
    val timeZoneMinutes: Long,
    var statusDescriptionMen: String? = null,
    var statusDescriptionWomen: String? = null,
    var activityTypeNote: String? = null
) : Parcelable
