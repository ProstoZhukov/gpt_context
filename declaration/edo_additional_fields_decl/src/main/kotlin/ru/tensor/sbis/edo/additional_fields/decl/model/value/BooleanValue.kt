package ru.tensor.sbis.edo.additional_fields.decl.model.value

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Перечисление возможных значений доп.поля типа boolean
 */
@Parcelize
enum class BooleanValue : Parcelable {
    UNDEFINED,
    TRUE,
    FALSE;

    val isUndefined get() = this == UNDEFINED

    fun toBoolean() = when (this) {
        UNDEFINED -> null
        TRUE -> true
        FALSE -> false
    }

    companion object {

        fun fromBoolean(boolean: Boolean?) = when (boolean) {
            true -> TRUE
            false -> FALSE
            null -> UNDEFINED
        }
    }
}