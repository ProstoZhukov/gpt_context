package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.rows

/**
 * Модель, описывающая место в ряду (концертный зал).
 * @author aa.gulevskiy
 */
internal data class RowPlace(
    val rowNumber: String,
    val placeNumber: String
)