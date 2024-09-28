package ru.tensor.sbis.toolbox_decl.linkopener

import android.content.Intent
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import java.io.Serializable

/**
 * Интерфейс описывающий контракт ссылки на документ.
 *
 * @property date дата документа.
 * @property href адрес ссылки.
 * @property fullUrl адрес полной ссылки (может отсутствовать, значение [href] приоритетное).
 * @property image путь к иконке.
 * @property title заголовок.
 * @property subtitle подзаголовок.
 * @property details описание документа.
 * @property secondDetails дополнительное описание документа.
 * @property urlType тип ссылки [UrlType].
 * @property docType тип объекта по ссылке.
 * @property docSubtype подтип объекта по ссылке.
 * @property rawDocSubtype подтип объекта по ссылке в виде строки.
 * @property docUuid идентификатор документа.
 * @property guid статистический уникальный идентификатор.
 * @property isIntentSource true если источник ссылки интент [Intent].
 * @property isOuter true если источник ссылки был внешним, например интент инициировавший запуск МП.
 * @property isSabylink ссылка была получена при редиректе из нецелевого приложения (android 12+).
 * @property isWebViewVisitor true если назначение ссылки открытие WebView
 * @property parameters параметры ссылки.
 *
 * @author as.chadov
 */
interface LinkPreview : Serializable {
    var date: String
    val href: String
    val fullUrl: String
    var image: String
    var title: String
    var subtitle: String
    var details: String
    var secondDetails: String
    val docUuid: String
    val guid: String
    var urlType: Int
    var docType: DocType
    var docSubtype: LinkDocSubtype
    var rawDocSubtype: String
    var isIntentSource: Boolean
    val isOuter: Boolean
    var isSabylink: Boolean
    var isWebViewVisitor: Boolean
    var parameters: HashMap<String, String>
}

/**
 * Реализация [LinkPreview].
 * Используется для передачи внешне полученной ссылки в [OpenLinkController.processAndForget].
 * @see LinkPreview
 */
data class LinkPreviewImpl constructor(
    override var date: String = "",
    override var href: String = "",
    override var fullUrl: String = "",
    override var image: String = "",
    override var title: String = "",
    override var subtitle: String = "",
    override var details: String = "",
    override var secondDetails: String = "",
    override val docUuid: String = "",
    override val guid: String = docUuid,
    override var urlType: Int = 0,
    override var docType: DocType = DocType.UNKNOWN,
    override var docSubtype: LinkDocSubtype = LinkDocSubtype.UNKNOWN,
    override var rawDocSubtype: String = "",
    override var isIntentSource: Boolean = false,
    override var isOuter: Boolean = false,
    override var parameters: HashMap<String, String> = hashMapOf(),
    override var isSabylink: Boolean = false,
    override var isWebViewVisitor: Boolean = false
) : LinkPreview

