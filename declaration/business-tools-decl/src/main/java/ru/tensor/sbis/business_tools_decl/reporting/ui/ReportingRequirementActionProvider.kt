package ru.tensor.sbis.business_tools_decl.reporting.ui

import android.content.Intent
import ru.tensor.sbis.info_decl.notification.NotificationUUID
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Interface which provides RequirementAction intent
 *
 * @author ev.grigoreva
 */
interface ReportingRequirementActionProvider : Feature {

    /**
     * Return Requirement Action intent.
     *
     * @param documentId        - document id
     * @param notificationUuid  - notification uuid
     * @param confirmed         - is requirement confirmed
     * @param contentType       - content type
     * @param subDocumentId     - sub document id
     * @param documentLink      - document link
     * @param companyName       - имя компании
     */
    fun getRequirementActionIntent(documentId: String,
                                   notificationUuid: NotificationUUID?,
                                   confirmed: Boolean,
                                   contentType: Int,
                                   subDocumentId: Long,
                                   documentLink: String?,
                                   documentUuid: String?,
                                   companyName: String?): Intent

}