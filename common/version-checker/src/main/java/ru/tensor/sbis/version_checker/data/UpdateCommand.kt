package ru.tensor.sbis.version_checker.data

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.version_checker.domain.source.UpdateCommandFactory.Companion.UPDATE_SOURCE_KEY
import timber.log.Timber

/**
 * Команда обновления МП.
 *
 * @property intents намерения для запуска по убыванию приоритета
 */
internal class UpdateCommand(
    private val intents: List<Intent>
) {

    /** Выполнить действие в контексте [context] и вернуть для аналитики источник обновления,
     *  который удалось открыть. */
    fun run(context: Context): String? = with(context) {
        intents.forEach {
            if (attempt(it)) {
                return it.getStringExtra(UPDATE_SOURCE_KEY)
            }
        }
        Timber.w("No Activity found to handle intent.")
        return null
    }

    private fun Context.attempt(intent: Intent) =
        try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            Timber.d(e)
            false
        }
}