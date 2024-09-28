package ru.tensor.sbis.common.util

import android.content.Context
import ru.tensor.sbis.toolbox_decl.language.LanguageProvider

/**
 * Служит для выполнения операций над
 * контекстом при его аттаче к Activity.
 */
object ContextReplacer {

    /**
     * Исполоьзуется для модификации [Context]
     */
    @JvmStatic
    fun replace(context: Context?): Context? {
        return context
            ?.let { updateContextForLanguage(it) }
            ?.let { updateContextForScaling(it) }
    }

    /**
     * Модификация контекста для поддержки смены языка.
     * Подмена контекста производится на уровне Activity в методе attachBaseContext.
     */
    private fun updateContextForLanguage(context: Context): Context =
        LanguageProvider.get(context)?.updateContext(context) ?: context

    /**
     * Модификация контекста для поддержки смены масштаба интерфейса приложения.
     * Подмена контекста производится на уровне Activity в методе attachBaseContext
     * На уровне application требуется реализация интерфейса [ApplicationScaling].
     * (см. приложение Retail)
     */
    fun updateContextForScaling(context: Context): Context {
        if (context.applicationContext !is ApplicationScaling) return context

        val density = (context.applicationContext as ApplicationScaling).densityDpi

        return density?.let {
            val config = context.resources.configuration
            config.densityDpi = density
            context.createConfigurationContext(config)
        } ?: context
    }
}

/**
 *  Интерфейс "холдер" для переопределения значения плотности экрана в конфигурации контекста Activity.
 */
interface ApplicationScaling {
    var densityDpi: Int?
}