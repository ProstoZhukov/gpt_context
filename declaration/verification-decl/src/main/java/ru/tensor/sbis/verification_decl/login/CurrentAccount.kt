package ru.tensor.sbis.verification_decl.login

import androidx.annotation.WorkerThread
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.account.UserAccount

interface CurrentAccount : Feature {

    /**
     * Возвращает модель текущего пользователя из кеша.
     *
     * @return пользователь
     */
    @WorkerThread
    fun getCurrentAccount(): UserAccount?
}