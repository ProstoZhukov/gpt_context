package ru.tensor.sbis.verification_decl.auth

import android.app.Activity

/**
 * Интерфейс, предназначенный для маркировки объектов,
 * требующих чтоб пользователь был авторизован.
 * Если вешается на [Activity], то обработка по умолчанию производится в модуле авторизации.
 *
 * @author ar.leschev
 */
interface AuthAware {

    /**
     * Способ проверки залогинен ли пользователь
     */
    val checkAuthStrategy: CheckAuthStrategy
        get() = CheckAuthStrategy.CheckWithForceJumpToLogin

    /**
     * Стратегии проверки залогинен ли пользователь
     */
    sealed class CheckAuthStrategy {

        /**
         * Пропускаем проверку залогинен ли пользователь.
         */
        object Skip : CheckAuthStrategy()

        /**
         * Проверяем залогинен ли пользователь и переходим на экран логина, если проверка не прошла.
         */
        object CheckWithForceJumpToLogin : CheckAuthStrategy()

        /**
         * Вызывает onAuthCheck с передачей параметров для проверки на стороне клиента.
         */
        class CheckCustom(val onAuthCheck: (onUiInitializing: Boolean, isAuthorized: Boolean) -> Unit) :
            CheckAuthStrategy()

    }

}