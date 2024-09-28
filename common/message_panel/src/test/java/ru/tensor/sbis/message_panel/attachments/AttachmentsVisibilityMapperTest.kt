package ru.tensor.sbis.message_panel.attachments

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.custom.combined.CombinedParameters
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.common.testing.mockStatic
import ru.tensor.sbis.common.testing.on
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.message_panel.helper.AttachmentsVisibilityMapper
import ru.tensor.sbis.message_panel.helper.NEW_DIALOG_MIN_ROWS_COUNT
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.common_attachments.R as RAttachments

private const val ATTACHMENTS_ITEM_HEIGHT_MESSAGE = 98
private const val ATTACHMENTS_ITEM_HEIGHT_PARTIAL = 28
private const val MESSAGE_ATTACHMENTS_TOP_MARGIN = 6
private const val MESSAGE_ATTACHMENTS_BOTTOM_MARGIN = 6
private const val MESSAGE_CONTAINER_VERTICAL_MARGIN = 5
private const val LINE_HEIGHT = 19
private const val NEW_DIALOG_WITH_ATTACHMENTS_MIN_HEIGHT = ATTACHMENTS_ITEM_HEIGHT_MESSAGE +
        MESSAGE_ATTACHMENTS_TOP_MARGIN +
        MESSAGE_ATTACHMENTS_BOTTOM_MARGIN +
        NEW_DIALOG_MIN_ROWS_COUNT * LINE_HEIGHT

private const val ATTACHMENTS_SMALL_HEIGHT = NEW_DIALOG_WITH_ATTACHMENTS_MIN_HEIGHT - 1

private const val NEW_DIALOG_WITH_PARTIAL_ATTACHMENTS_MIN_HEIGHT = ATTACHMENTS_ITEM_HEIGHT_PARTIAL +
        MESSAGE_ATTACHMENTS_TOP_MARGIN +
        MESSAGE_ATTACHMENTS_BOTTOM_MARGIN +
        NEW_DIALOG_MIN_ROWS_COUNT * LINE_HEIGHT +
        MESSAGE_CONTAINER_VERTICAL_MARGIN

private const val PARTIAL_ATTACHMENTS_BIG_HEIGHT = NEW_DIALOG_WITH_PARTIAL_ATTACHMENTS_MIN_HEIGHT + 1

/**
 * Тест маппера, определяющего состояние видимости панели вложений.
 *
 * @author vv.chekurda
 */
