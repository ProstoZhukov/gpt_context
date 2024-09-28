package ru.tensor.sbis.verification_decl.login

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.account.PersonalAccount

interface PersonalAccountsExcludePrivate : Feature {

    /**
     * Возвращает только тех у кого есть компания.
     */
    fun getPersonalAccountsExcludePrivate(): List<PersonalAccount>
}