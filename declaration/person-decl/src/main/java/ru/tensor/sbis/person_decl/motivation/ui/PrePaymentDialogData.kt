package ru.tensor.sbis.person_decl.motivation.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Данные по авансовой выплате для диалога
 *
 * @param minValue Минимальное кол-во денег, которое можно запросить в виде авансовой выплаты
 * @param mayBeRequested Кол-во денег, которое можно запросить в виде авансовой выплаты
 * @param card Номер карты (**** и 4 цифры)
 * @param bank Название банка
 * @param isCard true карта / false счёт
 */
@Parcelize
data class PrePaymentDialogData(
    val minValue: Double? = 0.0,
    val mayBeRequested: Double? = 0.0,
    val card: String?,
    val bank: String?,
    val isCard: Boolean?
) : Parcelable {

    companion object {
        //** SelfDocumented */
        val EMPTY = PrePaymentDialogData(
            null,
            null,
            null,
            null,
            null
        )
    }
}