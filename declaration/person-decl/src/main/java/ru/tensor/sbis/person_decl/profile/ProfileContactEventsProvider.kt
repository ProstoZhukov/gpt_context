package ru.tensor.sbis.person_decl.profile

import io.reactivex.Observable
import ru.tensor.sbis.person_decl.profile.event.ContactChangedEvent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * @author Subbotenko Dmitry
 */
interface ProfileContactEventsProvider : Feature {

    /**
     * Подписка на события по изменению контактов
     */
    fun subscribeToContactsEvents(): Observable<ContactChangedEvent>
}