package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Значения таймаутов для сканнеров.
 *
 * @param defaultValue значение по-умолчанию для таймаута у сканера (в ms)
 * @param minValue минимальное значение таймаута у сканера (в ms)
 * @param maxValue максимальное значение таймаута у сканера (в ms)
 * */
@Parcelize
data class ScannerTimeout(val defaultValue: Int?, val minValue: Int? = null, val maxValue: Int?) : Parcelable