package ru.tensor.sbis.message_panel.view

import android.content.res.Resources
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import junitparams.JUnitParamsRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.design.R as RDesign

private const val PADDING = 0
private const val MARGIN = 0
private const val MESSAGE_CONTAINER_LEFT_MARGIN_WITHOUT_ATTACH_BUTTON = 12
private const val MESSAGE_PANEL_INPUT_FIELD_RIGHT_PADDING_WITH_RECORDER = 32
private const val INPUT_TEXT_FIELD_RIGHT_PADDING = 6
/**
 * Unit test на [MessagePanelBindingHelper]
 *
 * @author vv.chekurda
 * @since 01/23/2020
 */
@RunWith(JUnitParamsRunner::class)
class MessagePanelBindingHelperTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var resources: Resources

    @Mock
    private lateinit var attachButton: View

    @Mock
    private lateinit var messageContainer: View

    @Mock
    private lateinit var editText: EditText

    @Mock
    private lateinit var layoutParams: RelativeLayout.LayoutParams

    private lateinit var messagePanelBindingHelper: MessagePanelBindingHelper

    @Before
    fun setUp() {
        whenever(resources.getDimensionPixelSize(R.dimen.message_container_left_margin_without_attach_button)).thenReturn(MESSAGE_CONTAINER_LEFT_MARGIN_WITHOUT_ATTACH_BUTTON)
        whenever(resources.getDimensionPixelSize(R.dimen.message_panel_input_field_right_padding_with_recorder)).thenReturn(MESSAGE_PANEL_INPUT_FIELD_RIGHT_PADDING_WITH_RECORDER)
        whenever(resources.getDimensionPixelSize(RDesign.dimen.input_text_field_right_padding)).thenReturn(INPUT_TEXT_FIELD_RIGHT_PADDING)

        messagePanelBindingHelper = MessagePanelBindingHelper(resources)
    }

    @Test
    fun `When attach button is not visible then message container margin left will be set to MESSAGE_CONTAINER_LEFT_MARGIN_WITHOUT_ATTACH_BUTTON`() {
        whenever(attachButton.visibility).thenReturn(View.GONE)
        whenever(messageContainer.layoutParams).thenReturn(layoutParams)

        messagePanelBindingHelper.setMessageContainerMargins(attachButton, messageContainer)

        verify(layoutParams).setMargins(eq(MESSAGE_CONTAINER_LEFT_MARGIN_WITHOUT_ATTACH_BUTTON), eq(MARGIN), eq(MARGIN), eq(MARGIN))
    }

    @Test
    fun `When attach button is null then message container margin left will be set to MESSAGE_CONTAINER_LEFT_MARGIN_WITHOUT_ATTACH_BUTTON`() {
        whenever(messageContainer.layoutParams).thenReturn(layoutParams)

        messagePanelBindingHelper.setMessageContainerMargins(null, messageContainer)

        verify(layoutParams).setMargins(eq(MESSAGE_CONTAINER_LEFT_MARGIN_WITHOUT_ATTACH_BUTTON), eq(MARGIN), eq(MARGIN), eq(MARGIN))
    }

    @Test
    fun `When attach button is visible then message container margin left will be set to 0`() {
        whenever(messageContainer.layoutParams).thenReturn(layoutParams)

        messagePanelBindingHelper.setMessageContainerMargins(attachButton, messageContainer)

        verify(layoutParams).setMargins(eq(MARGIN), eq(MARGIN), eq(MARGIN), eq(MARGIN))
    }

    @Test
    fun `When message panel has recorder then edit text right padding will be set to MESSAGE_PANEL_INPUT_FIELD_RIGHT_PADDING_WITH_RECORDER`() {
        messagePanelBindingHelper.setEditTextPadding(editText, hasRecorder = true)

        verify(editText).setPadding(eq(PADDING), eq(PADDING), eq(MESSAGE_PANEL_INPUT_FIELD_RIGHT_PADDING_WITH_RECORDER), eq(PADDING))
    }

    @Test
    fun `When message panel has not recorder then edit text right padding will be set to INPUT_TEXT_FIELD_RIGHT_PADDING`() {
        messagePanelBindingHelper.setEditTextPadding(editText, hasRecorder = true)

        verify(editText).setPadding(eq(PADDING), eq(PADDING), eq(MESSAGE_PANEL_INPUT_FIELD_RIGHT_PADDING_WITH_RECORDER), eq(PADDING))
    }
}