package ru.tensor.sbis.deals.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Аргументы для открытия реестра сделок.
 * @property clientUuid идентификатор слиента, реестр по которому нужно показать.
 * @property clientName название клиента, выводимое в шапке. null - название клианта получим сами с помощью
 * идентификаторов. Совет: передавать если уже вычитали название клиента, если это неудобно - не передавать.
 *
 * @author aa.sviridov
 */
@Parcelize
data class DealsListArgs(
    val clientUuid: UUID,
    val clientName: String? = null,
) : Parcelable