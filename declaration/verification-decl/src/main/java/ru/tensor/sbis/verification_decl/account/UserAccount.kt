package ru.tensor.sbis.verification_decl.account

import android.accounts.Account
import java.util.UUID

/**
 * Модель с информацией об аккаунте пользователя.
 * Возвращается запросом пользователя из LoginInterface.
 *
 * @author ar.leschev
 */
class UserAccount(
    name: String,
    type: String,
    var login: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var personId: String? = null,
    var userId: Int = 0,
    var clientId: Int = 0,
    var uuid: UUID? = null,
    var userName: String? = null,
    var userSurname: String? = null,
    var userPatronymic: String? = null,
    var userWorkPosition: String? = null,
    var isPhysic: Boolean = false,
    var isOnlyPhysic: Boolean = false,
    var isGuest: Boolean = false,
    var isDemo: Boolean = false
) : Account(
    name,
    type
) {
    override fun toString(): String {
        return "UserAccount { name=$name, " +
                "type=$type, " +
                "login=$login, " +
                "phone=$phone, " +
                "email=$email, " +
                "personId=$personId, " +
                "userId=$userId, " +
                "clientId=$clientId, " +
                "uuid=$uuid, " +
                "userName=$userName, " +
                "userSurname=$userSurname, " +
                "userPatronymic=$userPatronymic, " +
                "userWorkPosition=$userWorkPosition, " +
                "isPhysic=$isPhysic, " +
                "isOnlyPhysic=$isOnlyPhysic " +
                "isGuest=$isGuest " +
                "isDemo=$isDemo}"
    }

    /**
     * Получить идентификатор пользователя из сервиса профилей.
     *
     * Может отсутствовать у служебных пользователей.
     */
    fun getPersonUUID(): UUID? = personId?.let {
        runCatching { UUID.fromString(it) }.getOrNull()
    }
}