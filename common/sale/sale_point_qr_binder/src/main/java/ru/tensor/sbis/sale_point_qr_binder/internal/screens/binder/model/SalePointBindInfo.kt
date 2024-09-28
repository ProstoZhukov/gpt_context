package ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель информации для привязки QR-кода к точке продаж.
 *
 * @param salePointId идентификатор точки продаж.
 * @param bindUrl ссылка для выполнения обратного вызова.
 * @param objectIdentifier семантика этого параметра определяется на сервере и может меняться. Параметр приходит в пуш-уведомлении.
 * @param objectType семантика этого параметра определяется на сервере и может меняться. Параметр приходит в пуш-уведомлении.
 * @param site семантика этого параметра определяется на сервере и может меняться. Параметр приходит в пуш-уведомлении.
 *
 * @author kv.martyshenko
 */
@Parcelize
internal class SalePointBindInfo(
    val salePointId: String,
    val bindUrl: String,
    val objectIdentifier: String?,
    val objectType: String?,
    val site: String?,
    val hall: String?
) : Parcelable