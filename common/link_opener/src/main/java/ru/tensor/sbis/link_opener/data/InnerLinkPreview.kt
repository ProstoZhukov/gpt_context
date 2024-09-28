package ru.tensor.sbis.link_opener.data

import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype

/**
 * Внутренняя реализация ссылки на документ.
 *
 * @see [LinkPreview]
 *
 * @param url адрес ссылки
 * @param isAfterSync true если превью было синхронизировано с облака, иначе синхронизация не состоялась
 * @property isPredictable false если данные "вероятнее всего" являются актуальными и достаточными,
 * т.е. свидетельствуют что превью не была спрогнозирована локально
 */
internal class InnerLinkPreview constructor(
    private var url: String = "",
    override var date: String = "",
    override var fullUrl: String = "",
    override val docUuid: String = "",
    override var image: String = "",
    override var title: String = "",
    override var subtitle: String = "",
    override var details: String = "",
    override var secondDetails: String = "",
    override var urlType: Int = 0,
    override var docType: DocType = DocType.UNKNOWN,
    override var docSubtype: LinkDocSubtype = LinkDocSubtype.UNKNOWN,
    override var rawDocSubtype: String = "",
    override var isIntentSource: Boolean = false,
    override var isOuter: Boolean = false,
    override var parameters: HashMap<String, String> = hashMapOf(),
    override var isSabylink: Boolean = false,
    override var isWebViewVisitor: Boolean = false,
    var isAfterSync: Boolean = false,
) : LinkPreview {

    override var href: String
        get() = fullUrl.ifBlank { url }
        set(value) {
            fullUrl = value
            url = value
        }


    override val guid: String
        get() = docUuid

    val isPredictable: Boolean
        get() = !(isKnownDoc || title.isNotEmpty())

    private val isKnownDoc: Boolean
        get() = docType != DocType.UNKNOWN && docSubtype != LinkDocSubtype.UNKNOWN

    /** @SelfDocumented */
    fun setOriginFields(isIntentSource: Boolean, isOuter: Boolean) {
        this.isIntentSource = isIntentSource
        this.isOuter = isOuter
    }

    companion object {
        /** @SelfDocumented */
        val EMPTY = InnerLinkPreview()
    }
}

internal val LinkPreview.isKnownDocType: Boolean
    get() = docType != DocType.UNKNOWN && docType != DocType.UNKNOWN_ONLINE_DOC && docType != DocType.BRAND_UNKNOWN

/**
 * Проверяет, является ли превью ссылки на документ достаточно подробным для обработки дальнейшей навигации.
 */
@Suppress("SpellCheckingInspection")
internal fun LinkPreview.isRedirectable(): Boolean {
    if (href.isNotBlank() || docType != DocType.UNKNOWN) {
        return true
    }
    return if (this is InnerLinkPreview) {
        this != InnerLinkPreview.EMPTY
    } else {
        docUuid.isNotBlank()
    }
}

/** @SelfDocumented */
internal fun LinkPreview.map(): InnerLinkPreview =
    InnerLinkPreview(
        url = href,
        fullUrl = fullUrl,
        image = image,
        title = title,
        subtitle = subtitle,
        details = details,
        secondDetails = secondDetails,
        urlType = urlType,
        docType = docType,
        docSubtype = docSubtype,
        rawDocSubtype = rawDocSubtype,
        docUuid = docUuid,
        isOuter = isOuter,
        parameters = parameters,
        isSabylink = isSabylink
    )
