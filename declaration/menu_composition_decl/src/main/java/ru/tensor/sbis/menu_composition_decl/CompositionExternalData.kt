package ru.tensor.sbis.menu_composition_decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

/**
 * Внешние данные для экрана выбора комплектов и модификаторов.
 */
@Parcelize
data class CompositionExternalData(
    /** Цена. */
    val price: Double? = null,
    /** Строковые коды (Серийные номера, коды продукта, коды партии и всё что сканируется). */
    val codes: ArrayList<String> = arrayListOf(),
    /** Аббревиатура единицы измерения или упаковки. */
    val unit: String? = null,
    /** Количество. */
    val quantity: Double? = null
) : Parcelable