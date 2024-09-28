package ru.tensor.sbis.decorated_link.mapper

import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import timber.log.Timber
import ru.tensor.sbis.linkdecorator.generated.LinkPreview as ControllerLinkPreview

/**
 * Маппер для преобразования типа ссылки контроллера [ControllerLinkType] к
 * типу ссылки [DocType] используемому на UI
 */
internal fun ControllerLinkPreview.mapDocType(): DocType =
    when (val type = docType) {
        0 -> DocType.DOCUMENT
        1 -> DocType.LETTER // unused
        2, 3 -> DocType.DISC
        4 -> DocType.TRADES
        5 -> DocType.NEWS
        6 -> DocType.SOCNET_NEWS
        8 -> DocType.WI
        7 -> DocType.SOCNET_NEWS_REPOST // unused
        9 -> DocType.SOCNET_GROUP
        10 -> DocType.GROUP_DISCUSSION_TOPIC
        11 -> DocType.GROUP_DISCUSSION_QUESTION // unused
        15 -> DocType.WEBINAR
        16 -> DocType.MEETING
        17 -> DocType.MEETING_VIDEOCALL
        18 -> DocType.VIDEOCALL
        20 -> DocType.GROUP_SUGGESTIONS // unused
        21 -> DocType.EVENT
        22 -> DocType.PERSON
        23 -> DocType.WEBINAR_TRANSLATION
        24 -> DocType.COURSES
        25 -> DocType.INSTRUCTDOC
        26 -> DocType.CANDIDATE
        27 -> DocType.CONTRACTOR
        28 -> DocType.REG_INVITE
        29 -> DocType.OPEN_CHAT
        30 -> DocType.OPEN_DIALOG
        31 -> DocType.KNOWLEDGE_BASE
        32 -> DocType.BILLING_NEWS
        33 -> DocType.ARTICLE
        34 -> DocType.RETAIL_POINT_DEVICE_LINK
        35 -> DocType.QR_AUTH
        50 -> DocType.APPLICATION_FOR_UNQUALIFIED_SIGNATURE
        52 -> DocType.KNOWLEDGE_FOLDER
        60 -> DocType.RETAIL_ORDER_CARD
        61 -> DocType.RETAIL_ORDER_SALON
        62 -> DocType.QUEUES_SCHEDULE
        63 -> DocType.SETTING_CERTIFICATE_PASSWORD
        //region Saby get
        200 -> DocType.SABYGET_COMPANY
        201 -> DocType.SABYGET_REFEREE
        202 -> DocType.SABYGET_CERTIFICATE
        203 -> DocType.SABYGET_QR_TABLE
        204 -> DocType.SABYGET_QR_PRESTO
        205 -> DocType.SABYGET_QR_FAST_PAYMENTS_SYSTEM
        206 -> DocType.SABYGET_RECEIPT
        207 -> DocType.SABYGET_BILL
        208 -> DocType.SABYGET_CITY
        209 -> DocType.SABYGET_CATEGORY
        210 -> DocType.SABYGET_NOMENCLATURE
        211 -> DocType.SABYGET_NEWS
        212 -> DocType.SABYGET_PAGE_LINK
        213 -> DocType.BRAND_UNKNOWN
        214 -> DocType.SABYGET_GO_LINK
        215 -> DocType.SABYGET_MAIN
        //endregion
        2001 -> DocType.QR_AUTH_TO_WEB
        1000, 100000 -> DocType.UNKNOWN
        else -> {
            // нормальная ситуация - можем долго жить без поддержки типа, и мешать тестированию это не должно
            Timber.d("Unknown document type: $type (href $href)")
            DocType.UNKNOWN
        }
    }
