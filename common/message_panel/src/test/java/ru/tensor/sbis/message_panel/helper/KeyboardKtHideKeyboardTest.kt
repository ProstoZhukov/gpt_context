package ru.tensor.sbis.message_panel.helper

import android.os.IBinder
import org.mockito.kotlin.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Тесты запросов скрытия клавиатуры
 *
 * @author vv.chekurda
 * @since 1/15/2020
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class KeyboardKtHideKeyboardTest : AbstractKeyboardTest() {

    @Test
    fun `When view have focus and keyboard hide requested, then soft keyboard should be hidden for window`() {
        val windowToken: IBinder = mock()
        whenever(view.windowToken).thenReturn(windowToken)
        whenever(inputMethodManager.isActive(view)).thenReturn(true)

        view.hideKeyboard().subscribe()

        verify(inputMethodManager).hideSoftInputFromWindow(windowToken, 0)
    }

    @Test
    fun `When view don't have focus and keyboard hide requested, then soft keyboard should not be hidden for window`() {
        whenever(inputMethodManager.isActive(view)).thenReturn(false)

        view.hideKeyboard().subscribe()

        verify(inputMethodManager, never()).hideSoftInputFromWindow(any(), any())
    }
}