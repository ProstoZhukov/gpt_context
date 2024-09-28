package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель, описывающая драйвер устройства.
 *
 * @param isDefault Является ли драйвер драйвером по умолчанию?
 * @param name Имя драйвера
 * @param connections Список соединений,которые можно использовать с этим драйвером
 * */
@Parcelize
data class DriverInfo(val isDefault: Boolean, val name: String, val connections: List<ConnectionTypeInside>) : Parcelable

/** @SelfDocumented */
fun List<DriverInfo>.findDefaultDriver() = find { it.isDefault }

/** @SelfDocumented */
fun List<DriverInfo>.findDefaultDriverOrGetFirst() = findDefaultDriver() ?: firstOrNull()