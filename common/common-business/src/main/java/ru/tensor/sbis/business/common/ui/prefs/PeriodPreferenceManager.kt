package ru.tensor.sbis.business.common.ui.prefs

import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import ru.tensor.sbis.business.common.ui.utils.formatUTC
import ru.tensor.sbis.business.common.ui.utils.isTheSameDay
import ru.tensor.sbis.business.common.ui.utils.parseUTCFormat
import ru.tensor.sbis.business.common.ui.utils.period.CurrentAndPastPeriod
import ru.tensor.sbis.common.util.dateperiod.DatePeriod
import timber.log.Timber
import java.util.Calendar
import java.util.Date

/**
 * Базовый класс, выполняющий сохранение и восстановление значения [DatePeriod] посредством [SharedPreferences]
 * для разных модулей. Переопределение [prefPrefix] позволяет держать данные
 * под уникальными ключами, которые не затирают друг друга.
 *
 * @property preferences экземпляр [SharedPreferences]
 * @property prefPrefix - уникальный префикс для ключей сохранения периодов
 * @property prefPeriodFrom - по которому будет сохранено значение начала периода
 * @property prefPeriodTo - по которому будет сохранено значение конца периода
 * @property prefPastPeriodFrom - по которому будет сохранено значение начала прошлого периода
 * @property prefPastPeriodTo - по которому будет сохранено значение конца прошлого периода
 * @property prefPeriodValidity - по которому будет сохранено значение актуальности данных
 */
open class PeriodPreferenceManager(
    private val preferences: SharedPreferences,
    private val prefPrefix: String
) {
    @VisibleForTesting(otherwise = PRIVATE)
    val prefPeriodFrom: String = "${prefPrefix}PREF_PERIOD_FROM"

    @VisibleForTesting(otherwise = PRIVATE)
    val prefPeriodTo: String = "${prefPrefix}PREF_PERIOD_TO"

    @VisibleForTesting(otherwise = PRIVATE)
    val prefPastPeriodFrom: String = "${prefPrefix}PREF_PAST_PERIOD_FROM"

    @VisibleForTesting(otherwise = PRIVATE)
    val prefPastPeriodTo: String = "${prefPrefix}PREF_PAST_PERIOD_TO"

    private val prefPeriodValidity: String = "${prefPrefix}PREF_PERIOD_VALIDITY"
    private val comparedPeriodKeys: List<String> = listOf(
        prefPeriodFrom,
        prefPeriodTo,
        prefPastPeriodFrom,
        prefPastPeriodTo
    )
    private val periodKeys: List<String> = listOf(prefPeriodFrom, prefPeriodTo)

    /**
     * Сохраняет выбранные периоды сравнения (прошлый и текущий).
     *
     * @param datePeriods выбранные периоды
     */
    fun savePeriods(datePeriods: CurrentAndPastPeriod) {
        savePeriod(datePeriods.current)
        savePreviousPeriod(datePeriods.past)
    }

    /**
     * Сохраняет выбранный период.
     *
     * @param datePeriod выбранный период
     */
    fun savePeriod(datePeriod: DatePeriod) {
        updatePeriodValidity()
        preferences.edit()
            .putString(prefPeriodFrom, formatUTC(datePeriod.utcFrom))
            .putString(prefPeriodTo, formatUTC(datePeriod.utcTo))
            .apply()
    }

    /**
     * Восстанавливает сохранённый период
     *
     * @param defaultPeriod период по умолчанию, если не был сохранён ранее
     * @return восстановленный период
     */
    fun restorePeriod(
        defaultPeriod: () -> DatePeriod = { DatePeriod.getDefaultInstance() }
    ): DatePeriod {
        val dates = preferences.restore(periodKeys)
        val period = if (dates != null && dates.size == periodKeys.size) {
            DatePeriod.fromUtc(dates.component1(), dates.component2())
        } else {
            defaultPeriod()
        }
        return if (isPeriodExpired()) {
            DatePeriod().toType(period.type)
        } else {
            period
        }
    }

    /**
     * Восстанавливает сохранённые периоды сравнения
     *
     * @param defaultPeriod текущий период по умолчанию, если не был сохранён ранее
     * @return восстановленные периоды
     */
    fun restorePeriods(
        defaultPeriod: () -> DatePeriod = { DatePeriod.getDefaultInstance() }
    ): CurrentAndPastPeriod {
        val dates = preferences.restore(comparedPeriodKeys)
        val periods = if (dates != null && dates.size == comparedPeriodKeys.size) {
            CurrentAndPastPeriod(
                DatePeriod.fromUtc(dates.component1(), dates.component2()),
                DatePeriod.fromUtc(dates.component3(), dates.component4())
            )
        } else {
            CurrentAndPastPeriod(
                current = defaultPeriod(),
                past = defaultPeriod().shiftedBy(Calendar.YEAR, -1)
            )
        }
        return if (isPeriodExpired()) {
            val current = DatePeriod().toType(periods.current.type)
            val past = current.shiftedBy(Calendar.YEAR, -1)
            savePreviousPeriod(past)
            Timber.d("Substitute expired periods with: current $current, past $past")
            CurrentAndPastPeriod(current = current, past = past)
        } else {
            Timber.d("Restore periods: current ${periods.current}, past ${periods.past}")
            periods
        }
    }

    /**
     * Очистить данные о сохраненных периодах
     */
    fun reset() = preferences.edit().clear().apply()

    private fun savePreviousPeriod(datePeriod: DatePeriod) = preferences.edit()
        .putString(prefPastPeriodFrom, formatUTC(datePeriod.utcFrom))
        .putString(prefPastPeriodTo, formatUTC(datePeriod.utcTo))
        .apply()

    private fun SharedPreferences.restore(periodKeys: List<String>): List<Date>? {
        if (!periodKeys.all(::contains)) {
            Timber.d("Attempt to restore incomplete range of periods")
            return null
        }
        val dates = periodKeys.mapNotNull { key ->
            val utc = getString(key, null)
            utc?.let { parseUTCFormat(it) }
        }
        return if (dates.size == periodKeys.size) {
            dates
        } else {
            Timber.d("Restored inappropriate periods where dates are $dates")
            null
        }
    }

    private fun updatePeriodValidity() =
        preferences.edit()
            .putLong(prefPeriodValidity, System.currentTimeMillis())
            .apply()

    private fun isPeriodExpired(): Boolean = preferences.run {
        if (contains(prefPeriodValidity)) {
            return Date(getLong(prefPeriodValidity, System.currentTimeMillis()))
                .isTheSameDay(Date())
                .not()
        }
        return false
    }
}