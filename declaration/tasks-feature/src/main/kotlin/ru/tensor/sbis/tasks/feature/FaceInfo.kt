package ru.tensor.sbis.tasks.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Модель лица для облегчённой модели задачи, см. [DocumentMainDetails].
 * @property uuid глобальный идентификатор лица, см. [UUID].
 *
 * @author aa.sviridov
 */
@Parcelize
data class FaceInfo(
    val uuid: UUID?,
    val type: Type,
    val fullName: String,
    val shortName: String,
    val surName: String,
    val name: String,
    val patronymic: String?,
    val photoId: String?,
    val decoration: Decoration?,
    val departmentUuid: UUID?,
    val departmentFaces: List<FaceInfo>,
) : Parcelable {

    /**
     * Тип лица.
     *
     * @author aa.sviridov
     */
    @Parcelize
    enum class Type : Parcelable {
        /** Частное лицо  */
        PRIVATE_FACE,
        /** Подразделение  */
        DEPARTMENT,
        /** Контрагент  */
        CONTRACTOR,
        /** Лицо из сделок  */
        DEAL_FACE
    }

    /**
     * Информация для декорации лица.
     * @property initials инициалы.
     * @property backgroundColorHex цвет фона.
     *
     * @author aa.sviridov
     */
    @Parcelize
    data class Decoration(
        val initials: String,
        val backgroundColorHex: String,
    ) : Parcelable
}