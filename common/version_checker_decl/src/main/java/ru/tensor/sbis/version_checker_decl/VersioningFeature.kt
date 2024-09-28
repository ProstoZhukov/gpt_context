package ru.tensor.sbis.version_checker_decl

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс описывающий публичное api использования модуля "Версионирования МП"
 *
 * @author as.chadov
 *
 * @see [VersioningDispatcher]
 * @see [VersioningIntentProvider]
 * @see [VersioningDebugActivator]
 */
interface VersioningFeature :
    Feature,
    VersioningInitializer.Provider,
    VersioningDispatcher.Provider,
    VersioningIntentProvider,
    CriticalIncompatibilityProvider,
    IsActualVersionProvider,
    VersioningDebugActivator.Provider,
    VersioningDemoOpener.Provider,
    SbisApplicationManager.Provider
