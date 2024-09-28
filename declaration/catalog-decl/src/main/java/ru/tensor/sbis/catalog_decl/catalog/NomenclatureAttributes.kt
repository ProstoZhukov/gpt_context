package ru.tensor.sbis.catalog_decl.catalog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *  Модель данных параметры номенклатуры сервиса каталог.
 *
 *  @author sp.lomakin
 */
@Parcelize
data class NomenclatureAttributes(
    val name: String?,
    val value: AttributesValue?
) : Parcelable {

    @Parcelize
    sealed class AttributesValue : Parcelable {
        class Text(val value: String) : AttributesValue()
        class Number(val value: String) : AttributesValue()
        class Logical(val value: Boolean) : AttributesValue()
        class RichText(val value: CharSequence) : AttributesValue()
        class ListText(val value: String) : AttributesValue()
        class Group(val value: List<NomenclatureAttributes>) : AttributesValue()
    }
}
