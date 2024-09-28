package ru.tensor.sbis.link_opener.domain.parser

import android.net.Uri
import android.util.Patterns
import ru.tensor.sbis.link_opener.data.IncomingLinkType
import ru.tensor.sbis.link_opener.data.UriContainer
import ru.tensor.sbis.link_opener.di.DOMAIN_KEYWORDS
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Named

/**
 * Детектор определения типа ссылки [IncomingLinkType] по Uri.
 *
 * @param keywords ключевые слова для проверки, что ссылка на домен СБИС.
 *
 * @author as.chadov
 */
internal class LinkTypeDetector @Inject constructor(
    @Named(DOMAIN_KEYWORDS) private val keywords: List<String>,
    private val mapper: LinkUriMapper
) {
    /**@SelfDocumented */
    fun getType(arg: UriContainer): IncomingLinkType = when {
        arg.intent != null -> getType(arg.intent.data)
        arg.uri.isNotBlank() -> getType(Uri.parse(arg.uri))
        else -> IncomingLinkType.INVALID
    }

    /**@SelfDocumented */
    private fun getType(uri: Uri?): IncomingLinkType {
        if (!isValid(uri)) {
            return IncomingLinkType.INVALID
        }
        if (mapper.isSabylink(uri)) {
            return IncomingLinkType.SABYLINK
        }
        val host = extractHost(uri!!) ?: return IncomingLinkType.INVALID
        if (keywords.any { host.contains(it) }) {
            return IncomingLinkType.SBIS
        }
        return IncomingLinkType.FOREIGN
    }

    /**
     * Валидация [Uri] на принадлежность веб-ресурсу, либо на соответствие "sabylink://".
     */
    private fun isValid(uri: Uri?): Boolean {
        if (uri == null) {
            return false
        }
        val webUrlToValidate = if (uri.isHierarchical && uri.queryParameterNames.contains(TEMPLATE_OPTIONS)) {
            val query = uri.getQueryParameter(TEMPLATE_OPTIONS)
            // извлеченный декодированный параметр отличается от того что в uri (значит url не кодирован под WEB и не пройдет валидацию Patterns.WEB_URL)
            if (uri.encodedQuery?.contains(query.orEmpty()) == true) {
                uri.updateUriParameter(TEMPLATE_OPTIONS, URLEncoder.encode(query, "utf-8"))
            } else {
                uri
            }
        } else {
            uri
        }
        return Patterns.WEB_URL.matcher("$webUrlToValidate").matches() || mapper.isValidSabylink("$uri")
    }

    private fun Uri.updateUriParameter(name: String, newQuery: String): Uri {
        val params = queryParameterNames
        val newUri = buildUpon().clearQuery()
        for (param in params) {
            if (param == name) {
                newUri.appendQueryParameter(param, newQuery)
            } else {
                newUri.appendQueryParameter(param, getQueryParameter(param))
            }
        }
        return newUri.build()
    }


    /**
     * Извлечь хост из [Uri].
     */
    private fun extractHost(uri: Uri): String? =
        if (uri.host.isNullOrBlank()) {
            val matcher = Patterns.DOMAIN_NAME.matcher("$uri")
            if (matcher.matches()) {
                matcher.group()
            } else null
        } else uri.host

    private companion object {
        // параметр может содержать например json, а некорые МП (например телега декодируют передаваемый Uri)
        val TEMPLATE_OPTIONS = "templateOptions"
    }
}