package ru.tensor.sbis.verification_decl.lockscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ru.tensor.sbis.entrypoint_guard.activity.ActivityAssistant
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.lockscreen.data.NextLaunchScreen
import java.util.UUID

/**
 * Публичное API модуля Экран Блокировки.
 *
 * @author ar.leschev
 */
interface LockScreenFeature : Feature {

    /** @SelfDocumented */
    interface Provider {
        /** @SelfDocumented */
        val lockScreenFeature: LockScreenFeature
    }

    /** Обёртка для работы с биометрией. */
    val biometryController: BiometryController

    /** Репозиторий работы с настройками блокировки и биометрии. */
    val settingsRepository: LockSettingsRepository

    /** Фиксатор активности пользователя. */
    val activityCollector: ActivityCollector

    /**
     * Фрагмент настроек пин-кода для однопользовательских приложений, [isSingleUserMode] true.
     */
    fun createLockSettingsFragment(): Fragment

    /**
     * Следующий экран при запуске приложения.
     */
    fun nextLaunchScreen(): NextLaunchScreen

    /** Если с пользовательской активности прошло более 30 сек, вызовет экран блокировки поверх контента. */
    fun runLockScreenIfTime(context: Context)

    /** Аналог [runLockScreenIfTime], вернёт хост фрагмент. */
    @Deprecated("Неактуально, удалить использование https://online.sbis.ru/opendoc.html?guid=f5009184-5d24-4583-8ac2-251cb2d60364&client=3.")
    suspend fun getLockFragmentIfTime(): Fragment?

    /** Получить хост фрагмент блокировки. */
    fun getLockFragment(): Fragment

    /** Получить фрагмент блокировки для конкретного пользователя с [uuid]. Актуально для Общей учетной записи. */
    fun getMainLockFragment(uuid: UUID): Fragment

    /** Является ли приложение однопользовательским. */
    val isSingleUserMode: Boolean

    /** Класс активности блокировки. */
    val lockActivityClass: Class<out Activity>

    /** Получить фрагмент задания пин-кода. */
    fun getCreatePinFragment(): DialogFragment

    /** Получить фрагмент восстановления/смены пин-кода. */
    fun getRestorePinFragment(): Fragment

    /** Проверить наличие внешних пользователей. */
    suspend fun isNeedShowExternalUsers(): Boolean

    /** Показать экран, если [isNeedShowExternalUsers] вернул true. */
    fun getExternalUsersFragment(): Fragment

    /** Получить приклодной обработчик для установки на уровне приложения при создании активности. */
    fun getOnReadyInterceptor(): ActivityAssistant.OnReadyInterceptor

    /**
     * Получить [Intent] активити блокировки.
     *  После завершения всех операций попытается запустить [sourceIntent].
     *  Если не передан, просто закроется.
     */
    fun createIntentLockActivity(context: Context, sourceIntent: Intent): Intent

    companion object {
        /** Ключ. Используется для понимания что экран авторизации по логину был открыт с экрана блокировки. */
        const val ARG_IS_FROM_LOCKSCREEN = "ARG_IS_FROM_LOCKSCREEN"

        /**
         * Ключ получения результата для Fragment Result API при установке пина.
         */
        const val LOCK_FEATURE_WRITE_PIN = "LOCK_FEATURE_WRITE_PIN"
        const val LOCK_FEATURE_WRITE_PIN_IS_SET = "LOCK_FEATURE_WRITE_PIN_IS_SET"

        /** На экране общей учетной записи выбрали пользователя и прошла авторизация, перейти в МП. */
        const val SHARED_USER_EVENT = "LOCK_FEATURE_SHARED_USER_EVENT"

        /**
         * Код ошибки когда у пользователя, который пытается авторизоваться, отсутствует пин-код
         */
        const val NO_PIN_CODE = 423

    }

}