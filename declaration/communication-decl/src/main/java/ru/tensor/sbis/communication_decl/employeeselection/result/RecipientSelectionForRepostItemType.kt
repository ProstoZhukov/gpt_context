package ru.tensor.sbis.communication_decl.employeeselection.result

import java.lang.IllegalArgumentException

/**
 * Тип выбранного item'а для списка получателей репоста новости
 */
enum class RecipientSelectionForRepostItemType {
    PERSON,
    FOLDER,
    GROUP;

    companion object {

        fun fromValue(value: Int): RecipientSelectionForRepostItemType {
            for (item in values()) {
                if (item.ordinal == value) {
                    return item
                }
            }
            throw IllegalArgumentException("No such type for passed value")
        }

    }
}