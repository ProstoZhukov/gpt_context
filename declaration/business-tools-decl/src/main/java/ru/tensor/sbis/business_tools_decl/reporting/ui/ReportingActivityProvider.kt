package ru.tensor.sbis.business_tools_decl.reporting.ui

import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Interface which provides ReportingActivity Intent
 *
 * @author ev.grigoreva
 */
interface ReportingActivityProvider : Feature {

    /**
     * Return ReportingActivity Intent
     *
     * @param documentId - Document id
     * @param uuid - Document uuid
     * @param notificationType - NotificationType
     */
    fun getReportingActivityIntent(documentId: String,
                                   uuid: String,
                                   notificationType: Int): Intent
}