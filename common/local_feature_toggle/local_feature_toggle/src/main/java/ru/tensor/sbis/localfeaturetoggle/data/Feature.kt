package ru.tensor.sbis.localfeaturetoggle.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель с информацией о локальной фиче.
 *
 * @param name String - уникальный идентификатор.
 * @param description String - описание фичи (отображается на инженерном экране).
 * @param isActivated Boolean - активность фичи.
 *
 * @author mb.kruglova
 */
@Parcelize
data class Feature(
    val name: String,
    val description: String,
    var isActivated: Boolean = false
) : Parcelable