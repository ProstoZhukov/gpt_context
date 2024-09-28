package ru.tensor.sbis.message_panel.helper

import android.view.inputmethod.InputMethodManager
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Тесты запросов подъёма клавиатуры
 *
 * @author vv.chekurda
 * @since 1/15/2020
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class KeyboardKtShowKeyboardTest : AbstractKeyboardTest() {

    @Test
    fun `When view has focus, then soft keyboard should be requested`() {
        whenever(inputMethodManager.isActive(view)).thenReturn(true)

        view.showKeyboard().subscribe()

        verify(inputMethodManager).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    @Test
    fun `When view don't have focus, then soft keyboard should be force toggled`() {
        whenever(inputMethodManager.isActive(view)).thenReturn(false)

        view.showKeyboard().subscribe()

        verify(inputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}