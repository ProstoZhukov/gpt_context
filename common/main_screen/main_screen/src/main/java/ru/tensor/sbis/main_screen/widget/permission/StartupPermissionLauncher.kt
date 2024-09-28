package ru.tensor.sbis.main_screen.widget.permission

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermission

/**
 * Компонент для запроса [StartupPermission] у пользователя.
 *
 * @property callbackFlow подписка на результат обработки запросов разрешений
 *
 * @author am.boldinov
 */
interface StartupPermissionLauncher {

    val callbackFlow: Flow<Map<String, Boolean>>

    /**
     * Запрашивает последовательно разрешения из списка [permissions].
     * Опционально добавляется [predicate] для фильтрации запрашиваемых разрешений.
     */
    fun launch(permissions: List<StartupPermission>, predicate: (StartupPermission) -> Boolean = { true })

    /**
     * Отменяет все ожидающие запросы разрешений.
     */
    fun cancel()

    companion object
}