package ru.tensor.sbis.crud.sale.model

import java.util.*

/**
 * Модель с информацией о непереданных в ОФД документах связанных с кассой
 */
class DateTimeCheckStatusInside(var dateTimeSyncType: DateTimeSyncTypeInside,
                                var kkmDateTime: Date,
                                var kkmLastFiscalDocumentDateTime: Date?,
                                var dateTimeReference: Date,
                                @SuppressWarnings("unused") dateTimeSource: DateTimeSourceInside)