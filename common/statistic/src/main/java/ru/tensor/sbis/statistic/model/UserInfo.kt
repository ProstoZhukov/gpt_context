package ru.tensor.sbis.statistic.model

/**
 * Информация о пользователе.
 *
 * @property userId идентификатор пользователя.
 * @property clientId идентификатор аккаунта.
 */
class UserInfo(
    val userId: Long?,
    var clientId: Long?
)
