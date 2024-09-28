package ru.tensor.sbis.link_opener.domain.parser

import android.content.IntentFilter
import android.net.Uri
import ru.tensor.sbis.link_opener.data.InnerLinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import java.net.URLDecoder
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Маппер представления ссылки на внутренний сбис документ в виде [Uri].
 *
 * Формирует [Uri] из превью по ссылке в следующем формате:
 * sabylink://type.subtype/details?uuid=uuid_text&rawSubtype=rawSubtype_text&title=title_text
 *
 * Uri используется для перенаправления вызова открытия ссылки на контент МП если первым запущенным по ссылке было
 * нецелевое МП семейства Сбис (обязательное требование начиная с android 12).
 *
 * @author as.chadov
 */
internal class LinkUriMapper @Inject constructor() {

    companion object {
        const val SCHEME = "sabylink"
        private const val PATTERN = "$SCHEME://[a-zA-Z]+.*/details\\?uuid=.*"
        private const val UUID_PARAM = "uuid"
        private const val TITLE_PARAM = "title"
        private const val HREF_PARAM = "href"
        private const val RAW_SUBTYPE_PARAM = "rawSubtype"
        private const val QUERY_PATH = "details"
    }

    /**
     * Представлен ли [Uri] внутренней sabylink ссылкой.
     */
    fun isSabylink(uri: Uri?): Boolean =
        "$uri".startsWith("$SCHEME://")

    /**
     * Проверка на валидность sabylink.
     * Ссылка, сформированная в другом приложении СБИС в [LinkUriMapper.marshal].
     */
    fun isValidSabylink(uri: String): Boolean {
        val sabylinkPattern = Pattern.compile(PATTERN)
        return sabylinkPattern.matcher(uri).matches()
    }

    /**
     * Преобразование ссылки на превью документа [LinkPreview] в [Uri].
     */
    fun marshal(preview: LinkPreview): Uri =
        Uri.Builder().apply {
            scheme(SCHEME)
            val firstLevel = preview.docType.name
            val secondLevel = preview.docSubtype.takeIf { it != LinkDocSubtype.UNKNOWN }?.name.orEmpty()
            val host = if (secondLevel.isNotBlank()) "$firstLevel.$secondLevel" else firstLevel
            authority(host.lowercase(Locale.getDefault()))
            appendPath(QUERY_PATH)
            appendQueryParameter(UUID_PARAM, preview.docUuid)

            /** У одинаковых [LinkDocSubtype] могут быть разные rawDocSubtype, поэтому так же передаем **/
            preview.rawDocSubtype.takeIf(String::isNotBlank)?.let { rawDocSubtype ->
                appendQueryParameter(RAW_SUBTYPE_PARAM, rawDocSubtype)
            }

            preview.title.takeIf(String::isNotBlank)?.let { title ->
                appendQueryParameter(TITLE_PARAM, title)
            }

            preview.href.takeIf(String::isNotBlank)?.let { href ->
                appendQueryParameter(HREF_PARAM, href)
            }
        }.build()

    /**
     * Распарсивание uri-ссылки на превью документа [Uri] в [InnerLinkPreview].
     */
    fun unmarshal(uri: Uri?): InnerLinkPreview {
        uri == null && return InnerLinkPreview()

        val host = "$uri".substringAfter("$SCHEME://").substringBefore("/$QUERY_PATH")
        val firstLevel = host.substringBefore(".")
        val secondLevel = host.substringAfter(".", "")
        val uuid = "$uri"
            .substringAfter("?$UUID_PARAM=", "")
            .substringBefore("&")
        val rawSubtype = "$uri"
            .substringAfter("&$RAW_SUBTYPE_PARAM=", "")
            .substringBefore("&")
        val title = "$uri"
            .substringAfter("&$TITLE_PARAM=", "")
            .substringBefore("&")
        val href = "$uri"
            .substringAfter("&$HREF_PARAM=", "")

        return InnerLinkPreview(
            docType = DocType.values().find { it.name.equals(firstLevel, true) } ?: DocType.UNKNOWN,
            docSubtype = LinkDocSubtype.values().find { it.name.equals(secondLevel, true) } ?: LinkDocSubtype.UNKNOWN,
            rawDocSubtype = rawSubtype.decode(),
            docUuid = uuid,
            title = title.decode(),
            isSabylink = true
        ).apply {
            this.href = href.decode()
        }
    }

    /**
     * Извлечение поддерживаемых интент-фильтром [filter] типов документов [DocType].
     */
    fun unmarshalDocTypes(filter: IntentFilter?): List<DocType> {
        if (filter == null) {
            return listOf(DocType.UNKNOWN)
        }
        val result = mutableSetOf<DocType>()
        for (i in 0 until filter.countDataAuthorities()) {
            val typeText = filter.getDataAuthority(i).host.substringBefore(".")
            val type = DocType.values().firstOrNull { typeText.equals(it.name, true) }
            type?.let(result::add)
        }
        return result.toList()
    }

    /**
     * Извлечение поддерживаемых интент-фильтром [filter] подтипов документов [LinkDocSubtype].
     */
    fun unmarshalDocSubtypes(filter: IntentFilter?): List<LinkDocSubtype> {
        if (filter == null) {
            return listOf(LinkDocSubtype.UNKNOWN)
        }
        val result = mutableSetOf<LinkDocSubtype>()
        for (i in 0 until filter.countDataAuthorities()) {
            val subtypeText = filter.getDataAuthority(i).host.substringAfter(".")
            if (subtypeText.isEmpty()) break
            val subtype = LinkDocSubtype.values().firstOrNull { subtypeText.equals(it.name, true) }
            subtype?.let(result::add)
        }
        return result.toList()
    }

    /**
     * Декодировать из URL в кириллицу, например из "%D0%9F%D1%80%D0%BE%D0%B5%D0%BA%D1%82" в "Проект".
     */
    private fun String.decode() = URLDecoder.decode(this, "UTF-8")
}