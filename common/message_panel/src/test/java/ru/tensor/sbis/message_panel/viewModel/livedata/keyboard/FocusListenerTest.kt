package ru.tensor.sbis.message_panel.viewModel.livedata.keyboard

import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.message_panel.contract.FocusChangeListener

/**
 * @author vv.chekurda
 * @since 1/14/2020
 */
@RunWith(JUnitParamsRunner::class)
class FocusListenerTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var mediator: KeyboardEventMediator
    @Mock
    private lateinit var focusDelegate: FocusChangeListener

    @InjectMocks
    private lateinit var listener: FocusListener

    @Test
    @Parameters("true", "false")
    fun `When focus state received, then it should be delivered to focus listener`(focus: Boolean) {
        listener.onFocusChange(mock(), focus)

        verify(focusDelegate, only()).invoke(focus)
    }

    @Test
    fun `When focus received, then OpenedByFocus event should be published`() {
        listener.onFocusChange(mock(), true)

        verify(mediator, only()).postKeyboardEvent(OpenedByFocus)
    }

    @Test
    fun `When focus loosed, then ClosedByFocus event should be published`() {
        listener.onFocusChange(mock(), false)

        verify(mediator, only()).postKeyboardEvent(ClosedByFocus)
    }
}