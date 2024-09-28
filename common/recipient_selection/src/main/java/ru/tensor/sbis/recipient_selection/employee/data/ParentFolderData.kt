package ru.tensor.sbis.recipient_selection.employee.data

import java.util.*

/**
 * Данные родительской папки для навигации.
 * uuid нужен для запроса данных по папке, name - для обновления заголовка текущей папки
 */
data class ParentFolderData(
        val uuid: UUID,
        val name: String
)