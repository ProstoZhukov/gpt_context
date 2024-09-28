package ru.tensor.sbis.version_checker_decl

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для опциональной отложенной инициализации версионирования МП.
 * По умолчанию инициализация выполняется в плагине 'VersionCheckerPlugin', однако если требуется можно
 * отложить данный вызов и выполнить его вне плагина, предварительно активировав настройку:
 *
 * 'VersionCheckerPlugin.customizationOptions.deferInit = true'
 *
 * @author as.chadov
 */
interface VersioningInitializer : Feature {

    /**
     * Инициализация процесса версионирования
     */
    fun init()

    /**
     * Поставщик реализации [VersioningInitializer]
     */
    interface Provider : Feature {
        val versioningInitializer: VersioningInitializer
    }
}