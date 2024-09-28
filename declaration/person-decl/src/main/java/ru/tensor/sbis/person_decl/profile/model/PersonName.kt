package ru.tensor.sbis.person_decl.profile.model

import android.os.Parcelable

/**
 * Представляет сведения о ФИО персоны
 *
 * @author us.bessonov
 */
interface PersonName : Parcelable {
    /** @SelfDocumented */
    val firstName: String

    /** @SelfDocumented */
    val lastName: String

    /** @SelfDocumented */
    val patronymicName: String

    /** @SelfDocumented */
    val fullName: String

    /** @SelfDocumented */
    val isEmpty: Boolean
}