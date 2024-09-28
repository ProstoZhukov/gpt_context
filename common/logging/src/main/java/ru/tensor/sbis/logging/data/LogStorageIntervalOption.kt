package ru.tensor.sbis.logging.data

import ru.tensor.sbis.logging.R

/**
 * Типы периодов хранения логов.
 *
 * @author av.krymov
 */
enum class LogStorageIntervalOption(
    val daysCount: Short,
    val descriptionRes: Int
) {
    ONE_DAY(daysCount = 1, R.string.logging_settings_log_storage_interval_day),
    TWO_DAYS(daysCount = 2, R.string.logging_settings_log_storage_interval_two_days),
    THREE_DAYS(daysCount = 3, R.string.logging_settings_log_storage_interval_three_days),
    A_WEEK(daysCount = 7, R.string.logging_settings_log_storage_interval_week)
}