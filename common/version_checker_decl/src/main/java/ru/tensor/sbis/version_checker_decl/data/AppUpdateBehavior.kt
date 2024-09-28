package ru.tensor.sbis.version_checker_decl.data

/**
 * Перечисление вариантов использования компонента версионирования.
 *
 * @author as.chadov
 */
@Suppress("KDocUnresolvedReference")
class AppUpdateBehavior {

    companion object {
        /**
         * Поддержка стандартного механизма рекомендуемых обновлений с проверкой версии через СБИС сервис версионирования apps [ApiService.VERSION_SERVICE].
         * Для версионирования используется единый файл описания конфигурации @see[common/version-checker/versions/android_versions.json].
         */
        const val SBIS_SERVICE_RECOMMENDED = 0x0001

        /**
         * Поддержка критических обновлений с использованием СБИС сервиса версионирования apps [ApiService.VERSION_SERVICE].
         * Для версионирования используется единый файл описания конфигурации @see[common/version-checker/versions/android_versions.json].
         */
        const val SBIS_SERVICE_CRITICAL = 0x0002

        /**
         * Поддержка стандартного механизма рекомендуемого обновления с проверкой версии через Google Play Core Library
         */
        const val PLAY_SERVICE_RECOMMENDED = 0x0004

        /**
         * Интервал по умолчанию для рекомендуемого предложения обновить приложение в днях
         */
        internal const val RECOMMENDED_INTERVAL = 7
    }
}