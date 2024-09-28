package ru.tensor.sbis.verification_decl.lockscreen

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.verification_decl.lockscreen.data.NextLaunchScreen

/**
 * Контракт навигатора авторизации уровня приложения.
 * Работает с учётом наличия LockScreenFeature в МП.
 *
 * @author ar.leschev
 */
interface AuthLaunchNavigator {

    /**
     * Решает куда пойти приложению при запуске и вовзращает [NextLaunchScreen].
     */
    fun nextLaunchScreen(): NextLaunchScreen =
        TODO("Необходима реализация в LoginInterface вашего приложения.")

    /**
     * Получить интент блокировки. [sourceIntent] будет сохранен и открыт после разблокировки.
     */
    fun createLockScreenIntent(context: Context, sourceIntent: Intent): Intent =
        TODO("Необходима реализация в LoginInterface вашего приложения.")

    /** Запустить экран блокировки, если пора блокировать МП. */
    fun runLockScreenIfTime(context: Context) = Unit
}