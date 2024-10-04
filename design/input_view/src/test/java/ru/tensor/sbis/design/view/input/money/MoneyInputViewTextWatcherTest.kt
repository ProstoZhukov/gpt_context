package ru.tensor.sbis.design.view.input.money

import android.content.Context
import android.os.Build
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.mock
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.decorators.MoneyDecorator
import ru.tensor.sbis.design.view.input.base.MaskEditText

/**
 * Класс для тестирования [MoneyInputViewTextWatcher].
 *
 * @author ps.smirnyh
 */
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class MoneyInputViewTextWatcherTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private lateinit var maskEditText: MaskEditText

    private lateinit var moneyInputViewTextWatcher: MoneyInputViewTextWatcher

    @Before
    fun setUp() {
        context.theme.applyStyle(ru.tensor.sbis.design.R.style.AppGlobalTheme, true)
        maskEditText = MaskEditText(context)
        moneyInputViewTextWatcher = MoneyInputViewTextWatcher(MoneyDecorator(context), maskEditText, mock())
        moneyInputViewTextWatcher.numberFraction = 2u
        maskEditText.addTextChangedListener(moneyInputViewTextWatcher)
    }

    @After
    fun after() {
        maskEditText.removeTextChangedListener(moneyInputViewTextWatcher)
    }

    @Test
    fun `When isShownZeroValue is false then input should be empty`() {
        // act
        maskEditText.setText("")
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertTrue(maskEditText.text.isNullOrEmpty())
    }

    @Test
    fun `When isShownZeroValue is true then 0 dot 00 should remain on full delete`() {
        moneyInputViewTextWatcher.isShownZeroValue = true
        // act
        maskEditText.setText("")
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertEquals("0.00", maskEditText.text.toString())
    }

    @Test
    fun `When last fractional digit of number is deleted and isShownZeroValue is true then last fractional digit of number should be replaced with zero`() {
        val text = "12.34"
        val indexDelete = text.indexOf('4')
        moneyInputViewTextWatcher.isShownZeroValue = true
        // act
        maskEditText.setText(text)
        maskEditText.text?.delete(indexDelete, indexDelete + 1)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertEquals("12.30", maskEditText.text.toString())
    }

    @Test
    fun `When not last fractional digit of number is deleted and isShownZeroValue is true then fractional part should be shifted to the place of the removed digit`() {
        val text = "12.34"
        val indexDelete = text.indexOf('3')
        moneyInputViewTextWatcher.isShownZeroValue = true
        // act
        maskEditText.setText(text)
        maskEditText.text?.delete(indexDelete, indexDelete + 1)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertEquals("12.40", maskEditText.text.toString())
    }

    @Test
    fun `When number is entered and isShownZeroValue is false then fraction part must be added`() {
        val text = "12"
        // act
        maskEditText.setText(text)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertEquals("12.00", maskEditText.text.toString())
    }

    @Test
    fun `When last fractional digit of number is deleted and isShownZeroValue is false then last fractional digit of number should be deleted`() {
        val text = "12.34"
        val indexDelete = text.indexOf('4')
        // act
        maskEditText.setText(text)
        maskEditText.text?.delete(indexDelete, indexDelete + 1)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertEquals("12.3", maskEditText.text.toString())
    }

    @Test
    fun `When separator is deleted then separator is restored and number to the left of it is deleted`() {
        val text = "12.34"
        val indexDelete = text.indexOf('.')
        // act
        maskEditText.setText(text)
        maskEditText.text?.delete(indexDelete, indexDelete + 1)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertEquals("1.34", maskEditText.text.toString())
    }

    @Test
    fun `When number greater than thousand is entered then number is divided into triads`() {
        val text = "12345"
        // act
        maskEditText.setText(text)
        shadowOf(Looper.getMainLooper()).idle()
        // verify
        assertEquals("12 345.00", maskEditText.text.toString())
    }
}