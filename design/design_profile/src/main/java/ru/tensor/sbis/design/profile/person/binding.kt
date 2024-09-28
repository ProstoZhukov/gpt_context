@file:Suppress("KDocUnresolvedReference", "unused")

/**
 * @author us.bessonov
 */

package ru.tensor.sbis.design.profile.person

import android.view.View
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.createData
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus
import java.util.UUID
import ru.tensor.sbis.person_decl.profile.model.InitialsStubData as InitialsStubDataDecl

/**
 * Установка данных для показа фотографии.
 */
@BindingAdapter(value = ["photoUrl", "uuid", "initialsStubData", "personActivityStatus"], requireAll = false)
fun PersonView.setPersonData(
    photoUrl: String?,
    uuid: UUID?,
    initialsStubData: InitialsStubDataDecl?,
    personActivityStatus: ActivityStatus?
) {
    setData(
        PersonData(
            uuid = uuid,
            photoUrl = photoUrl,
            initialsStubData = initialsStubData?.createData()
        )
    )
    personActivityStatus?.let { setHasActivityStatus(true) }
}

/**
 * Установка данных для показа фотографии.
 */
@BindingAdapter(value = ["photoUrl", "uuid", "fullName", "personActivityStatus"], requireAll = false)
fun PersonView.setPersonData(
    photoUrl: String?,
    uuid: UUID?,
    fullName: String?,
    personActivityStatus: ActivityStatus?
) = setPersonData(photoUrl, uuid, fullName?.run { InitialsStubData.createByFullName(this) }, personActivityStatus)

/**
 * Показ фото если данные не пустые.
 */
@BindingAdapter("dataOrGone")
fun PersonView.setDataOrGone(photoData: PhotoData?) {
    @Suppress("LiftReturnOrAssignment")
    if (photoData != null) {
        setData(photoData)
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}