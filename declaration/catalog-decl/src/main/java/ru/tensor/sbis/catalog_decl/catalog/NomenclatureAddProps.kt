package ru.tensor.sbis.catalog_decl.catalog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *  Модель данных дополнительные поля номенклатуры.
 *
 *  @author sp.lomakin
 */
@Parcelize
data class NomenclatureAddProps(
    val calorificHeader: Calorific? = null,
    val calorificValues: List<Calorific>? = null,
    val executionTime: String? = null,
    val outputWeight: String? = null,
    val countryName: String? = null,
) : Parcelable {

    @Parcelize
    data class Calorific(
        val title: String?,
        val value: Double?,
        val code: String? = null,
    ) : Parcelable
}