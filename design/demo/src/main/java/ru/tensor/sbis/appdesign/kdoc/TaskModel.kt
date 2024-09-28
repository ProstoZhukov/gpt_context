package ru.tensor.sbis.appdesign.kdoc

import java.util.*

/**
 * Модель задачи
 *
 * @author ma.kolpakov
 */
data class TaskModel(
    /**
     * Уникальный идентификатор задачи
     */
    val id: UUID,
    /**
     * Заголовок задачи. Может отсутствовать
     */
    val title: String?,
    /**
     * Описание задачи
     */
    val description: String
)
