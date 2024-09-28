package ru.tensor.sbis.verification_decl.login

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.account.PersonalAccount

interface CurrentPersonalAccount : Feature {

    /**
     * Возвращает модель текущего аккаунта пользователя из кеша
     * @return аккаунт
     */
    fun getCurrentPersonalAccount(): PersonalAccount
}