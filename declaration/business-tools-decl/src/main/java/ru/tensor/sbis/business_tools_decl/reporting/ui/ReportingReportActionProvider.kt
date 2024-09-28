package ru.tensor.sbis.business_tools_decl.reporting.ui

import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Interface which provides ReportAction intent
 *
 * @author ev.grigoreva
 */
interface ReportingReportActionProvider : Feature {

    /**
     * Return Report Action intent.
     *
     * @param documentId        - document id
     * @param contentType       - content type
     * @param subDocumentId     - sub document id
     * @param documentLink      - document link
     */
    fun getReportActionIntent(
        documentId: String,
        contentType: Int,
        subDocumentId: Long,
        documentLink: String?
    ): Intent

}