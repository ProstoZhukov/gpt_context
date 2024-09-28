package calendar_date_icon

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.calendar_date_icon.CalendarDateIconController

/**
 * Тесты контроллера иконки календаря
 *
 * @author da.zolotarev
 */
class CalendarIconControllerTests {
    private lateinit var controller: CalendarDateIconController

    @Before
    fun setUp() {
        val iconSize = 24F
        controller = CalendarDateIconController(iconSize)
        controller.attach(null, null)
    }

    @Test
    fun `When set invalid calendar number, then dayNumber null`() {
        controller.dayNumber = INVALID_DATE
        Assert.assertNull(controller.dayNumber)
    }

    @Test
    fun `When set negative calendar number, then dayNumber null`() {
        controller.dayNumber = INVALID_NEGATIVE_DATE
        Assert.assertNull(controller.dayNumber)
    }

    @Test
    fun `When set zero calendar number, then dayNumber null`() {
        controller.dayNumber = INVALID_ZERO_DATE
        Assert.assertNull(controller.dayNumber)
    }

    @Test
    fun `When set valid calendar number, then dayNumber equal calendar number`() {
        controller.dayNumber = VALID_DATE
        Assert.assertEquals(VALID_DATE, controller.dayNumber)
    }

    companion object {
        const val INVALID_DATE = 32
        const val INVALID_NEGATIVE_DATE = -2
        const val INVALID_ZERO_DATE = 0
        const val VALID_DATE = 2
    }
}