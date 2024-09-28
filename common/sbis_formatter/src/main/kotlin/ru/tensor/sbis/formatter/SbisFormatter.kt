package ru.tensor.sbis.formatter

import androidx.annotation.MainThread
import ru.tensor.sbis.formatter.currency.CurrencyFormatter
import ru.tensor.sbis.formatter.dateTime.DateTimeFormatter

/**
 * Класс-holder для инстансов форметтеров. Один на приложение.
 *
 * @property region регион пользователя.
 *
 * @author ps.smirnyh
 */
class SbisFormatter private constructor(private val region: String) {

    /** Инстанс форматтера даты под текущий регион. */
    val dateFormatter: DateTimeFormatter by lazy { DateTimeFormatter(region) }

    /** Инстанс форматтера валюты под текущий регион. */
    val currencyFormatter: CurrencyFormatter by lazy { CurrencyFormatter(region) }

    companion object {
        private const val DEFAULT_REGION = "RU"

        /** Инстанс класса с форматтерами под текущий регион. */
        @JvmStatic
        var current: SbisFormatter = SbisFormatter(DEFAULT_REGION)
            private set

        /**
         * Обновить форматтеры под новый регион.
         * Нужен для инициализации с текущим региона при запуске приложения.
         */
        @MainThread
        fun updateRegion(region: String) {
            current = SbisFormatter(region)
        }
    }

}