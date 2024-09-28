package ru.tensor.sbis.pin_code.feature

import androidx.core.text.getSpans
import androidx.core.text.toSpanned
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.annotation.Config
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.view.input.password.PasswordInputView
import ru.tensor.sbis.pin_code.R
import ru.tensor.sbis.pin_code.decl.*
import ru.tensor.sbis.pin_code.findPinCodeView
import ru.tensor.sbis.pin_code.launchTestFragmentFragmentInContainer
import ru.tensor.sbis.pin_code.util.getText
import ru.tensor.sbis.pin_code.view.BubbleLimitedInputView
import ru.tensor.sbis.pin_code.view.CodeSpan

/**
 * Тест для [PinCodeFeature].
 *
 * @author as.stafeev
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class PinCodeFeatureTest {

    @get:Rule
    val rxRule = TrampolineSchedulerRule()

    private val testDescription = "Test description"

    @Test
    fun `All content is displayed correctly with the given configuration`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->
                val inputCode = "123456789123456789123456789123456789"
                val useCase = PinCodeUseCase.ConfirmSignature(testDescription)

                fragment.showPinCode(fragment, useCase)

                findPinCodeView(fragment).run {
                    val comment = findViewById<SbisTextView>(R.id.pin_code_comment).text
                    val code = findViewById<PasswordInputView>(R.id.pin_code_password_input_view).apply {
                        value = inputCode
                    }.getText()

                    assertThat(comment, CoreMatchers.equalTo(useCase.configuration.description))
                    assertThat(code, CoreMatchers.equalTo(inputCode.substring(0, useCase.configuration.codeLength)))
                }
            }
        }
    }

    @Test
    fun `All content is displayed correctly with the given another configuration`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->
                val useCase = PinCodeUseCase.SimpleConfirm(testDescription)
                val inputCode = "12"

                fragment.showPinCode(fragment, useCase)

                findPinCodeView(fragment).run {
                    val comment = findViewById<SbisTextView>(R.id.pin_code_comment).text
                    val codeSpans = findViewById<BubbleLimitedInputView>(R.id.pin_code_edit_text).apply {
                        setText(inputCode)
                    }.text!!.toSpanned().getSpans<CodeSpan>()

                    assertThat(comment, CoreMatchers.equalTo(useCase.configuration.description))
                    assertThat(codeSpans.size, CoreMatchers.`is`(useCase.configuration.codeLength))
                    assertThat(
                        codeSpans.map { it.isPrivate },
                        CoreMatchers.everyItem(CoreMatchers.`is`(false))
                    )
                }
            }
        }
    }

    @Test
    fun `When the user has entered all characters and request result is success, then consumer gets successful result`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->
                val result = "Test result"
                fragment.eventCatcher = mock {
                    on { onCodeEntered() }.thenReturn(result)
                }

                fragment.showPinCode(fragment, PinCodeUseCase.SimpleConfirm(testDescription))

                findPinCodeView(fragment).run {
                    val editText = findViewById<BubbleLimitedInputView>(R.id.pin_code_edit_text)
                    editText.setText("12345")
                    findViewWithTag<SbisRoundButton>("PIN_CODE_ACCEPT_BUTTON").performClick()

                    verify(fragment.eventCatcher).onSuccess(argThat { this == result })
                    verify(fragment.eventCatcher).onCodeEntered()
                    verifyNoMoreInteractions(fragment.eventCatcher)
                }
            }
        }
    }
}