package ru.tensor.sbis.catalog_decl.warehouse.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Контракт модели нашей организации.
 *
 * @param originalId облачный идентификатор нашей организации.
 * @param name имя нашей организации.
 *
 * @author aa.mezencev
 */
@Parcelize
data class OurOrganisation(
    val originalId: Int,
    val name: String?
) : Parcelable