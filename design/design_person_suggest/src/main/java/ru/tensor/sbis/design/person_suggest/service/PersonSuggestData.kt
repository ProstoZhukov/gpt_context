package ru.tensor.sbis.design.person_suggest.service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.person_decl.profile.model.PersonName
import java.util.UUID

/**
 * Модель информации о персоне для компонентов саггеста.
 *
 * @property personData данные фото персоны.
 * @property name имя персоны.
 *
 * @author vv.chekurda
 */
@Parcelize
data class PersonSuggestData(
    val personData: PersonData,
    val name: PersonName
) : Parcelable {

    /**
     * Идентификатор персоны.
     */
    val uuid: UUID?
        get() = personData.uuid
}