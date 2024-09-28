package ru.tensor.sbis.verification_decl.login

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.account.PersonalAccount

/**
 * Возвращает список всех персональных аккаунтов пользователя.
 *
 */
interface PersonalAccounts : Feature {

    /**
     * Получить писок аккаунтов.
     */
    fun getPersonalAccounts(): List<PersonalAccount?>
}