package ru.tensor.sbis.design.view.input.number

import android.os.Build
import android.text.Editable
import android.text.Selection
import android.text.SpannableStringBuilder
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Класс для тестирования [NumberInputViewWatcher].
 *
 * @author ps.smirnyh
 */
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class NumberInputViewWatcherTest {

    private val numberInputViewWatcher = NumberInputViewWatcher()

    @Test
    fun `When first symbol is dot then 0 is inserted at beginning`() {
        val editable: Editable = SpannableStringBuilder(".")
        // act
        changeText(editable)
        // verify
        assertTrue(editable.startsWith("0."))
    }

    @Test
    fun `When last symbol is dot then 0 is inserted at the end`() {
        val editable: Editable = SpannableStringBuilder("0.")
        // act
        changeText(editable)
        // verify
        assertTrue(editable.endsWith(".0"))
    }

    @Test
    fun `When min is set and entered number less than min then number is rounded`() {
        val editable = SpannableStringBuilder("-125")
        numberInputViewWatcher.min = 0.0
        // act
        changeText(editable)
        // verify
        assertEquals("0", editable.toString())
    }

    @Test
    fun `When max is set and entered number greater than max then number is rounded`() {
        val editable = SpannableStringBuilder("125")
        numberInputViewWatcher.max = 100.0
        // act
        changeText(editable)
        // verify
        assertEquals("100", editable.toString())
    }

    @Test
    fun `When number fraction is set then fractional part is no more number fraction`() {
        val editable = SpannableStringBuilder("125.123")
        numberInputViewWatcher.numberFraction = 2u
        // act
        changeText(editable)
        // verify
        assertEquals("125.12", editable.toString())
    }

    @Test
    fun `When isShownZeroValue is true then 0 should remain on full delete`() {
        val editable = SpannableStringBuilder("")
        numberInputViewWatcher.isShownZeroValue = true
        // act
        changeText(editable)
        // verify
        assertEquals("0", editable.toString())
    }

    @Test
    fun `When number is entered after separator then entered number replace number on the right`() {
        val editable = SpannableStringBuilder("123.645")
        val oldText = "123.45"
        val startEdit = editable.indexOf('6')
        val endEdit = startEdit + 1
        val countEdit = endEdit - startEdit
        numberInputViewWatcher.numberFraction = 2u
        Selection.setSelection(editable, endEdit)
        // act
        numberInputViewWatcher.beforeTextChanged(
            oldText,
            oldText.indexOf('4'),
            countEdit,
            editable.length
        )
        numberInputViewWatcher.onTextChanged(
            editable.toString(),
            startEdit,
            oldText.length,
            countEdit
        )
        numberInputViewWatcher.afterTextChanged(editable)
        // verify
        assertEquals("123.65", editable.toString())
    }

    private fun changeText(editable: Editable) {
        val charSequence = editable.toString()
        numberInputViewWatcher.beforeTextChanged(
            charSequence,
            0,
            charSequence.length,
            charSequence.length
        )
        numberInputViewWatcher.onTextChanged(charSequence, 0, 0, charSequence.length)
        numberInputViewWatcher.afterTextChanged(editable)
    }
}