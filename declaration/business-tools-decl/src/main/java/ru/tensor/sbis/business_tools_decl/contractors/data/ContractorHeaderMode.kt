package ru.tensor.sbis.business_tools_decl.contractors.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Перечисление вариантов отображения шапки карточки контрагента.
 *
 * @av.efimov1
 */
@Parcelize
sealed interface ContractorHeaderMode : Parcelable {

    /**
     * Графическая шапка
     */
    @Parcelize
    object Banner : ContractorHeaderMode

    /**
     * Тулбар
     */
    @Parcelize
    data class Toolbar(val clientName: String) : ContractorHeaderMode
}