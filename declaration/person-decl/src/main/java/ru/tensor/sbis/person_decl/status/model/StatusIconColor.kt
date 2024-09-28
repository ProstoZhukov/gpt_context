package ru.tensor.sbis.person_decl.status.model

/**
 * Интерфейс описывающий цвет иконки
 */
interface StatusIconColor {
    /**
     * @property uuid уникальный идентификатор цвета иконки
     */
    val uuid: String

    /**
     * @property attrRes сгенерированный идентификатор ресурса цвета
     */
    val attrRes: Int
}
