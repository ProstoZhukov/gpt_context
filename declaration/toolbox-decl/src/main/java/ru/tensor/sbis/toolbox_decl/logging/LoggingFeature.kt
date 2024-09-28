package ru.tensor.sbis.toolbox_decl.logging

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для предоставления функционала модуля логгирования
 */
interface LoggingFeature : Feature {

    fun getLoggingFragmentProvider(): LoggingFragmentProvider
}