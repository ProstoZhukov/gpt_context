package ru.tensor.sbis.widget_player

import androidx.annotation.WorkerThread
import ru.tensor.sbis.widget_player.converter.sabydoc.SabyDocVersion
import ru.tensor.sbis.widget_player.converter.sabydoc.SabyDocVersionJsonProvider

/**
 * @author am.boldinov
 */
interface SabyDocVersionProvider {

    @WorkerThread
    fun load(json: String): SabyDocVersion

    @WorkerThread
    fun loadFromFile(filePath: String): SabyDocVersion

    companion object {

        @JvmStatic
        fun create(): SabyDocVersionProvider {
            return SabyDocVersionJsonProvider()
        }
    }
}