package ru.tensor.sbis.order_message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** @SelfDocument */
@Parcelize
data class OrderMessageResult(val nomenclatures: List<OrderMessageNomenclatures>) : Parcelable