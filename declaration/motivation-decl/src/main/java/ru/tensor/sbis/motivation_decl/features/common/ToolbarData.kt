package ru.tensor.sbis.motivation_decl.features.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Набор типов данных, которые могут отображаться в тулбаре.
 */
sealed interface ToolbarData : Parcelable {

    /** @SelfDocumented */
    val needShowNavigateBackButton: Boolean

    /** Стандартная шапка. Заголовок будет выбран открываемой фичей. */
    @Parcelize
    class Default(
        override val needShowNavigateBackButton: Boolean = true
    ) : ToolbarData


    /** Шапка с отображением персоны. */
    @Parcelize
    class Person(
        val uuid: UUID,
        val firstName: String = "",
        val lastName: String = "",
        val patronymicName: String = "",
        val photoURL: String,
        override val needShowNavigateBackButton: Boolean = true,
    ) : ToolbarData
}