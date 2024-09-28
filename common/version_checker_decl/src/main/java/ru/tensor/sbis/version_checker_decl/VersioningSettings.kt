package ru.tensor.sbis.version_checker_decl

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.RECOMMENDED_INTERVAL
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.SBIS_SERVICE_CRITICAL
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.SBIS_SERVICE_RECOMMENDED
import ru.tensor.sbis.version_checker_decl.data.UpdateSource

/**
 * Интерфейс поставщика информации о МП и настроек для его версионирования.
 *
 * @author as.chadov
 */
interface VersioningSettings : Feature {

    /**
     * Текущая версия МП. Обычно берется из `BuildConfig`.
     */
    val appVersion: String

    /**
     * Идентификатор МП. Обычно берется из `BuildConfig`.
     */
    val appId: String

    /**
     * Название МП, используется в тексте предложения обновиться.
     */
    val appName: String

    /**
     * Поддерживаемые источники обновления для МП.
     * По умолчанию [UpdateSource.SBIS_MARKET] и [UpdateSource.GOOGLE_PLAY_STORE].
     */
    fun getUpdateSource() = listOf(UpdateSource.SBIS_MARKET, UpdateSource.GOOGLE_PLAY_STORE)

    /**
     * Опциональная смена поведения версионирования посредством указания флагов используемых приложением вариантов.
     * По-умолчанию версионирование только через сбис сервис: [SBIS_SERVICE_RECOMMENDED], [SBIS_SERVICE_CRITICAL].
     */
    fun getAppUpdateBehavior(): Int = SBIS_SERVICE_RECOMMENDED or SBIS_SERVICE_CRITICAL

    /**
     * Интервал для предложения рекомендованного обновления МП в днях.
     */
    fun getRecommendedInterval(): Int = RECOMMENDED_INTERVAL

    /** @SelfDocumented */
    fun interface Provider : Feature {
        /** @SelfDocumented */
        fun getVersioningSettings(): VersioningSettings
    }
}
