package ru.tensor.sbis.version_checker_decl

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import ru.tensor.sbis.entrypoint_guard.activity.ActivityAssistant
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Диспетчер версионирования МП.
 * Посредник связывающий состояние UI с процессом проверки версий и информирования пользователя.
 * Для запуска версионирования необходимо вызвать метод [start]
 *
 * @author as.chadov
 */
interface VersioningDispatcher : Feature {
    /**
     * @see ActivityAssistant.OnReadyInterceptor
     */
    val onReadyInterceptor: ActivityAssistant.OnReadyInterceptor

    /**
     * Проверить [intent] и произвести редирект в другое МП при необходимости. true - если был редирект.
     * Если не передан [intent], берется из [activity].
     */
    fun isLaunchedForInstallation(activity: AppCompatActivity, intent: Intent? = null): Boolean

    /**
     * Запустить диспетчер для проверки версий.
     *
     * @param application текущее приложение
     */
    fun start(application: Application)

    /**
     * Указать стратегию проведения версионирования относительно состояния UI.
     * По умолчанию [Strategy.REGULAR]
     */
    fun behaviour(behaviour: Strategy): VersioningDispatcher

    /**
     * Стратегия версионирования UI для [VersioningDispatcher]
     */
    enum class Strategy {
        /**
         * Реакция на события версиоинрования в привязке к ЖЦ активити
         * - проверка версионирования в пределах [Lifecycle.State.RESUMED]
         * - событие версионирования будет обработано из активности
         * - можно исключить активити из версионирования через маркировку [VersionedComponent]
         */
        REGULAR,

        /**
         * Реакция на события версиоинрования в привязке к ЖЦ активити и фрагмента
         * - проверка версионирования в пределах [Lifecycle.State.RESUMED]
         * - событие критического версионирования будет обработано из активити
         * - событие рекомендуемого версионирования будет обработано из фрагмента
         * - по умолчанию все [DialogFragment] исключены из версионирования
         * - можно исключить активити/фрагменты из версионирования через маркировку [VersionedComponent]
         */
        BY_FRAGMENTS
    }

    /**
     * Поставщик реализации [VersioningDispatcher]
     */
    interface Provider : Feature {
        val versioningDispatcher: VersioningDispatcher
    }
}