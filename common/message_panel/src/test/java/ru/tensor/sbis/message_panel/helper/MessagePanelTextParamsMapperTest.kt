package ru.tensor.sbis.message_panel.helper

import android.view.Gravity
import androidx.core.content.res.ResourcesCompat.ID_NULL
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.message_panel.viewModel.livedata.hint.MessagePanelHintConfig.Companion.DEFAULT_HINT

private const val DEFAULT_MAX_HEIGHT = 100
private const val DEFAULT_MESSAGE = "Message"
private const val DEFAULT_MIN_LINES = 1

/**
 * Тест функции, формирующей параметры текста панели ввода
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MessagePanelTextParamsMapperTest {

    private val mapper = MessagePanelTextParamsMapper()

    @Test
    fun `When new dialog mode disabled and there is no message, then should set single line mode, enable ellipsize and show hint`() {
        val params = mapper.invoke(
            newDialogModeEnabled = false,
            maxHeight = DEFAULT_MAX_HEIGHT,
            messageText = RxContainer(null),
            hint = DEFAULT_HINT,
            minLines = DEFAULT_MIN_LINES
        )

        val expected = MessagePanelTextParams(
            minLines = MESSAGE_PANEL_NEW_DIALOG_MODE_MIN_LINES_DEFAULT,
            maxHeightParams = MaxLines(NO_MESSAGE_MAX_LINES),
            ellipsizeEnd = true,
            gravity = Gravity.CENTER_VERTICAL,
            hint = DEFAULT_HINT
        )
        assertEquals(expected, params)
    }

    @Test
    fun `When new dialog mode enabled and there is no message, then should set proper min lines count, apply max height, enable ellipsize, set gravity to top and show hint`() {
        val params = mapper.invoke(
            newDialogModeEnabled = true,
            maxHeight = DEFAULT_MAX_HEIGHT,
            messageText = RxContainer(null),
            hint = DEFAULT_HINT,
            minLines = DEFAULT_MIN_LINES
        )

        val expected = MessagePanelTextParams(
            minLines = MESSAGE_PANEL_NEW_DIALOG_MODE_MIN_LINES,
            maxHeightParams = MaxHeight(DEFAULT_MAX_HEIGHT),
            ellipsizeEnd = true,
            gravity = Gravity.TOP,
            hint = DEFAULT_HINT
        )
        assertEquals(expected, params)
    }

    @Test
    fun `When new dialog mode disabled and there is a message, then should use default min lines count, apply max height, disable ellipsize, set gravity to center and hide hint`() {
        val params = mapper.invoke(
            newDialogModeEnabled = false,
            maxHeight = DEFAULT_MAX_HEIGHT,
            messageText = RxContainer(DEFAULT_MESSAGE),
            hint = DEFAULT_HINT,
            minLines = DEFAULT_MIN_LINES
        )

        val expected = MessagePanelTextParams(
            minLines = MESSAGE_PANEL_NEW_DIALOG_MODE_MIN_LINES_DEFAULT,
            maxHeightParams = MaxHeight(DEFAULT_MAX_HEIGHT),
            ellipsizeEnd = false,
            gravity = Gravity.CENTER_VERTICAL,
            hint = ID_NULL
        )
        assertEquals(expected, params)
    }

    @Test
    fun `When new dialog mode enabled and there is a message, then should set proper min lines count, apply max height, disable ellipsize, set gravity to top and hide hint`() {
        val params = mapper.invoke(
            newDialogModeEnabled = true,
            maxHeight = DEFAULT_MAX_HEIGHT,
            messageText = RxContainer(DEFAULT_MESSAGE),
            hint = DEFAULT_HINT,
            minLines = DEFAULT_MIN_LINES
        )

        val expected = MessagePanelTextParams(
            minLines = MESSAGE_PANEL_NEW_DIALOG_MODE_MIN_LINES,
            maxHeightParams = MaxHeight(DEFAULT_MAX_HEIGHT),
            ellipsizeEnd = false,
            gravity = Gravity.TOP,
            hint = ID_NULL
        )
        assertEquals(expected, params)
    }
}