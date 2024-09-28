package ru.tensor.sbis.person_decl.status.model

/**
 * Интерфейс описывающий размер иконки
 */
interface StatusIconSize {
    /**
     * @property uuid уникальный идентификатор размера иконки
     */
    val uuid: String

    /**
     * @property dimenRes сгенерированный идентификатор ресурса размера
     */
    val dimenRes: Int
}
