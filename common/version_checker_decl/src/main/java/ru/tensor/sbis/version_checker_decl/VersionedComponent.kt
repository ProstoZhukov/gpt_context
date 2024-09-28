package ru.tensor.sbis.version_checker_decl

import android.app.Activity
import androidx.fragment.app.Fragment

/**
 * Маркерный интерфейс, предназначенный для опциональной настройки версионирования над компонентом.
 * Для исключения компонента из версионирования достаточно реализовать интерфейс.
 * Вешается на [Activity] или [Fragment], а обработка производится диспетчером версионирования [VersioningDispatcher]
 *
 * @author as.chadov
 */
interface VersionedComponent {

    /**
     * Проверка необходимости версионирования
     */
    val versioningStrategy: Strategy get() = Strategy.SKIP

    /**
     * Стратегия версионирования компонента [VersionedComponent]
     */
    enum class Strategy {

        /** Пропустить проверку */
        SKIP,

        /** Проверяем только рекомендуемое обновление */
        CHECK_RECOMMENDED,

        /** Проверяем только критическое обновление */
        CHECK_CRITICAL
    }
}