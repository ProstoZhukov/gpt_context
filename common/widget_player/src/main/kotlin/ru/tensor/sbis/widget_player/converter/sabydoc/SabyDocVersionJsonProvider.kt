package ru.tensor.sbis.widget_player.converter.sabydoc

import ru.tensor.sbis.jsonconverter.generated.SabyDocParser
import ru.tensor.sbis.widget_player.SabyDocVersionProvider
import timber.log.Timber

/**
 * @author am.boldinov
 */
internal class SabyDocVersionJsonProvider : SabyDocVersionProvider {

    override fun load(json: String) = loadVersion {
        SabyDocParser.getDocVersion(json)
    }

    override fun loadFromFile(filePath: String) = loadVersion {
        SabyDocParser.getDocVersionFromFile(filePath)
    }

    private inline fun loadVersion(loader: () -> String): SabyDocVersion {
        val version = try {
            loader.invoke()
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
        return SabyDocVersion.fromString(version)
    }

}