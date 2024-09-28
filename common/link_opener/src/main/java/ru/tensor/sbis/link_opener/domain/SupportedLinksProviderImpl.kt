package ru.tensor.sbis.link_opener.domain

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import ru.tensor.sbis.network_native.httpclient.HttpProtocol
import ru.tensor.sbis.toolbox_decl.linkopener.SupportedLinksProvider
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinksData
import timber.log.Timber

/**
 * Реализация поставщика доступных ссылок для открытия в приложении.
 * Использует `AndroidManifest.xml`, получая зарегистрированные intent-фильтры с валидной scheme. Формирует список
 * доступных url и поддерживаемых шаблонов ссылок.
 *
 * @author us.bessonov
 */
internal class SupportedLinksProviderImpl(private val context: Context) : SupportedLinksProvider {

    override suspend fun getAvailableLinks(): List<LinksData> = withContext(Dispatchers.IO) {
        val assetManager = context.createPackageContext(context.packageName, 0).assets
        val parser = try {
            assetManager.openXmlResourceParser(ANDROID_MANIFEST)
        } catch (e: Exception) {
            Timber.e("Cannot open $ANDROID_MANIFEST to retrieve supported links")
            return@withContext emptyList()
        }

        val results = mutableListOf<IntentFilter>()
        parser.apply {
            try {
                while (next() != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && name == TAG_INTENT_FILTER) {
                        val intentFilter = getIntentFilter()
                        if (intentFilter.scheme == HttpProtocol.HTTPS.protocolName
                            && intentFilter.action == Intent.ACTION_VIEW) {
                            results.add(intentFilter)
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.w(e, "Error when reading $ANDROID_MANIFEST")
            } finally {
                close()
            }
        }

        return@withContext results.mergeDuplicateHosts()
    }

    private fun XmlPullParser.getIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        while (!(next() == XmlPullParser.END_TAG && name == TAG_INTENT_FILTER)) {
            if (eventType == XmlPullParser.END_TAG) continue
            val attrs = getAttributes()
            when (name) {
                TAG_DATA -> {
                    attrs[ATTR_SCHEME]?.let {
                        intentFilter.scheme = it
                    }
                    attrs[ATTR_HOST]?.let {
                        intentFilter.hosts.add(it.getRawOrById())
                    }
                    PATH_ATTRIBUTES.forEach {
                        attrs[it]?.let { path ->
                            intentFilter.paths.add(path.getRawOrById())
                        }
                    }
                }

                TAG_ACTION -> {
                    intentFilter.action = attrs[ATTR_NAME]
                }
            }
        }
        return intentFilter
    }

    private fun String.getRawOrById(): String {
        if (startsWith(PREFIX_STRING_ID)) {
            val id = substringAfter(PREFIX_STRING_ID).toIntOrNull()
                ?: return this
            return context.resources.getString(id)
        }
        return this
    }

    private fun XmlPullParser.getAttributes() = (0 until attributeCount).associate {
        getAttributeName(it) to getAttributeValue(it)
    }

    private fun List<IntentFilter>.mergeDuplicateHosts(): List<IntentFilter> {
        val itemsByPaths = mutableMapOf<String, MutableList<IntentFilter>>()
        forEach {
            val key = it.paths.joinToString()
            itemsByPaths
                .getOrPut(key) { mutableListOf() }
                .add(it)
        }
        return itemsByPaths.map { (_, duplicates) ->
            with(duplicates) {
                if (size == 1) {
                    single()
                } else {
                    IntentFilter(
                        flatMap(IntentFilter::hosts).toMutableList(),
                        flatMap(IntentFilter::paths).distinct().toMutableList(),
                        firstOrNull()?.scheme,
                        firstOrNull()?.action
                    )
                }
            }.swapHostsAndPathsIfNeeded()
        }
    }

    private fun IntentFilter.swapHostsAndPathsIfNeeded() = apply {
        if (paths.isEmpty()) {
            paths.addAll(hosts)
            hosts.clear()
        }
    }

    private companion object {
        const val ANDROID_MANIFEST = "AndroidManifest.xml"
        const val PREFIX_STRING_ID = "@"

        const val ATTR_PATH_PREFIX = "pathPrefix"
        const val ATTR_PATH_PATTERN = "pathPattern"
        const val ATTR_SSP_PATTERN = "sspPattern"
        const val ATTR_SCHEME = "scheme"
        const val ATTR_NAME = "name"
        const val ATTR_HOST = "host"

        const val TAG_DATA = "data"
        const val TAG_ACTION = "action"
        const val TAG_INTENT_FILTER = "intent-filter"

        val PATH_ATTRIBUTES = listOf(ATTR_PATH_PREFIX, ATTR_PATH_PATTERN, ATTR_SSP_PATTERN)
    }
}

private data class IntentFilter(
    override val hosts: MutableList<String> = mutableListOf(),
    override val paths: MutableList<String> = mutableListOf(),
    var scheme: String? = null,
    var action: String? = null
) : LinksData


