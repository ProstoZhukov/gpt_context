package ru.tensor.sbis.message_panel.view

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.custom.combined.CombinedParameters
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.message_panel.helper.NEW_DIALOG_MIN_ROWS_COUNT

private const val RECIPIENTS_HEIGHT = 30
private const val QUOTE_HEIGHT = 42
private const val MESSAGE_CONTAINER_VERTICAL_MARGIN = 5
private const val INPUT_TEXT_FIELD_MINIMUM_HEIGHT = 34
private const val EDIT_TEXT_MIN_HEIGHT = INPUT_TEXT_FIELD_MINIMUM_HEIGHT + MESSAGE_CONTAINER_VERTICAL_MARGINS_COUNT * MESSAGE_CONTAINER_VERTICAL_MARGIN
private const val ATTACHMENTS_ITEM_HEIGHT_PARTIAL = 28
private const val MESSAGE_ATTACHMENTS_TOP_MARGIN = 6
private const val MESSAGE_ATTACHMENTS_BOTTOM_MARGIN = 6
private const val LINE_HEIGHT = 19
private const val ATTACHMENTS_MIN_HEIGHT = ATTACHMENTS_ITEM_HEIGHT_PARTIAL + MESSAGE_ATTACHMENTS_TOP_MARGIN + MESSAGE_ATTACHMENTS_BOTTOM_MARGIN
private const val NEW_DIALOG_WITH_PARTIAL_ATTACHMENTS_MIN_HEIGHT =
            ATTACHMENTS_ITEM_HEIGHT_PARTIAL + MESSAGE_ATTACHMENTS_TOP_MARGIN + MESSAGE_ATTACHMENTS_BOTTOM_MARGIN +
            MESSAGE_CONTAINER_VERTICAL_MARGIN
private const val PARTIAL_ATTACHMENTS_BIG_HEIGHT = NEW_DIALOG_WITH_PARTIAL_ATTACHMENTS_MIN_HEIGHT + 1
private const val DEFAULT_IS_TABLET = true

/**
 * @author vv.chekurda
 * @since 12/17/2019
 */
@RunWith(JUnitParamsRunner::class)
class SpaceForViewsAvailabilityMapperTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private lateinit var availabilityMapper: SpaceAvailabilityFunction

    @Before
    fun setUp() {
        availabilityMapper = SpaceForViewsAvailabilityMapper(
            RECIPIENTS_HEIGHT,
            QUOTE_HEIGHT,
            EDIT_TEXT_MIN_HEIGHT,
            ATTACHMENTS_MIN_HEIGHT,
            PARTIAL_ATTACHMENTS_BIG_HEIGHT,
            LINE_HEIGHT,
            DEFAULT_IS_TABLET
        )
    }

    @Test
    @CombinedParameters("true,false", "true,false", "true,false")
    fun `When available height les than required height, then recipients and attachments should be hidden`(
        isQuotePanelVisible: Boolean,
        areRecipientsVisible: Boolean,
        newDialogModeEnabled: Boolean
    ) {
        val availableHeight = EDIT_TEXT_MIN_HEIGHT - 1

        with(
            availabilityMapper.apply(
                isQuotePanelVisible,
                areRecipientsVisible,
                availableHeight,
                newDialogModeEnabled
            )
        ) {
            assertEquals(false, hasSpaceForAttachments)
            assertEquals(false, hasSpaceForRecipients)
        }
    }

    @Test
    @CombinedParameters("true,false", "true,false", "true,false")
    fun `When available height more or equal to required height, then recipients and attachments should be visible`(
        isQuotePanelVisible: Boolean,
        areRecipientsVisible: Boolean,
        newDialogModeEnabled: Boolean
    ) {
        val availableHeight = EDIT_TEXT_MIN_HEIGHT + ATTACHMENTS_MIN_HEIGHT + RECIPIENTS_HEIGHT + QUOTE_HEIGHT

        with(
            availabilityMapper.apply(
                isQuotePanelVisible,
                areRecipientsVisible,
                availableHeight,
                newDialogModeEnabled
            )
        ) {
            assertEquals(true, hasSpaceForAttachments)
            assertEquals(true, hasSpaceForRecipients)
        }
    }

    @Test
    @Parameters("true", "false")
    fun `When message panel can be shown only without attachments, then attachments should be hidden`(
        newDialogModeEnabled: Boolean
    ) {
        // слегка не хватает места
        val availableHeight = EDIT_TEXT_MIN_HEIGHT + ATTACHMENTS_MIN_HEIGHT + RECIPIENTS_HEIGHT + QUOTE_HEIGHT - 1

        with(availabilityMapper.apply(true, true, availableHeight, newDialogModeEnabled)) {
            assertEquals(false, hasSpaceForAttachments)
            assertEquals(true, hasSpaceForRecipients)
        }
    }

    @Test
    fun `When required height is less or equal to min height of new dialog with partial attachments, then attachments should be hidden`() {
        availabilityMapper = SpaceForViewsAvailabilityMapper(
            RECIPIENTS_HEIGHT,
            QUOTE_HEIGHT,
            EDIT_TEXT_MIN_HEIGHT,
            ATTACHMENTS_MIN_HEIGHT,
            NEW_DIALOG_WITH_PARTIAL_ATTACHMENTS_MIN_HEIGHT,
            LINE_HEIGHT,
            false
        )

        with(availabilityMapper.apply(true, true, NEW_DIALOG_WITH_PARTIAL_ATTACHMENTS_MIN_HEIGHT, true)) {
            assertEquals(false, hasSpaceForAttachments)
            assertEquals(true, hasSpaceForRecipients)
        }
    }
}