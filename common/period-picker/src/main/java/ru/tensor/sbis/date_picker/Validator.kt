package ru.tensor.sbis.date_picker

import ru.tensor.sbis.date_picker.items.Day
import ru.tensor.sbis.date_picker.range.rangeTo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

/**
 * @author mb.kruglova
 */
class Validator {

    private val format = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).apply {
        isLenient = false
    }

    /**
     * Преобразовывает строку в дату. Поддерживается следующее правило:
     * Если указанный год попадает в промежуток с "начало текущего века, 00" по "текущий год + 10", то век
     * подставляется текущий, иначе прошлый. Например, пусть текущий год 2023. Если date это "56", то строка
     * преобразуется в "1956" (не входит в промежуток с 2000 по 2023 + 10). Если date это "30", то строка преобразуется
     * в "2030" (входит в промежуток с 2000 по 2023 + 10).
     */
    private fun parseFor2DigitsYear(date: String): Calendar {
        val currentYear = GregorianCalendar().year
        val startOfCenturyYear = currentYear / 100 * 100
        val year = startOfCenturyYear + date.substring(date.length - 2, date.length).toInt()
        val startYear = if (startOfCenturyYear <= year && year <= currentYear + 10) year else year - 100
        format.set2DigitYearStart(GregorianCalendar(startYear, Calendar.JANUARY, 1).time)
        return format.parse(date)!!.toCalendar()
    }

    /**
     * Проверка корректности введенного пользователем периода, оповещение об ошибках и сохранение периода
     * @param dateTo строка начальной даты
     * @param dateTo строка конечной даты
     * @param min нижняя граница календарной сетки
     * @param max верхняя граница календарной сетки
     * @param view представление компонента выбора периода
     */
    fun parseAndValidatePeriodFromText(
        dateFrom: String,
        dateTo: String,
        min: Calendar,
        max: Calendar,
        view: DatePickerContract.View?
    ): Period {
        val isDateFromValid = validateDate(dateFrom, min, max)
        val isDateToValid = validateDate(dateTo, min, max)
        val isPeriodValid = validatePeriod(dateFrom, dateTo)

        if (isDateFromValid || dateFrom.replace(" ", "").length < 8) {
            view?.setDateFromOk()
        } else {
            view?.setDateFromError()
        }

        if ((isDateToValid && isPeriodValid) || dateTo.replace(" ", "").length < 8) {
            view?.setDateToOk()
        } else {
            view?.setDateToError()
        }

        return if (isPeriodValid && isDateFromValid) {
            if (isDateToValid) {
                Period(parseFor2DigitsYear(dateFrom), parseFor2DigitsYear(dateTo))
            } else {
                val calendar = parseFor2DigitsYear(dateFrom)
                Period(calendar, calendar)
            }
        } else {
            Period.create()
        }
    }

    /**
     * Проверка корректности введенной пользователем даты
     * @param inputDate введенная дата
     * @param min нижняя граница календарной сетки
     * @param max верхняя граница календарной сетки
     */
    private fun validateDate(inputDate: String, min: Calendar, max: Calendar): Boolean {
        if (inputDate.length != 8) {
            return false
        }

        return try {
            val date = parseFor2DigitsYear(inputDate)
            date in min..max
        } catch (t: Throwable) {
            false
        }
    }

    /**
     * Проверка корректности введенного пользователем периода (начало периода не должно быть позднее конца периода)
     * @param dateTo строка начальной даты
     * @param dateTo строка конечной даты
     */
    private fun validatePeriod(dateFrom: String, dateTo: String): Boolean {
        return try {
            parseFor2DigitsYear(dateFrom).time <= parseFor2DigitsYear(dateTo).time
        } catch (t: Throwable) {
            true
        }
    }

    /**
     * Проверка введенного периода перед подтверждением
     * @param inputPeriod введенный период
     */
    fun checkInputPeriodOrDate(inputPeriod: Period) = inputPeriod.hasFrom

    /**
     * Согласно стандарту, если после выбора первой границы периода не было выбора второй,
     * но была нажата кнопка подтверждения, то диапазон становится равным отмеченному значению.
     * @param period период
     */
    fun preparePeriodBeforeConfirmation(period: Period): Period {
        return if (!period.hasTo) {
            Period(period.dateFrom, period.fakeDateTo)
        } else {
            period
        }
    }

    /**
     * Согласно стандарту, при попытке выбора периода, содержащего в себе хотя бы одну недоступную ячейку,
     * закрывающий маркер устанавливается на последнюю доступную для выбора ячейку перед недоступной.
     * @param period период, выбранный пользователем
     * @param unavailableDays множество недоступных дней
     * @return период с учетом недоступных дней
     */
    fun excludeUnavailableDays(period: Period, unavailableDays: Set<Day>): Period {
        var from: Calendar? = null
        var to: Calendar? = null

        // поиск первого доступного дня
        for (calendar in period.dayRange!!) {
            if (!unavailableDays.contains(calendar.toDay())) {
                from = calendar
                break
            }
        }

        // определение конца периода
        from?.let {
            for (calendar in from..period.dateTo!!) {
                if (unavailableDays.contains(calendar.toDay())) {
                    break
                }
                to = calendar
            }
        }

        return Period(from, to)
    }

    /**
     * Согласно стандарту, если недоступные даты присутствуют в периоде, введенном вручную,
     * то при попытке подтверждения такого периода всплывет стандартный pop-up с предупреждением "Текущий период недоступен для выбора".
     * @param period период, введенный пользователем
     * @param unavailableDays множество недоступных дней
     * @return true - если период содержит недоступные дни
     */
    fun hasUnavailableDays(period: Period, unavailableDays: Set<Day>): Boolean {
        period.dayRange?.forEach { calendar ->
            if (unavailableDays.contains(calendar.toDay())) {
                return true
            }
        }
        return false
    }
}