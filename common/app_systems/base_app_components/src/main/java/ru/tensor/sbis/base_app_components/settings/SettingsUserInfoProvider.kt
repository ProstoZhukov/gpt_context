package ru.tensor.sbis.base_app_components.settings

import androidx.annotation.WorkerThread
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Предоставляет данные зависящие от пользователя для отрисовки экране настроек.
 * Смысл в том, чтобы проще поддерживать изменения логики между множеством приложений, реализующих [SettingItemsProvider].
 *
 * @author ar.leschev
 */
class SettingsUserInfoProvider {

    /** @SelfDocumented */
    @WorkerThread
    fun provide(loginInterface: LoginInterface): UserDependentData {
        val currentAccount = loginInterface.getCurrentPersonalAccount()
        val moreThenOneAccount = if (currentAccount.isPhysical) {
            loginInterface.getPersonalAccounts()
        } else {
            loginInterface.getPersonalAccountsExcludePrivate()
        }.size > 1
        val companyName = if (moreThenOneAccount) currentAccount.company else currentAccount.workCompany
        val noCompanyNameTitle = moreThenOneAccount || companyName.isNullOrBlank() || currentAccount.isPhysicalOnly
        val screenTitle = if (noCompanyNameTitle) null else companyName

        return UserDependentData(moreThenOneAccount, screenTitle, companyName, currentAccount.photoUrl)
    }

}

/**
 * Данные зависящие от пользователя.
 *
 * @param moreThenOneAccount у пользователя более чем 1 аккаунт.
 * @param screenTitle заголовок экрана настроек. Обычно название компании.
 * @param companyName название компании. Может меняться для различного количества аккаунтов.
 * @param photoUrl url аватара пользователя.
 *
 * @author ar.leschev
 */
data class UserDependentData(
    val moreThenOneAccount: Boolean = false,
    val screenTitle: String? = null,
    val companyName: String? = null,
    val photoUrl: String? = null
)