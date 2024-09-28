package ru.tensor.sbis.verification_decl.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель с информацией о персональном аккаунте пользователя.
 *
 * @param isCurrentProfile true, если профиль привязан к текущему авторизованному пользователю.
 * @param isPhysical true, если профиль физического лица.
 * @param company головная организация.
 * @param workCompany компания, куда трудоустроен сотрудник.
 * @param userId идентификатор пользователя из микросервисов авторизации и аутентификации.
 * @param name имя.
 * @param surname фамилия.
 * @param patronymic отчество.
 * @param photoUrl ссылка на фотографию. Может не грузиться без сессии под пользователем.
 * @param isPhysicalOnly true, если пользователь является "настоящим физическим лицом". С учётом биллинга и трудоустроенности.
 *
 * @author ar.leschev
 */
@Parcelize
data class PersonalAccount(
    var isCurrentProfile: Boolean = false,
    val isPhysical: Boolean = false,
    val company: String? = "",
    val workCompany: String? = "",
    val workPosition: String? = "",
    val userId: Int = 0,
    val name: String? = "",
    val surname: String? = "",
    val patronymic: String? = "",
    val photoUrl: String? = "",
    val isPhysicalOnly: Boolean = true,
    val isStubPhoto: Boolean = true
) : Parcelable {

    /**
     * Инициалы имени пользователя.
     */
    val initials: String
        get() = surname.orEmpty().take(1) + name.orEmpty().take(1)
}
