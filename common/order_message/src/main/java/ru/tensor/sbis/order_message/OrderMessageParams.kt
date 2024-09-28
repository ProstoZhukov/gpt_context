package ru.tensor.sbis.order_message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Параметры инициализации
 *
 * @param nomenclatures номенклатура
 */
@Parcelize
data class OrderMessageParams(
    val nomenclatures: List<OrderMessageNomenclatures>,
    val resultKey: String = ORDER_MESSAGE_RESULT_KEY
) : Parcelable

/**@SelfDocumented */
const val ORDER_MESSAGE_RESULT_KEY = "ORDER_MESSAGE_RESULT_KEY"

/**@SelfDocumented */
const val ORDER_MESSAGE_FRAGMENT_TAG = "ORDER_MESSAGE_FRAGMENT_TAG"