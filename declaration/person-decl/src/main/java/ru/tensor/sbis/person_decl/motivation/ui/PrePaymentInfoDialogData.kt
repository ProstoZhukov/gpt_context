package ru.tensor.sbis.person_decl.motivation.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Данные по авансовой выплате для диалога информации
 *
 * @param firstComment Комментарий №1
 * @param secondComment Комментарий №2
 */
@Parcelize
data class PrePaymentInfoDialogData(
    val firstComment: String?,
    val secondComment: String?
) : Parcelable {

    companion object {
        //** SelfDocumented */
        val EMPTY = PrePaymentInfoDialogData(
            null,
            null
        )
    }
}