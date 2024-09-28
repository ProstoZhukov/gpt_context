package ru.tensor.sbis.deals.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Аргументы для открытия карточки сделки.
 *
 * @author aa.sviridov
 */
sealed class DealOpenArgs : Parcelable {

    /**
     * Идентификатор сделки (идентификаторВИ).
     */
    abstract val extId: String

    /**
     * Идентификатор сделки (идентификатор).
     */
    abstract val uuid: UUID

    /**
     * Тип документа.
     */
    abstract val type: String

    /**
     * Аргументы для открытия карточки сделки когда имеются данные сделки, например из реестра.
     * @property details основные детали сделки, см. [DealMainDetails].
     *
     * @author aa.sviridov
     */
    @Parcelize
    class Detailed(
        val details: DealMainDetails,
    ) : DealOpenArgs() {

        override val extId: String
            get() = details.extId

        override val uuid: UUID
            get() = details.uuid

        override val type: String
            get() = details.type
    }

    /**
     * Аргументы для открытия карточки сделки когда имеются только идентификаторы сделки.
     *
     * @author aa.sviridov
     */
    @Parcelize
    class IdsOnly(
        override val extId: String,
        override val uuid: UUID,
        override val type: String,
    ) : DealOpenArgs()
}