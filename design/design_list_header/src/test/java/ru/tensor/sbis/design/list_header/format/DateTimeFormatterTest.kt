package ru.tensor.sbis.design.list_header.format

import android.content.Context
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.joda.time.LocalDateTime
import org.junit.Assert
import org.junit.Test
import ru.tensor.sbis.common.util.date.DateFormatTemplate
import ru.tensor.sbis.common.util.date.DateFormatUtils
import java.util.Random
import ru.tensor.sbis.design.R as RDesign

/**
 * Наборт отформатированных дат для разных случаев
 * @author Roman Petrov (ra.petrov)
 */
data class DateSet(
    /**
     * Если дата - текущий день
     */
    val currentDay: String,
    /**
     * Если дата - в этом году
     */
    val currentYear: String,

    /**
     * Если дата относится к году, отличному от текущего
     */
    val otherYear: String,

    /**
     * Время, если день - текущий
     */
    val currentDayTime: String,

    /**
     * Время, если день не текущий
     */
    val notСurrentDayTime: String
)

class DateTimeFormatterTest {

    /**
     * Вспомогательный класс для тестов форматтеров
     * Проверяет кейсы, но надо предоставить наборы отформатированных дат для разных случаев
     * Время - всегда форматируется одинаково
     */
    class FormatterTestHelper(private val formatter: ListDateFormatter) {
        /**
         * Форматирвоание заголовка (или первой даты, идущей в списке)
         */
        private lateinit var header: DateSet

        /**
         * Если дата и предидующая дата относятся к одному дню
         */
        private lateinit var theSameDay: DateSet

        /**
         * Если дата и предидующая дата относятся к разным дням
         */
        private lateinit var notTheSameDay: DateSet

        /**
         * Метод устанавливает наборы дат
         */
        fun setUp(header: DateSet, theSameDay: DateSet, notTheSameDay: DateSet) {
            this.header = header
            this.theSameDay = theSameDay
            this.notTheSameDay = notTheSameDay
        }

        fun testAll() {
            `Given date, then format equals expected`()
            `Given date and previewDate where previewDate is the same day, then format equals expected`()
            `Given date and previewDate where previewDate is not the same day, then format equals expected`()
        }

        private fun `Given date, then format equals expected`() {
            formatter.format(THIS_DAY.toDate()).apply {
                Assert.assertEquals(header.currentDay, date)
                Assert.assertEquals(header.currentDayTime, time)
            }

            formatter.format(THIS_YEAR.toDate()).apply {
                Assert.assertEquals(header.currentYear, date)
                Assert.assertEquals(header.notСurrentDayTime, time)
            }

            formatter.format(OTHER_YEAR.toDate()).apply {
                Assert.assertEquals(header.otherYear, date)
                Assert.assertEquals(header.notСurrentDayTime, time)
            }
        }

        private fun `Given date and previewDate where previewDate is the same day, then format equals expected`() {
            formatter.format(THIS_DAY.toDate(), THIS_DAY.minusHours(1).toDate()).apply {
                Assert.assertEquals(theSameDay.currentDay, date)
                Assert.assertEquals(theSameDay.currentDayTime, time)
            }

            formatter.format(THIS_YEAR.toDate(), THIS_YEAR.minusHours(1).toDate()).apply {
                Assert.assertEquals(theSameDay.currentYear, date)
                Assert.assertEquals(theSameDay.notСurrentDayTime, time)
            }

            formatter.format(OTHER_YEAR.toDate(), OTHER_YEAR.minusHours(1).toDate()).apply {
                Assert.assertEquals(theSameDay.otherYear, date)
                Assert.assertEquals(theSameDay.notСurrentDayTime, time)
            }
        }

        private fun `Given date and previewDate where previewDate is not the same day, then format equals expected`() {
            formatter.format(THIS_DAY.toDate(), THIS_DAY.minusDays(1).toDate()).apply {
                Assert.assertEquals(notTheSameDay.currentDay, date)
                Assert.assertEquals(notTheSameDay.currentDayTime, time)
            }

            formatter.format(THIS_YEAR.toDate(), THIS_YEAR.minusDays(1).toDate()).apply {
                Assert.assertEquals(notTheSameDay.currentYear, date)
                Assert.assertEquals(notTheSameDay.notСurrentDayTime, time)
            }

            formatter.format(OTHER_YEAR.toDate(), OTHER_YEAR.minusDays(1).toDate()).apply {
                Assert.assertEquals(notTheSameDay.otherYear, date)
                Assert.assertEquals(notTheSameDay.notСurrentDayTime, time)
            }
        }
    }

