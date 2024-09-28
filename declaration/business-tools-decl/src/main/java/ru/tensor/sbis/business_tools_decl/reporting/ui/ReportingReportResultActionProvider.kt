package ru.tensor.sbis.business_tools_decl.reporting.ui

import android.content.Intent
import ru.tensor.sbis.info_decl.notification.NotificationUUID
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс провайдера результатов отчетности. Для типов reject, accept, encrypt
 *
 * @author ae.noskov
 */
interface ReportingReportResultActionProvider : Feature {

    /**
     * Return Report Action intent.
     *
     * @param documentUUID     - document uuid
     * @param notificationUuid - notification uuid
     */
    fun getReportActionIntent(documentUUID: String, notificationUuid: NotificationUUID): Intent

}