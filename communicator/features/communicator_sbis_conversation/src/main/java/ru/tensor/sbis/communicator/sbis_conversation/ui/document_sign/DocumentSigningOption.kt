package ru.tensor.sbis.communicator.sbis_conversation.ui.document_sign

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.message_panel.R as RMessagePanel

/**
 * Enum опций меню подписания вложений.
 *
 * @param textRes ресурс текста.
 * @param destructive нужно ли покрасить текст опции в красный.
 *
 * @author dv.baranov
 */
internal enum class DocumentSigningOption(
    @StringRes val textRes: Int,
    val destructive: Boolean = false,
) {
    /** Подписать. */
    SIGN(RMessagePanel.string.message_panel_document_signing_action_sign),

    /** Запросить подпись. */
    REQUEST(RMessagePanel.string.message_panel_document_signing_action_request),

    /** Подписать и запросить подпись. */
    SIGN_AND_REQUEST(RMessagePanel.string.message_panel_document_signing_action_sign_and_request),

    /** Отмена. */
    CANCEL(RDesign.string.design_cancel_item_label, true)
}

/** Получить список доступных опций для меню подписания вложений. */
internal fun getOptions(onlySignButton: Boolean) = mutableListOf(
    DocumentSigningOption.SIGN,
).apply {
    doIf(!onlySignButton) { add(DocumentSigningOption.REQUEST) }
    doIf(!onlySignButton) { add(DocumentSigningOption.SIGN_AND_REQUEST) }
    add(DocumentSigningOption.CANCEL)
}
