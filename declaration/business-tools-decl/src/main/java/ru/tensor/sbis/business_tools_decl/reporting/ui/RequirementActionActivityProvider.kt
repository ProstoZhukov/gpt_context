package ru.tensor.sbis.business_tools_decl.reporting.ui

import android.content.Intent
import ru.tensor.sbis.info_decl.notification.NotificationUUID
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Interface which provides RequirementActionActivity intent.
 *
 * @author ev.grigoreva
 */
interface RequirementActionActivityProvider : Feature {

    /**
     * Return RequirementActionActivity intent.
     *
     * @param documentId        - document id
     * @param notificationUuid  - notification uuid
     * @param subDocumentId     - sub document id
     */
    fun getRequirementActionActivityIntent(documentId: String,
                                           notificationUuid: NotificationUUID,
                                           subDocumentId: Long): Intent

}