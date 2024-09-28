package ru.tensor.sbis.video_monitoring_decl.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Информация о принадлежности камер.
 *
 * @param companies список id компаний (точек продаж) принадлежности запрашиваемых камер
 * @param rooms список идентификаторов помещений
 * @param favorite принадлежит к списку избранных камер
 * @param hierarchy иерархия принадлежности камер
 */
@Parcelize
class CameraAffiliation(
    val companies: List<String> = emptyList(),
    val rooms: List<Int> = emptyList(),
    val favorite: Boolean = false,
    val hierarchy: CameraHierarchyPath? = null
) : Parcelable {

    @IgnoredOnParcel
    val company: String? = companies.firstOrNull()

    @IgnoredOnParcel
    val room: Int? = rooms.firstOrNull()

    @IgnoredOnParcel
    val isEmpty = companies.isEmpty() && rooms.isEmpty()

    companion object {
        /** @SelfDocumented */
        fun ofCompany(companyId: String?): CameraAffiliation =
            CameraAffiliation(companies = listOfNotNull(companyId))

        /** @SelfDocumented */
        fun ofLocation(companyId: String?, roomId: Int?, hierarchy: CameraHierarchyPath): CameraAffiliation =
            CameraAffiliation(
                companies = listOfNotNull(companyId),
                rooms = listOfNotNull(roomId),
                hierarchy = hierarchy
            )

        /** @SelfDocumented */
        fun ofFavorites(affiliation: CameraAffiliation): CameraAffiliation =
            CameraAffiliation(
                companies = affiliation.companies,
                rooms = affiliation.rooms,
                favorite = true,
                hierarchy = affiliation.hierarchy
            )
    }
}