package ru.tensor.sbis.design_selection.main

import android.os.Build
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design_selection.ui.main.router.SelectionRouter

/**
 * Тесты роутера компонента выбора [SelectionRouter].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SelectionRouterTest {

    private lateinit var router: SelectionRouter

    private lateinit var fragmentManager: FragmentManager
    private var containerId: Int = 124

    @Before
    fun setUp() {
        fragmentManager = mock()
        router = SelectionRouter(
            fragmentManager = fragmentManager,
            containerId = containerId
        )
    }

    @Test
    fun `When call open folder, then commit add transaction with backstack in container id`() {
        val fragmentTransaction = mock<FragmentTransaction> {
            on { add(any<Int>(), any()) } doReturn this.mock
            on { addToBackStack(anyOrNull()) } doReturn this.mock
            on { commit() } doReturn 123
        }
        whenever(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction)

        router.openFolder(mock())

        verify(fragmentManager).beginTransaction()
        verify(fragmentTransaction).add(eq(containerId), any())
        verify(fragmentTransaction).addToBackStack(anyOrNull())
        verify(fragmentTransaction).commit()

        verifyNoMoreInteractions(fragmentManager)
        verifyNoMoreInteractions(fragmentTransaction)
    }

    @Test
    fun `When call back and popBackStackImmediate return true, then call popBackStackImmediate and return true`() {
        whenever(fragmentManager.popBackStackImmediate()).thenReturn(true)

        val result = router.back()

        verify(fragmentManager).popBackStackImmediate()
        assertTrue(result)
    }

    @Test
    fun `When call back and popBackStackImmediate return false, then call popBackStackImmediate and return false`() {
        whenever(fragmentManager.popBackStackImmediate()).thenReturn(false)

        val result = router.back()

        verify(fragmentManager).popBackStackImmediate()
        assertFalse(result)
    }

    @Test
    fun `When call closeAllFolders, then call back while it closing any fragments`() {
        whenever(fragmentManager.popBackStackImmediate()).thenReturn(true, true, true, false)

        router.closeAllFolders()

        verify(fragmentManager, times(4)).popBackStackImmediate()
    }
}