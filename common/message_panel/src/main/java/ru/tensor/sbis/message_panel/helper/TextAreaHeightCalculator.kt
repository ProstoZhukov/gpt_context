package ru.tensor.sbis.message_panel.helper

import android.content.res.Resources
import android.widget.EditText
import androidx.annotation.Px
import io.reactivex.functions.Function4
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.common_attachments.R as RAttachments
import ru.tensor.sbis.design.message_panel.R as RMPDesign
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon

typealias TextAreaHeightFunction = Function4<AttachmentsViewVisibility, Boolean, Boolean, Int, Int>

/**
 * Инструмент для вычисления [EditText.getMaxHeight] на основе информации о выстоте панели ввода и видимых компонентов
 *
 * @author vv.chekurda
 * @since 12/19/2019
 */
internal class TextAreaHeightCalculator(
    @Px
    private val padding: Int,
    @Px
    private val attachmentsHeight: Int,
    @Px
    private val attachmentsPartialHeight: Int,
    @Px
    private val quoteHeight: Int,
    @Px
    private val recipientsHeight: Int
) : TextAreaHeightFunction {

    constructor(resources: Resources): this(
        // padding
        resources.getDimensionPixelSize(R.dimen.message_container_vertical_margin) * 2,
        // attachments height
        resources.getDimensionPixelSize(RAttachments.dimen.attachments_item_height_message) +
                resources.getDimensionPixelSize(R.dimen.message_attachments_top_margin) +
                resources.getDimensionPixelSize(R.dimen.message_attachments_bottom_margin),
        // attachments partially height
        resources.getDimensionPixelSize(RAttachments.dimen.attachments_item_height_partial) +
                resources.getDimensionPixelSize(R.dimen.message_attachments_top_margin) +
                resources.getDimensionPixelSize(R.dimen.message_attachments_bottom_margin),
        // quote height
        resources.getDimensionPixelSize(RMPCommon.dimen.design_message_panel_common_quote_view_height),
        // recipients height
        resources.getDimensionPixelSize(RMPCommon.dimen.design_message_panel_recipients_panel_height)
    )

    @Px
    override fun apply(
        attachmentsVisibility: AttachmentsViewVisibility,
        quoteVisibility: Boolean,
        recipientsVisibility: Boolean,
        @Px maxHeigh: Int
    ): Int {
        var height = maxHeigh - padding
        height -= when (attachmentsVisibility) {
            AttachmentsViewVisibility.VISIBLE   -> attachmentsHeight
            AttachmentsViewVisibility.PARTIALLY -> attachmentsPartialHeight
            AttachmentsViewVisibility.GONE      -> 0
        }
        if (quoteVisibility) {
            height -= quoteHeight
        }
        if (recipientsVisibility) {
            height -= recipientsHeight
        }
        return height
    }
}