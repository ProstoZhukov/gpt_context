package ru.tensor.sbis.pin_code.view

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.annotation.Config
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.pin_code.R
import ru.tensor.sbis.pin_code.decl.PinCodeConfiguration
import ru.tensor.sbis.pin_code.findPinCodeView
import ru.tensor.sbis.pin_code.launchTestFragmentFragmentInContainer

/**
 * Тест для [BubbleLimitedInputView].
 *
 * @author as.stafeev
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class BubbleLimitedInputViewTest {

    @get:Rule
    val rxRule = TrampolineSchedulerRule()

    private val mockListener = mock<() -> Unit>()
    private val defaultCode = "123"

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    @Test
    fun `When user has entered all characters, then callback is called`() = testScope.runTest {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->
                fragment.showPinCode(fragment, PinCodeConfiguration())
                findPinCodeView(fragment).run {
                    val editText = findViewById<BubbleLimitedInputView>(R.id.pin_code_edit_text)
                    editText.scope = testScope
                    editText.dispatcher = testDispatcher

                    editText.maxLengthReachedListener = mockListener
                    editText.setText(defaultCode)
                    editText.append("4")
                    advanceUntilIdle()
                    verify(mockListener).invoke()
                }
            }
        }
    }
}