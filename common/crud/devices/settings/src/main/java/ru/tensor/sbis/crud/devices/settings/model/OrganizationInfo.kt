package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Информация об организации.
 *
 * @param companyId - идентификатор компании.
 * @param companyName - название компании.
 * @param organizationId - идентификатор организации.
 * @param inn - ИНН компании.
 * @param isUse - используется ли платёжый терминал этой компанией?
 * */
@Parcelize
data class OrganizationInfo(
    val companyId: Long,
    val companyName: String,
    val organizationId: String,
    val qrId: String,
    val inn: String,
    val isUse: Boolean
) : Parcelable