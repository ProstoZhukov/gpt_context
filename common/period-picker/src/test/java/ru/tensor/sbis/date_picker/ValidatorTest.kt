package ru.tensor.sbis.date_picker

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockedConstruction
import org.mockito.Mockito.mockConstruction
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import java.util.Calendar
import java.util.GregorianCalendar

class ValidatorTest {

    private val validator = Validator()

    private val view = mock<DatePickerContract.View> {
        on { setDateFromOk() } doAnswer { }
        on { setDateFromError() } doAnswer { }
        on { setDateToOk() } doAnswer {}
        on { setDateToError() } doAnswer { }
    }

    private val minDate = GregorianCalendar(1960, 0, 1)

    private val maxDate = GregorianCalendar(2260, 0, 1)

    private lateinit var gregorianCalendarCreationMock: MockedConstruction<GregorianCalendar>

    @Before
    fun setUp() {
        gregorianCalendarCreationMock = mockConstruction(GregorianCalendar::class.java) { mock, context ->
            val args = context.arguments()
            val year: Int
            val month: Int
            val day: Int
            if (args.size == 3) {
                year = args[0] as Int
                month = args[1] as Int
                day = args[2] as Int
            } else {
                year = 2123
                month = Calendar.MAY
                day = 2
            }
            val calendar = GregorianCalendar(year, month, day)
            mock.stub {
                on { get(Calendar.YEAR) } doAnswer { calendar.get(Calendar.YEAR) }
                on { time } doAnswer { calendar.time }
                on { time = any() } doAnswer { calendar.time = it.getArgument(0) }
                on { compareTo(any()) } doAnswer { calendar.compareTo(it.getArgument(0)) }
            }
        }
    }

    @After
    fun termDown() {
        gregorianCalendarCreationMock.close()
    }

    @Test
    fun parseAndValidatePeriodFromText_previousCentury() {
        val result = validator.parseAndValidatePeriodFromText(
            "02.05.60",
            "",
            min = minDate,
            max = maxDate,
            view = view,
        )
        assertEquals(GregorianCalendar(2060, Calendar.MAY, 2).time, result.dateFrom?.time)
    }

    @Test
    fun parseAndValidatePeriodFromText_currentCentury() {
        val result = validator.parseAndValidatePeriodFromText(
            "01.02.33",
            "",
            min = minDate,
            max = maxDate,
            view = view,
        )
        assertEquals(result.dateFrom?.time, GregorianCalendar(2133, Calendar.FEBRUARY, 1).time)
    }

    @Test
    fun parseAndValidatePeriodFromText_wrongInput() {
        val result = validator.parseAndValidatePeriodFromText(
            "+-. \t.:;",
            "",
            min = minDate,
            max = maxDate,
            view = view,
        )
        assertEquals(result.dateFrom?.time, null)
    }
}