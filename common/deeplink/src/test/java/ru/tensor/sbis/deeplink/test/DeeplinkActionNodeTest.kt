package ru.tensor.sbis.deeplink.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.mockito.kotlin.*
import org.junit.*
import org.junit.runner.*
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.deeplink.*

@RunWith(MockitoJUnitRunner::class)
class DeeplinkActionNodeTest {

    /**
     * Если в подклассе [Fragment] интерфейс [DeeplinkActionNode] не реализован
     */
    @Test(expected = IllegalArgumentException::class)
    fun `performNewDeeplinkAction is not for DeeplinkActionNode`() {
        DeeplinkActionNode.performNewDeeplinkAction(mock())
    }

    /**
     * Тестирует сценарий реализации интерфейса [DeeplinkActionNode] в подклассе [Fragment],
     * был передан null в качестве action и
     * DeeplinkActionNode.putNewDeeplinkActionToArgsIfNotNull не был вызван.
     */
    @Test
    fun `performNewDeeplinkAction is for DeeplinkActionNode and null action is not added`() {
        val supportedFragment = newDeeplinkActionNodeFragment()

        DeeplinkActionNode.performNewDeeplinkAction(supportedFragment)

        verify(
            supportedFragment,
            never()
        )
            .onNewDeeplinkAction(NoDeeplinkAction)
    }

    /**
     * Тестирует сценарий реализации интерфейса [DeeplinkActionNode] в подклассе [Fragment],
     * был передан null в качестве action и
     * DeeplinkActionNode.putNewDeeplinkActionToArgsIfNotNull был вызван.
     */
    @Test
    fun `performNewDeeplinkAction is for DeeplinkActionNode and null action is added`() {
        val supportedFragment = newDeeplinkActionNodeFragment(null)

        DeeplinkActionNode.performNewDeeplinkAction(supportedFragment)

        verify(
            supportedFragment,
            never()
        )
            .onNewDeeplinkAction(NoDeeplinkAction)
    }

    /**
     * Тестирует сценарий реализации интерфейса [DeeplinkActionNode] в подклассе [Fragment],
     * было передано значение в качестве action и
     * DeeplinkActionNode.putNewDeeplinkActionToArgsIfNotNull не был вызван.
     */
    @Test
    fun `performNewDeeplinkAction is for DeeplinkActionNode and non-null action is not added`() {
        val action = NoDeeplinkAction
        val supportedFragment = newDeeplinkActionNodeFragment()

        DeeplinkActionNode.performNewDeeplinkAction(supportedFragment)

        verify(
            supportedFragment,
            never()
        )
            .onNewDeeplinkAction(action)
    }

    /**
     * Тестирует сценарий реализации интерфейса [DeeplinkActionNode] в подклассе [Fragment],
     * было передано значение в качестве action и
     * DeeplinkActionNode.putNewDeeplinkActionToArgsIfNotNull был вызван.
     */
    @Test
    fun `performNewDeeplinkAction is for DeeplinkActionNode and non-null action is added`() {
        val action = NoDeeplinkAction
        val supportedFragment = newDeeplinkActionNodeFragment(action)

        DeeplinkActionNode.performNewDeeplinkAction(supportedFragment)

        verify(supportedFragment).onNewDeeplinkAction(action)
    }

    /**
     *  Симулирует вызов DeeplinkActionNode.putNewDeeplinkActionToArgsIfNotNull
     */
    private fun newDeeplinkActionNodeFragment(action: DeeplinkAction?): DeeplinkActionNodeFragment {
        val args = mock<Bundle> {
            doReturn(action).whenever(mock).getSerializable(any())
        }
        return mock {
            doReturn(args).whenever(mock).arguments
        }
    }

    private fun newDeeplinkActionNodeFragment(): DeeplinkActionNodeFragment = mock()

    private class DeeplinkActionNodeFragment : Fragment(), DeeplinkActionNode {

        override fun onNewDeeplinkAction(args: DeeplinkAction) = Unit
    }
}