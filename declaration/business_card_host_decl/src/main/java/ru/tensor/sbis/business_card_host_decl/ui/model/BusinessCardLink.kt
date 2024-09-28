package ru.tensor.sbis.business_card_host_decl.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Ссылка на визитку
 * @property title Название ссылки
 * @property url Сама ссылка
 */
@Parcelize
data class BusinessCardLink(
    val title: String?,
    val url: String
) : Parcelable {
    companion object {
        private val stub by lazy {
            BusinessCardLink(
                "",
                ""
            )
        }

        /**@SelfDocumented*/
        fun stub() = stub
    }
}
