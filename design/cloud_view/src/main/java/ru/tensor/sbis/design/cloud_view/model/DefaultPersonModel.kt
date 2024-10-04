package ru.tensor.sbis.design.cloud_view.model

import ru.tensor.sbis.design.profile_decl.person.PersonData

/**
 * Реализация по умолчанию для [PersonModel]
 *
 * @author ma.kolpakov
 */
data class DefaultPersonModel(
    override val personData: PersonData,
    override var name: String
) : PersonModel