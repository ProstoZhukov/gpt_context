package ru.tensor.sbis.verification_decl.permission.startup

import android.Manifest

/**
 * Модель разрешения для запроса при запуске приложения (главного экрана)
 *
 * @property names список системных разрешений [Manifest.permission]
 * @property oneTime true если необходимо запросить разрешение только 1 раз
 * @property strategy стратегия запроса разрешений
 * @property callback функция обратного вызова для получения результата запроса разрешений
 * @property rationaleAction действие, описывающие обоснование запроса разрешений
 *
 * @author am.boldinov
 */
class StartupPermission(
    val names: Array<String>,
    val oneTime: Boolean = true,
    val strategy: StartupPermissionStrategy = StartupPermissionStrategy.MAIN_SCREEN_RESUMED,
    val callback: ((name: String, result: PermissionResult) -> Unit)? = null,
    val rationaleAction: RationalePermissionAction? = null,
) {

    constructor(
        name: String,
        oneTime: Boolean = true,
        strategy: StartupPermissionStrategy = StartupPermissionStrategy.MAIN_SCREEN_RESUMED,
        callback: ((name: String, result: PermissionResult) -> Unit)? = null,
        rationaleAction: RationalePermissionAction? = null
    ) : this(arrayOf(name), oneTime, strategy, callback, rationaleAction)
}

/**
 * Результат запроса разрешений.
 */
enum class PermissionResult {
    GRANTED,
    DENIED
}

/**
 * Стратегия запроса разрешений.
 */
enum class StartupPermissionStrategy {
    MAIN_SCREEN_RESUMED
}