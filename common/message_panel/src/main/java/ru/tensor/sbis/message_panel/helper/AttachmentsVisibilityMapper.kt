package ru.tensor.sbis.message_panel.helper

import io.reactivex.functions.Function5
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility.*
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.common_attachments.R as RAttachments

internal const val NEW_DIALOG_MIN_ROWS_COUNT = 5
internal const val DIALOG_MIN_ROWS_COUNT = 1


/**
 * Функция конвертации состояния окружения в состояние видимости панели вложений
 *
 * @author vv.chekurda
 * @since 6/4/2019
 */
internal class AttachmentsVisibilityMapper(resourceProvider: ResourceProvider) :
    Function5<Boolean, Boolean, Boolean, Int, Boolean, AttachmentsViewVisibility> {

    private val isTablet = DeviceConfigurationUtils.isTablet(resourceProvider.mContext)

    private val attachmentsHeight =
        resourceProvider.getDimensionPixelSize(RAttachments.dimen.attachments_item_height_message) +
                resourceProvider.getDimensionPixelSize(R.dimen.message_attachments_top_margin) +
                resourceProvider.getDimensionPixelSize(R.dimen.message_attachments_bottom_margin)

    private val newDialogMinEditTextHeight = NEW_DIALOG_MIN_ROWS_COUNT * resourceProvider.getDimensionPixelSize(RDesign.dimen.size_body1_scaleOn)

    private val newDialogWithAttachmentsMinHeight = attachmentsHeight + newDialogMinEditTextHeight

    override fun apply(
        keyboard: Boolean,
        attachments: Boolean,
        hasSpaceForAttachments: Boolean,
        panelMaxHeight: Int,
        isLandscape: Boolean
    ): AttachmentsViewVisibility =
        when {
            !attachments                                       -> GONE
            mustAttachmentsVisiblePartiallyOnPortraitPhone(
                keyboard,
                isLandscape,
                panelMaxHeight
            )                                                  -> PARTIALLY
            isLandscape && keyboard && !hasSpaceForAttachments -> GONE
            isLandscape && keyboard                            -> PARTIALLY
            else                                               -> VISIBLE
        }

    /**
     * Если телефон в портретной ориентации с открытой клавиатурой и отображающейся панелью вложений, и
     * урезанный максимальный размер меньше высоты панели вложений плюс 5 строчек ввода,
     * то вложения делаем частично видмыми
     */
    private fun mustAttachmentsVisiblePartiallyOnPortraitPhone(
        keyboard: Boolean,
        isLandscape: Boolean,
        panelMaxHeight: Int
    ) = !isTablet && !isLandscape && keyboard && panelMaxHeight < newDialogWithAttachmentsMinHeight
}