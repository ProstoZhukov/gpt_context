package ru.tensor.sbis.hallscheme.v2.business.model.tableconfig

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.hallscheme.v2.business.model.textconfig.HallSchemeTextConfig

/**
 * Настройки отображения информации на столе.
 *
 * @param nameTextConfig настройки отображения названия стола.
 * @param dishCountTextConfig настройки отображения количества блюд.
 * @param sumTextConfig настройки отображения суммы заказа.
 * @param latencyTextConfig настройки отображения времени приготовления блюд.
 */
@Parcelize
data class TableConfig(
    val nameTextConfig: HallSchemeTextConfig?,
    val dishCountTextConfig: HallSchemeTextConfig?,
    val sumTextConfig: HallSchemeTextConfig?,
    val latencyTextConfig: HallSchemeTextConfig?,
) : Parcelable {

    companion object {
        val defaultConfig = TableConfig(null, null, null, null)
    }
}