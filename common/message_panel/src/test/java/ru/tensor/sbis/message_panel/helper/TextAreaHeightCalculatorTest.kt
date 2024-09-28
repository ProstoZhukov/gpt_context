package ru.tensor.sbis.message_panel.helper

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility

private const val MAX_HEIGHT = 100

private const val VERTICAL_PADDING = 1
private const val ATTACHMENTS_HEIGHT = 2
private const val ATTACHMENTS_PARTIAL_HEIGHT = 4
private const val QUOTE_HEIGHT = 8
private const val RECIPIENTS_HEIGHT = 16

/**
 * @author vv.chekurda
 * @since 12/19/2019
 */
@RunWith(JUnitParamsRunner::class)
class TextAreaHeightCalculatorTest {

    private val heightCalculator = TextAreaHeightCalculator(
        VERTICAL_PADDING,
        ATTACHMENTS_HEIGHT,
        ATTACHMENTS_PARTIAL_HEIGHT,
        QUOTE_HEIGHT,
        RECIPIENTS_HEIGHT
    )

    @Test
    @Parameters(
        // цитата и получатели скрыты
        "GONE, false, false, ${MAX_HEIGHT - VERTICAL_PADDING}",
        "VISIBLE, false, false, ${MAX_HEIGHT - VERTICAL_PADDING - ATTACHMENTS_HEIGHT}",
        "PARTIALLY, false, false, ${MAX_HEIGHT - VERTICAL_PADDING - ATTACHMENTS_PARTIAL_HEIGHT}",
        // цитата видна, a получатели скрыты
        "GONE, true, false, ${MAX_HEIGHT - VERTICAL_PADDING - QUOTE_HEIGHT}",
        "VISIBLE, true, false, ${MAX_HEIGHT - VERTICAL_PADDING - QUOTE_HEIGHT - ATTACHMENTS_HEIGHT}",
        "PARTIALLY, true, false, ${MAX_HEIGHT - VERTICAL_PADDING - QUOTE_HEIGHT - ATTACHMENTS_PARTIAL_HEIGHT}",
        // цитата скрыта, а получатели видны
        "GONE, false, true, ${MAX_HEIGHT - VERTICAL_PADDING - RECIPIENTS_HEIGHT}",
        "VISIBLE, false, true, ${MAX_HEIGHT - VERTICAL_PADDING - RECIPIENTS_HEIGHT - ATTACHMENTS_HEIGHT}",
        "PARTIALLY, false, true, ${MAX_HEIGHT - VERTICAL_PADDING - RECIPIENTS_HEIGHT - ATTACHMENTS_PARTIAL_HEIGHT}",
        // цитата и получатели видны
        "GONE, true, true, ${MAX_HEIGHT - VERTICAL_PADDING - QUOTE_HEIGHT - RECIPIENTS_HEIGHT}",
        "VISIBLE, true, true, ${MAX_HEIGHT - VERTICAL_PADDING - QUOTE_HEIGHT - RECIPIENTS_HEIGHT - ATTACHMENTS_HEIGHT}",
        "PARTIALLY, true, true, ${MAX_HEIGHT - VERTICAL_PADDING - QUOTE_HEIGHT - RECIPIENTS_HEIGHT - ATTACHMENTS_PARTIAL_HEIGHT}"
    )
    fun `Verify edit text height dependency from components visibility`(
        attachmentsVisibility: String,
        isQuotePanelVisible: Boolean,
        areRecipientsVisible: Boolean,
        expectedHeight: Int
    ) {
        val visibility = AttachmentsViewVisibility.valueOf(attachmentsVisibility)

        assertEquals(
            expectedHeight,
            heightCalculator.apply(visibility, isQuotePanelVisible, areRecipientsVisible, MAX_HEIGHT)
        )
    }
}