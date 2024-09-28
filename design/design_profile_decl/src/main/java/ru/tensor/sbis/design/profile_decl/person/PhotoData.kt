package ru.tensor.sbis.design.profile_decl.person

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.utils.errorSafe
import java.util.UUID

/**
 * Данные, отображаемые в компонентах фото сотрудника.
 *
 * @author us.bessonov
 */
sealed class PhotoData : Parcelable {
    abstract val uuid: UUID?
    abstract val photoUrl: String?
}

/**
 * Данные фото сотрудника.
 */
@Parcelize
data class PersonData(
    override val uuid: UUID? = null,
    override val photoUrl: String? = null,
    val initialsStubData: InitialsStubData? = null
) : PhotoData(), Parcelable

/**
 * Данные коллажа для подразделения.
 * Коллаж формируется из фото сотрудников в [persons], первым из которых должен быть руководитель подразделения
 *
 * @param isStub модель с этим флагом предназначена для отображения заглушки подразделения
 */
@Parcelize
class DepartmentData internal constructor(
    override val uuid: UUID?,
    val persons: List<PersonData>,
    var isStub: Boolean
) : PhotoData() {

    constructor(
        uuid: UUID? = null,
        persons: List<PersonData> = emptyList()
    ) : this(uuid, persons, uuid == null && persons.isEmpty())

    override val photoUrl: String?
        get() = if (isStub) null else errorSafe("Working groups don't have custom photo")

}

/**
 * Данные отображаемого фото компании
 */
@Parcelize
data class CompanyData(
    override val uuid: UUID?,
    override val photoUrl: String? = null
) : PhotoData()

/**
 * Данные изображения группы социальной сети
 */
@Parcelize
data class GroupData(
    override val uuid: UUID?,
    override val photoUrl: String? = null
) : PhotoData()

/**
 * Данные изображения с произвольной заглушкой.
 */
@Parcelize
data class ImageData(
    override val photoUrl: String?,
    @DrawableRes
    val placeholder: Int?,
    override val uuid: UUID? = null
) : PhotoData()