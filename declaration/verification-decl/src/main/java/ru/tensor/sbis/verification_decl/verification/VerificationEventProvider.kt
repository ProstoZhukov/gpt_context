package ru.tensor.sbis.verification_decl.verification

import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик событий верификации.
 *
 * @author ar.leschev
 */
interface VerificationEventProvider : Feature {

    /**
     * Подписка на событие верификации контакта.
     */
    fun observeContactVerifiedEvent(): Observable<ContactVerifiedEvent>
}