package ru.tensor.sbis.design.custom_view_tools.utils.view_extensions

import android.view.View
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout

/**
 * Тесты метода [safeRequestLayout].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CustomViewExtensionsKtSafeRequestLayoutTest {

    @Mock
    private lateinit var mockView: View

    @Test
    fun `When call safeRequestLayout(), then use only requestLayout() and invalidate()`() {
        mockView.safeRequestLayout()

        verify(mockView).requestLayout()
        verify(mockView).invalidate()
        verifyNoMoreInteractions(mockView)
    }
}