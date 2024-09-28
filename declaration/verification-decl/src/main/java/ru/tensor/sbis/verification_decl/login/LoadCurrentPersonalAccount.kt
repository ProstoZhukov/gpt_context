package ru.tensor.sbis.verification_decl.login

import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.account.PersonalAccount

interface LoadCurrentPersonalAccount : Feature {

    /**
     * Синхронизирует аккаунты пользователя с облаком и возвращает текущий
     *
     * @return onNext consumer, если текущий аккаунт существует в кеше, иначе onError consumer
     * Результат в рабочем потоке.
     */
    fun loadCurrentPersonalAccount(): Observable<PersonalAccount>
}