package ru.tensor.sbis.loyalty_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/** Минимальная информация об операции, необходимая для работы экрана "Программы лояльности". */
@Parcelize
data class OperationConfiguration(
    val operationUUID: UUID,
    val operationOwnerRegion: String?,
    val operationContext: OperationContext
) : Parcelable
