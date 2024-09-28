package ru.tensor.sbis.message_panel.helper

import org.mockito.kotlin.whenever
import io.reactivex.Completable
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Тест общих механик для работы с клавиатурой
 *
 * @author vv.chekurda
 * @since 1/15/2020
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class KeyboardKtTest : AbstractKeyboardTest() {

    @Mock
    private lateinit var action: Completable
    @Mock
    private lateinit var fallbackKeyboardHandler: Completable

    @Test
    fun `When view has focus, then action handler should be used`() {
        whenever(inputMethodManager.isActive(view)).thenReturn(true)

        assertEquals(action, view.choseKeyboardChangeStrategy(action, fallbackKeyboardHandler))
    }

    @Test
    fun `When view don't have focus, then fallback handler should be used`() {
        whenever(inputMethodManager.isActive(view)).thenReturn(false)

        assertEquals(
            fallbackKeyboardHandler,
            view.choseKeyboardChangeStrategy(action, fallbackKeyboardHandler)
        )
    }
}