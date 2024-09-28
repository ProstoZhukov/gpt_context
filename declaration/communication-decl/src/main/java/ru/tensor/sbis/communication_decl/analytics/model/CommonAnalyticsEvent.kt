package ru.tensor.sbis.communication_decl.analytics.model

import ru.tensor.sbis.communication_decl.analytics.AnalyticsEvent

/**
 * Сканирование документа.
 */
class ScanningDocuments(override val functional: String) : AnalyticsEvent {

    override val analyticContext: String
        get() = FILE_ATTACHMENT_SCREEN
    override val event: String
        get() = COMMUNICATOR_ANALYTICS_SCANNING_DOCUMENTS
}

/**
 * Открытие шаринга.
 */
class OpenedSharedExtension(override val functional: String) : AnalyticsEvent {

    override val analyticContext: String
        get() = SHARE_SCREEN
    override val event: String
        get() = COMMUNICATOR_ANALYTICS_OPENED_SHARED_EXTENSION
}

/**
 * Переход из сервисных диалогов об обновлении МП в маркет.
 */
class OpenUpdateAppLink(override val functional: String) : AnalyticsEvent {
    override val analyticContext = MESSAGE_SCREEN
    override val event: String = COMMUNICATOR_ANALYTICS_OPEN_UPDATE_APP_LINK
}

private const val FILE_ATTACHMENT_SCREEN = "file_attachment_screen"
private const val SHARE_SCREEN = "share_screen"
private const val MESSAGE_SCREEN = "message_screen"
private const val COMMUNICATOR_ANALYTICS_SCANNING_DOCUMENTS = "scanning_documents"
private const val COMMUNICATOR_ANALYTICS_OPENED_SHARED_EXTENSION = "opened_shared_extension"
private const val COMMUNICATOR_ANALYTICS_OPEN_UPDATE_APP_LINK = "open_update_app_link"
