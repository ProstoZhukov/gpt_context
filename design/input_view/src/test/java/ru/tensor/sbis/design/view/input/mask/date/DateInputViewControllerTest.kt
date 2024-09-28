package ru.tensor.sbis.design.view.input.mask.date

import android.content.Context
import android.os.Build
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.BaseInputViewTextWatcher
import ru.tensor.sbis.design.view.input.base.MaskEditText
import ru.tensor.sbis.design.view.input.base.ValidationStatus
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

/**
 * Класс для тестирования [DateInputViewController].
 *
 * @author ps.smirnyh
 */
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class DateInputViewControllerTest {

    private lateinit var mockBaseInputView: BaseInputView

    private var context: Context = ApplicationProvider.getApplicationContext()

    private var maskEditText: MaskEditText = MaskEditText(context)

    private lateinit var mockSingleLineController: SingleLineInputViewController

    private lateinit var dateInputViewController: DateInputViewController

    @Before
    fun setUp() {
        context.theme.applyStyle(ru.tensor.sbis.design.R.style.AppGlobalTheme, true)
        mockBaseInputView = mock()
        mockSingleLineController = mock()
        whenever(mockSingleLineController.context).doReturn(context)
        doReturn(maskEditText).whenever(mockSingleLineController).inputView
        doReturn(mock<BaseInputViewTextWatcher>()).whenever(mockSingleLineController).valueChangedWatcher
        dateInputViewController = DateInputViewController(mockSingleLineController)
        dateInputViewController.attach(mockBaseInputView, null, 0, 0)
    }

    @Test
    fun `When minDate is set and new date is less minDate then validationStatus is Error`() {
        dateInputViewController.minDate = Date(120, 0, 1, 0, 0)
        // act
        maskEditText.setText("12.05.19")
        dateInputViewController.updateOnFocusChanged(false)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        val arg = ArgumentCaptor.forClass(ValidationStatus::class.java)
        verify(mockSingleLineController, atLeastOnce()).validationStatus = arg.capture()
        assertTrue(arg.value is ValidationStatus.Error)
    }

    @Test
    fun `When maxDate is set and new date is greater maxDate then validationStatus is Error`() {
        dateInputViewController.maxDate = Date(120, 0, 1, 0, 0)
        // act
        maskEditText.setText("12.05.21 00:00")
        dateInputViewController.updateOnFocusChanged(false)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        val arg = ArgumentCaptor.forClass(ValidationStatus::class.java)
        verify(mockSingleLineController, atLeastOnce()).validationStatus = arg.capture()
        assertTrue(arg.value is ValidationStatus.Error)
    }

    @Test
    fun `When year doesn't enter then year is auto completed`() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR).mod(2000)
        // act
        maskEditText.setText("12.05.")
        dateInputViewController.updateOnFocusChanged(false)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertTrue(maskEditText.text?.startsWith("12.05.$year") == true)
    }

    @Test
    fun `When month and year don't enter then month and year are auto completed`() {
        val currentDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 12)
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
        val strDate: String = dateFormat.format(currentDate.time)
        // act
        maskEditText.setText("12.")
        dateInputViewController.updateOnFocusChanged(false)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertTrue(maskEditText.text?.startsWith(strDate) == true)
    }

    @Test
    fun `Set value by Calendar`() {
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
        val strDate: String = dateFormat.format(currentDate.time)

        // act
        dateInputViewController.updateValue(currentDate)
        dateInputViewController.updateOnFocusChanged(false)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertTrue(maskEditText.text.toString() == strDate)
    }

    @Test
    fun `Get value as Calendar if mask is DATE_TIME_MASK`() {
        val currentDate = GregorianCalendar(2012, 11, 12, 13, 45)

        // act
        maskEditText.setText("12.12.12 13:45")
        dateInputViewController.updateOnFocusChanged(false)
        val date = dateInputViewController.getDate()
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertNotNull(date)
        assertTrue(currentDate.timeInMillis == date!!.timeInMillis)
    }

    @Test
    fun `Get value as Calendar if mask is DATE_MASK`() {
        val currentDate = GregorianCalendar(2012, 11, 12)

        // act
        dateInputViewController.mask = "00.00.00"
        maskEditText.setText("12.12.12")
        dateInputViewController.updateOnFocusChanged(false)
        val date = dateInputViewController.getDate()
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertNotNull(date)
        assertTrue(currentDate.timeInMillis == date!!.timeInMillis)
    }

    @Test
    fun `Get value as Calendar if mask is DATE_MASK and year belongs to the previous century`() {
        val currentDate = GregorianCalendar(1945, 11, 12)

        // act
        dateInputViewController.mask = "00.00.00"
        maskEditText.setText("12.12.45")
        dateInputViewController.updateOnFocusChanged(false)
        val date = dateInputViewController.getDate()
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertNotNull(date)
        assertTrue(currentDate.timeInMillis == date!!.timeInMillis)
    }

    @Test
    fun `Get value as Calendar if mask is DATE_FULL_YEAR_MASK`() {
        val currentDate = GregorianCalendar(2012, 11, 12)

        // act
        dateInputViewController.mask = "00.00.0000"
        maskEditText.setText("12.12.2012")
        dateInputViewController.updateOnFocusChanged(false)
        val date = dateInputViewController.getDate()
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertNotNull(date)
        assertTrue(currentDate.timeInMillis == date!!.timeInMillis)
    }

    @Test
    fun `Get value as Calendar if mask is DATE_WITHOUT_YEAR_MASK`() {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val currentDate = GregorianCalendar(year, 11, 12)

        // act
        dateInputViewController.mask = "00.00"
        maskEditText.setText("12.12")
        dateInputViewController.updateOnFocusChanged(false)
        val date = dateInputViewController.getDate()
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertNotNull(date)
        assertTrue(currentDate.timeInMillis == date!!.timeInMillis)
    }

    @Test
    fun `Get value as Calendar if mask is DATE_ONLY_YEAR_MASK`() {
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val currentDate = GregorianCalendar(2023, month, dayOfMonth)

        // act
        dateInputViewController.mask = "0000"
        maskEditText.setText("2023")
        dateInputViewController.updateOnFocusChanged(false)
        val date = dateInputViewController.getDate()
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertNotNull(date)
        assertTrue(currentDate.timeInMillis == date!!.timeInMillis)
    }

    @Test
    fun `Get value as Calendar if mask is TIME_MASK`() {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val currentDate = GregorianCalendar(year, month, dayOfMonth, 13, 45)

        // act
        dateInputViewController.mask = "00:00"
        maskEditText.setText("13:45")
        dateInputViewController.updateOnFocusChanged(false)
        val date = dateInputViewController.getDate()
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertNotNull(date)
        assertTrue(currentDate.timeInMillis == date!!.timeInMillis)
    }

    @Test
    fun `Get value as Calendar if value is empty`() {
        // act
        dateInputViewController.updateOnFocusChanged(false)
        val date = dateInputViewController.getDate()
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertNull(date)
    }

    @Test
    fun `Set correct mask`() {
        val prevMask = dateInputViewController.mask
        val newMask = "00.00.00"

        // act
        dateInputViewController.mask = newMask
        dateInputViewController.updateOnFocusChanged(false)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertFalse(prevMask == dateInputViewController.mask)
    }

    @Test
    fun `Set incorrect mask`() {
        val prevMask = dateInputViewController.mask
        val newMask = "00.00.0"

        // act
        dateInputViewController.mask = newMask
        dateInputViewController.updateOnFocusChanged(false)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertTrue(prevMask == dateInputViewController.mask)
    }
}