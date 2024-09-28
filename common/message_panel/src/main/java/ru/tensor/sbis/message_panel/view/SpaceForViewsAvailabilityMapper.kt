package ru.tensor.sbis.message_panel.view

import android.content.res.Resources
import io.reactivex.functions.Function4
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.message_panel.helper.DIALOG_MIN_ROWS_COUNT
import ru.tensor.sbis.message_panel.helper.NEW_DIALOG_MIN_ROWS_COUNT
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.common_attachments.R as RAttachments
import ru.tensor.sbis.design.message_panel.R as RMPDesign
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon

internal const val MESSAGE_CONTAINER_VERTICAL_MARGINS_COUNT = 2

internal typealias SpaceAvailabilityFunction = Function4<Boolean, Boolean, Int, Boolean, SpaceForViewsAvailability>

/**
 * Определяет доступность отображения элементов панели ввода в зависимости от наличия доступного места.
 * Согласно спецификации, при нехватке места сперва требуется скрывать вложения, а затем строку получателей
 */
internal class SpaceForViewsAvailabilityMapper(
    private val recipientsHeight: Int,
    private val quoteHeight: Int,
    private val editTextMinHeight: Int,
    private val attachmentsMinHeight: Int,
    private val partialAttachmentsMinHeight: Int,
    private val editTextLineHeight: Int,
    private val isTablet: Boolean
) : SpaceAvailabilityFunction {

    private var latestHasSpaceForRecipients = true

    constructor(resources: Resources, editTextLineHeight: Int) : this(
        resources.getDimensionPixelSize(RMPCommon.dimen.design_message_panel_recipients_panel_height),
        resources.getDimensionPixelSize(RMPCommon.dimen.design_message_panel_common_quote_view_height),
        resources.getDimensionPixelSize(RDesign.dimen.input_text_field_minimum_height) +
                MESSAGE_CONTAINER_VERTICAL_MARGINS_COUNT * resources.getDimensionPixelSize(R.dimen.message_container_vertical_margin),
        resources.getDimensionPixelSize(RAttachments.dimen.attachments_item_height_partial) +
                resources.getDimensionPixelSize(R.dimen.message_attachments_top_margin) +
                resources.getDimensionPixelSize(R.dimen.message_attachments_bottom_margin),
        resources.getDimensionPixelSize(RAttachments.dimen.attachments_item_height_partial) +
                resources.getDimensionPixelSize(R.dimen.message_attachments_top_margin) +
                resources.getDimensionPixelSize(R.dimen.message_attachments_bottom_margin) +
                resources.getDimensionPixelSize(R.dimen.message_container_vertical_margin),
        editTextLineHeight,
        resources.getBoolean(RDesign.bool.is_tablet)
    )

    override fun apply(
        isQuotePanelVisible: Boolean,
        areRecipientsVisible: Boolean,
        panelMaxHeight: Int,
        newDialogModeEnabled: Boolean
    ): SpaceForViewsAvailability {
        var desiredHeight = editTextMinHeight + attachmentsMinHeight
        if (areRecipientsVisible) desiredHeight += recipientsHeight
        if (isQuotePanelVisible) desiredHeight += quoteHeight

        val dialogWithPartialAttachmentsMinHeight =
            (if (newDialogModeEnabled)
                NEW_DIALOG_MIN_ROWS_COUNT
            else
                DIALOG_MIN_ROWS_COUNT) * editTextLineHeight + partialAttachmentsMinHeight

        val hideAttachmentsOnLandscapePhone =
            !isTablet && panelMaxHeight <= dialogWithPartialAttachmentsMinHeight

        return when {
            hideAttachmentsOnLandscapePhone -> SpaceForViewsAvailability(
                hasSpaceForAttachments = false,
                hasSpaceForRecipients = latestHasSpaceForRecipients
            )
            desiredHeight <= panelMaxHeight -> SpaceForViewsAvailability(
                hasSpaceForAttachments = true,
                hasSpaceForRecipients = true
            )
            desiredHeight - attachmentsMinHeight <= panelMaxHeight -> SpaceForViewsAvailability(
                hasSpaceForAttachments = false,
                hasSpaceForRecipients = latestHasSpaceForRecipients
            )
            else                                                   -> SpaceForViewsAvailability(
                hasSpaceForAttachments = false,
                hasSpaceForRecipients = false
            )
        }.also { latestHasSpaceForRecipients = it.hasSpaceForRecipients }
    }
}

internal data class SpaceForViewsAvailability(
    val hasSpaceForAttachments: Boolean,
    val hasSpaceForRecipients: Boolean
)