    companion object {
        val random = Random()

        private val thisYearDateFormat = DateFormatUtils.getFormatter(DateFormatTemplate.DATE_WITHOUT_YEAR.template)
        private val notThisYearDateFormat =
            DateFormatUtils.getFormatter(DateFormatTemplate.DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR.template)

        private val NOW = LocalDateTime.now()
        private val THIS_DAY = LocalDateTime(NOW.year, NOW.monthOfYear, NOW.dayOfMonth, 13, 54)
        private val THIS_YEAR = LocalDateTime(
            NOW.year,
            random.nextInt(11) + 1,
            (NOW.dayOfMonth + 29) % 28 + 1, // Отталкиваемся от текущей даты (день + 1 с поправкой на диапазон),
            // чтобы не сгенерировать текущую дату случайно
            13,
            54
        )
        private val OTHER_YEAR =
            LocalDateTime(1990 + random.nextInt(30), THIS_YEAR.monthOfYear, THIS_YEAR.dayOfMonth, 13, 54, 5)
    }

    /**
     * Тест для ListDateFormatter.DateTime
     * @see ListDateFormatter.DateTime
     */
    @Test
    fun testDateTimeFormatter() {
        FormatterTestHelper(ListDateFormatter.DateTime()).apply {
            setUp(
                header = DateSet(
                    thisYearDateFormat.format(THIS_DAY.toDate()),
                    thisYearDateFormat.format(THIS_YEAR.toDate()),
                    notThisYearDateFormat.format(OTHER_YEAR.toDate()),
                    "13:54",
                    "13:54"
                ),
                theSameDay = DateSet("", "", "", "13:54", "13:54"),
                notTheSameDay = DateSet(
                    thisYearDateFormat.format(THIS_DAY.toDate()),
                    thisYearDateFormat.format(THIS_YEAR.toDate()),
                    notThisYearDateFormat.format(OTHER_YEAR.toDate()),
                    "13:54",
                    "13:54"

                )
            )
            testAll()
        }
    }

    /**
     * Тест для ListDateFormatter.DateTimeWithToday
     * @see ListDateFormatter.DateTimeWithToday
     */
    @Test
    fun testDateTimeWithTodayFormatter() {
        val context: Context = mock { on { getString(RDesign.string.design_date_today) } doReturn "Сегодня" }

        FormatterTestHelper(ListDateFormatter.DateTimeWithToday(context)).apply {
            setUp(
                header = DateSet(
                    "Сегодня",
                    thisYearDateFormat.format(THIS_YEAR.toDate()),
                    notThisYearDateFormat.format(OTHER_YEAR.toDate()),
                    "13:54",
                    "13:54"
                ),
                theSameDay = DateSet(
                    "",
                    thisYearDateFormat.format(THIS_YEAR.toDate()),
                    notThisYearDateFormat.format(OTHER_YEAR.toDate()),
                    "13:54",
                    ""
                ),
                notTheSameDay = DateSet(
                    "",
                    thisYearDateFormat.format(THIS_YEAR.toDate()),
                    notThisYearDateFormat.format(OTHER_YEAR.toDate()),
                    "13:54",
                    ""
                )
            )
            testAll()
        }
    }

    /**
     * Тест для ListDateFormatter.DateWithMonth
     * @see ListDateFormatter.DateWithMonth
     */
    @Test
    fun testDateWithMonthFormatter() {
        FormatterTestHelper(ListDateFormatter.DateWithMonth()).apply {
            val otherMonth = DateFormatUtils.format(THIS_YEAR.toDate(), DateFormatTemplate.DATE_WITHOUT_YEAR)
            val otherYear = DateFormatUtils.format(OTHER_YEAR.toDate(), DateFormatTemplate.DATE_SPLIT_BY_POINTS)
            val dataSet = DateSet("", otherMonth, otherYear, "13:54", "")
            setUp(
                header = dataSet,
                theSameDay = dataSet,
                notTheSameDay = dataSet
            )
            testAll()
        }
    }
}