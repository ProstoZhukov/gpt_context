package ru.tensor.sbis.message_panel.helper

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import org.mockito.kotlin.whenever
import org.junit.Before
import org.mockito.Mock

/**
 * Подготовка тестов для инструментов работы с клавиатурой
 *
 * @author vv.chekurda
 * @since 1/15/2020
 */
open class AbstractKeyboardTest {
    @Mock
    protected lateinit var context: Context
    @Mock
    protected lateinit var inputMethodManager: InputMethodManager
    @Mock
    protected lateinit var view: View

    @Before
    fun setUp() {
        whenever(context.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(inputMethodManager)
        whenever(view.context).thenReturn(context)
    }
}