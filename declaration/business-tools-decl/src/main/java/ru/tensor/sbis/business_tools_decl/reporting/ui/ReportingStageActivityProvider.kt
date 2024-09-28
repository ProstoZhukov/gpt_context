package ru.tensor.sbis.business_tools_decl.reporting.ui

import android.content.Intent
import ru.tensor.sbis.info_decl.notification.NotificationUUID
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Interface which provides ReportingStageActivity intent.
 *
 * @author ev.grigoreva
 */
interface ReportingStageActivityProvider : Feature {

    /**
     * Returns ReportingStageActivity intent.
     *
     * @param documentId        - document id
     * @param documentUuid      - document uuid
     * @param notificationUuid  - notification uuid
     */
    fun getReportingStageActivityIntent(documentId: String,
                                        documentUuid: String,
                                        notificationUuid: NotificationUUID): Intent

}