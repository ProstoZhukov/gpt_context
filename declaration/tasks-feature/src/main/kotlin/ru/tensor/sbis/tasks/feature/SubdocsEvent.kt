package ru.tensor.sbis.tasks.feature

import java.util.UUID

/**
 * Событие от компонента поддокументов.
 *
 * @author aa.sviridov
 */
sealed class SubdocsEvent {

    /**
     * Событие клика по персоне от компонента поддокументов.
     * @property uuid идентификатор персоны.
     *
     * @author aa.sviridov
     */
    class PersonClicked(
        val uuid: UUID,
    ) : SubdocsEvent()

    /**
     * Событие клика по поддокументу от компонента поддокументов.
     * @property details детали поддокумента, см. [DocumentMainDetails].
     *
     * @author aa.sviridov
     */
    class SubdocClicked(
        val details: DocumentMainDetails,
    ) : SubdocsEvent()
}