@RunWith(JUnitParamsRunner::class)
class AttachmentsVisibilityMapperTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var resourceProvider: ResourceProvider

    private lateinit var mapper: AttachmentsVisibilityMapper

    private val deviceConfigurationUtils = mockStatic<DeviceConfigurationUtils>()

    @Before
    fun setUp() {

        deviceConfigurationUtils.on<DeviceConfigurationUtils, Boolean> {
            DeviceConfigurationUtils.isTablet(any())
        } doReturn false

        whenever(resourceProvider.mContext).thenReturn(mock())

        whenever(resourceProvider.getDimensionPixelSize(RAttachments.dimen.attachments_item_height_message)).thenReturn(
            ATTACHMENTS_ITEM_HEIGHT_MESSAGE
        )

        whenever(resourceProvider.getDimensionPixelSize(R.dimen.message_attachments_top_margin)).thenReturn(
            MESSAGE_ATTACHMENTS_TOP_MARGIN
        )

        whenever(resourceProvider.getDimensionPixelSize(R.dimen.message_attachments_bottom_margin)).thenReturn(
            MESSAGE_ATTACHMENTS_BOTTOM_MARGIN
        )

        whenever(resourceProvider.getDimensionPixelSize(RDesign.dimen.size_body1_scaleOn)).thenReturn(
            LINE_HEIGHT
        )

        mapper = AttachmentsVisibilityMapper(resourceProvider)
    }

    @After
    fun after() {
        deviceConfigurationUtils.close()
    }

    @Test
    @CombinedParameters("true,false", "true,false")
    fun `Attachments panel is gone when there are no attachments`(
        isKeyboardShown: Boolean,
        hasSpaceForAttachments: Boolean
    ) {
        val visibility = mapper.apply(
            keyboard = isKeyboardShown,
            attachments = false,
            hasSpaceForAttachments = hasSpaceForAttachments,
            panelMaxHeight = NEW_DIALOG_WITH_ATTACHMENTS_MIN_HEIGHT,
            isLandscape = true
        )

        assertEquals(AttachmentsViewVisibility.GONE, visibility)
    }

    @Test
    @Parameters(value = ["true", "false"])
    fun `Attachments panel is gone when keyboard is shown and there is not enough space`(hasAttachments: Boolean) {
        val visibility = mapper.apply(
            keyboard = true,
            attachments = hasAttachments,
            hasSpaceForAttachments = false,
            panelMaxHeight = NEW_DIALOG_WITH_ATTACHMENTS_MIN_HEIGHT,
            isLandscape = true
        )

        assertEquals(AttachmentsViewVisibility.GONE, visibility)
    }

    @Test
    fun `Attachments panel is partially visible when keyboard is shown and there is enough space`() {
        val visibility = mapper.apply(
            keyboard = true,
            attachments = true,
            hasSpaceForAttachments = true,
            panelMaxHeight = NEW_DIALOG_WITH_ATTACHMENTS_MIN_HEIGHT,
            isLandscape = true
        )

        assertEquals(AttachmentsViewVisibility.PARTIALLY, visibility)
    }

    @Test
    @Parameters(value = ["true", "false"])
    fun `Attachments panel is visible when keyboard is not shown`(hasSpaceForAttachments: Boolean) {
        val visibility = mapper.apply(
            keyboard = false,
            attachments = true,
            hasSpaceForAttachments = hasSpaceForAttachments,
            panelMaxHeight = NEW_DIALOG_WITH_ATTACHMENTS_MIN_HEIGHT,
            isLandscape = true
        )

        assertEquals(AttachmentsViewVisibility.VISIBLE, visibility)
    }

    @Test
    fun `Attachments are visible partially when phone in portrait orientation, keyboard is shown and panel height is less then minimum panel height`() {
        mapper = AttachmentsVisibilityMapper(resourceProvider)

        val visibility = mapper.apply(
            keyboard = true,
            attachments = true,
            hasSpaceForAttachments = true,
            panelMaxHeight = ATTACHMENTS_SMALL_HEIGHT,
            isLandscape = false
        )

        assertEquals(AttachmentsViewVisibility.PARTIALLY, visibility)
    }

    @Test
    fun `Attachments are fully visible when phone in portrait orientation, keyboard is shown and panel height is equal or greater then minimum panel height`() {
        mapper = AttachmentsVisibilityMapper(resourceProvider)

        val visibility = mapper.apply(
            keyboard = true,
            attachments = true,
            hasSpaceForAttachments = true,
            panelMaxHeight = NEW_DIALOG_WITH_ATTACHMENTS_MIN_HEIGHT,
            isLandscape = false
        )

        assertEquals(AttachmentsViewVisibility.VISIBLE, visibility)
    }

    @Test
    @CombinedParameters(
        "true, false",
        "true, false",
        "true, false",
        "$NEW_DIALOG_WITH_ATTACHMENTS_MIN_HEIGHT, $ATTACHMENTS_SMALL_HEIGHT"
    )
    fun `Attachments are not visible partially when tablet in portrait orientation`(
        keyboard: Boolean,
        attachments: Boolean,
        hasSpaceForAttachments: Boolean,
        panelMaxHeight: Int
    ) {
        deviceConfigurationUtils.on<DeviceConfigurationUtils, Boolean> {
            DeviceConfigurationUtils.isTablet(any())
        } doReturn true

        mapper = AttachmentsVisibilityMapper(resourceProvider)

        val visibility = mapper.apply(
            keyboard = keyboard,
            attachments = attachments,
            hasSpaceForAttachments = hasSpaceForAttachments,
            panelMaxHeight = panelMaxHeight,
            isLandscape = false
        )

        assertNotEquals(AttachmentsViewVisibility.PARTIALLY, visibility)
    }

    @Test
    @CombinedParameters(
        "true, false",
        "$NEW_DIALOG_WITH_PARTIAL_ATTACHMENTS_MIN_HEIGHT, $PARTIAL_ATTACHMENTS_BIG_HEIGHT"
    )
    fun `Attachments are not hidden when tablet in landscape orientation and exists space for attachments`(
        keyboard: Boolean,
        panelMaxHeight: Int
    ) {
        deviceConfigurationUtils.on<DeviceConfigurationUtils, Boolean> {
            DeviceConfigurationUtils.isTablet(any())
        } doReturn true

        mapper = AttachmentsVisibilityMapper(resourceProvider)

        val visibility = mapper.apply(
            keyboard = keyboard,
            attachments = true,
            hasSpaceForAttachments = true,
            panelMaxHeight = panelMaxHeight,
            isLandscape = true
        )

        assertNotEquals(AttachmentsViewVisibility.GONE, visibility)
    }
}