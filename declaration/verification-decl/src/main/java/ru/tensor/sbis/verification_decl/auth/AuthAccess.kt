package ru.tensor.sbis.verification_decl.auth

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.auth.AuthAccessResultType.SUCCESS

/**
 * Интерфейс отправителя ответа на запрос подтверждения входа.
 *
 * @author ev.grigoreva
 */
interface AuthAccessDispatcher : Feature {

    /**
     * Подтвердить вход.
     *
     * @param resourceId идентификатор для отправки ответа на запрос аутентификации
     * @return результат отправки ответа.
     */
    fun confirmLogin(resourceId: String): AuthAccessResult

    /**
     * Отклонить вход.
     *
     * @param resourceId идентификатор для отправки ответа на запрос аутентификации.
     * @return результат отправки ответа.
     */
    fun rejectLogin(resourceId: String): AuthAccessResult
}

/**
 * Тип результата.
 *
 * @author ev.grigoreva
 */
enum class AuthAccessResultType {
    /** @SelfDocumented */
    SUCCESS,

    /** @SelfDocumented */
    FAILURE
}

/**
 * Модель результата.
 *
 * @param resourceId идентификатор, по которому был отправлен ответ.
 * @param type тип результата.
 * @param error текст ошибки.
 *
 * @author ev.grigoreva
 */
data class AuthAccessResult @JvmOverloads constructor(
    val resourceId: String,
    val type: AuthAccessResultType = SUCCESS,
    val error: String? = null
)

