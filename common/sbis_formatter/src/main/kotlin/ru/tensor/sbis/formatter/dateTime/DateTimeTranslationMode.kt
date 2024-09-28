package ru.tensor.sbis.formatter.dateTime

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Аналог плюсового DateTimeTranslationMode.
 * Формат вывода локализованной даты и времени.
 * Формат для представления локализованной даты и времени. Запись изменяется в
 * зависимости от текущей страны (например: для RU формат fullDate будет
 * DD.MM.YY а для US MM/DD/YY).
 *
 * Подробнее об структуре форматов написано [здесь](https://wi.sbis.ru/docs/js/Types/formatter/methods/date/).
 *
 * @author ps.smirnyh
 */
enum class DateTimeTranslationMode {

    /** Месяц и полный год (ru_RU: 02.2023). */
    DIGITAL_MONTH_FULL_YEAR,

    /** Полная дата с полным днем недели (ru_RU: 5 февраля'23 воскресенье). */
    FULL_DATE_DOW,

    /** день (ru_RU: 5). */
    DAY,

    /** Полная дата с коротким годом (день, месяц, год)  (ru_RU: 01.01.19). */
    FULL_DATE,

    /** Полная дата с полным месяцем (ru_RU: 5 февраля'23). */
    FULL_DATE_FULL_MONTH,

    /** Полная дата с полным месяцем и полным годом (ru_RU: 5 февраля 2023). */
    FULL_DATE_FULL_MONTH_FULL_YEAR,

    /** Полная дата с полным годом (день, месяц, год) (ru_RU: 01.01.2001). */
    FULL_DATE_FULL_YEAR,

    /** Полная дата с коротким месяцем (ru_RU: 5 фев'23). */
    FULL_DATE_SHORT_MONTH,

    /** Полная дата с коротким месяцем и полным годом (ru_RU: 5 фев 2023). */
    FULL_DATE_SHORT_MONTH_FULL_YEAR,

    /** Полная дата с коротким временем (ru_RU: 05.02.23 14:50). */
    FULL_DATE_SHORT_TIME,

    /** Полная даты с полным временем (ru_RU: 05.02.23 14:50:01). */
    FULL_DATE_FULL_TIME,

    /** Полная дата с полным временем с миллисекундами (ru_RU: 05.02.23 14:50:01.123). */
    FULL_DATE_FULL_TIME_FRACTION,

    /** Полная дата с полным годом с коротким временем (ru_RU: 05.02.2023 14:50). */
    FULL_DATE_FULL_YEAR_SHORT_TIME,

    /** Полная дата с полным годом с полным временем (ru_RU: 05.02.2023 14:50:01). */
    FULL_DATE_FULL_YEAR_FULL_TIME,

    /** Полная дата с полным годом с полным временем с миллисекундами (ru_RU: 05.02.2023 14:50:01.123). */
    FULL_DATE_FULL_YEAR_FULL_TIME_FRACTION,

    /** Полная дата и время (ru_RU: 5 фев'23 14:50). */
    FULL_DATETIME,

    /** Полный месяц и год (ru_RU: Февраль'23). */
    FULL_MONTH,

    /** Название месяца (ru_RU: Февраль). */
    MONTH,

    /** Короткое название месяца (ru_RU: Фев). */
    SHR_MONTH,

    /** Компонент даты год (ru_RU: 2023). */
    FULL_YEAR,

    /** Время часы, минуты и секунды (15:12:32). */
    FULL_TIME,

    /** Время часы, минуты, секунды, миллисекунды (08:33:32.217). */
    FULL_TIME_FRACTION,

    /** Дата в формате YYYY-MM-DDTHH:mm:ss.SSSZZ. */
    ISO_DATETIME,

    /** Дата в формате YYYY-MM-DD HH:mm:ss.SSSZZ. */
    ISO_DATETIME_SQL,

    /** Время минуты и секунды (15:12). */
    SHORT_TIME,

    /** Формат короткой даты с полным днем недели (ru_RU: 5 февраля, воскресенье). */
    SHORT_DATE_DOW,

    /** Формат короткой даты с коротким днем недели (ru_RU: 5 февраля, вс). */
    SHORT_DATE_SHORT_DOW,

    /** Формат короткой даты с коротким днем недели и коротким месяцем (ru_RU: 5 фев, вс). */
    SHORT_DATE_SHORT_MONTH_SHORT_DOW,

    /** Короткий формат даты (день, месяц)  (ru_RU: 05.02). */
    SHORT_DATE,

    /** Короткий формат даты с полным месяцем (ru_RU: 5 февраля). */
    SHORT_DATE_FULL_MONTH,

    /** Короткий формат даты с коротким месяцем (ru_RU: 5 фев). */
    SHORT_DATE_SHORT_MONTH,

    /** Короткая дата, короткое время (ru_RU: 05.02 14:50). */
    SHORT_DATE_SHORT_TIME,

    /** Короткая дата, полное время (ru_RU: 05.02 14:50:01). */
    SHORT_DATE_FULL_TIME,

    /** Короткая дата, полное время с миллисекундами (ru_RU: 05.02 14:50:01.123). */
    SHORT_DATE_FULL_TIME_FRACTION,

    /** Формат короткой даты и времени (ru_RU: 5 фев 14:50). */
    SHORT_DATETIME,

    /** Короткое название месяца и год (ru_RU: Фев'23). */
    SHORT_MONTH,

    /** Полный формат квартала в римской нотации (ru_RU: I квартал'23). */
    @RequiresApi(Build.VERSION_CODES.N)
    FULL_QUARTER,

    /** Короткий формат квартала (ru_RU: 01'23). */
    @RequiresApi(Build.VERSION_CODES.N)
    SHORT_QUARTER,

    /** Номер квартала (ru_RU: I). */
    @RequiresApi(Build.VERSION_CODES.N)
    QUARTER

}