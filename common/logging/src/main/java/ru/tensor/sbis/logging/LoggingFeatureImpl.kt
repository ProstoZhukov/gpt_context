package ru.tensor.sbis.logging

import ru.tensor.sbis.toolbox_decl.logging.LoggingFeature
import ru.tensor.sbis.toolbox_decl.logging.LoggingFragmentProvider

/**
 * Реализация [LoggingFeature].
 *
 * @author av.krymov
 */
class LoggingFeatureImpl : LoggingFeature {
    override fun getLoggingFragmentProvider(): LoggingFragmentProvider = LoggingFragmentProviderImpl()
}