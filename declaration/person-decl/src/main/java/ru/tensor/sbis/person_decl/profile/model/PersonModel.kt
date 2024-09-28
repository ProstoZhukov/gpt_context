package ru.tensor.sbis.person_decl.profile.model

import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Реализация модели персоны.
 * @see Person
 *
 * @author dv.baranov
 */
@Parcelize
data class PersonModel @JvmOverloads constructor(
    override val uuid: UUID,
    override val faceId: Long? = null,
    override val name: PersonName,
    override val photoUrl: String? = null,
    override val gender: Gender = Gender.UNKNOWN,
    override val initialsStubData: InitialsStubData? = null,
    override val hasAccess: Boolean = false
) : Person
