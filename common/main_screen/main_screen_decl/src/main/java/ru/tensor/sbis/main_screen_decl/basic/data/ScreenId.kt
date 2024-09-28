package ru.tensor.sbis.main_screen_decl.basic.data

import ru.tensor.sbis.common.navigation.NavxId

/**
 * Идентификатор прикладного экрана.
 *
 * @author us.bessonov
 */
sealed class ScreenId {

    /**
     * Идентификатор в структуре навигации приложения.
     */
    data class Navx(val id: NavxId) : ScreenId() {
        override fun toString() = id.ids.first().toString()
    }

    /**
     * Строковый идентификатор для экранов, не зависящих от сервиса навигации.
     */
    data class Tag(val string: String) : ScreenId() {
        override fun toString() = string
    }
}