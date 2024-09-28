package ru.tensor.sbis.crud.sale.model

import java.util.*

/**
 * Модель с информацией о непереданных в ОФД документах связанных с кассой
 */
data class OFDUntransmittedDocumentsInfoInside(
    val fiscalDocumentsCountForTransmit: Long,
    val firstUntransmittedFiscalDocumentDateTime: Date
)