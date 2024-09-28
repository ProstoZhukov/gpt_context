package ru.tensor.sbis.tasks.feature

import java.util.UUID

/**
 * Интерфейс для передачи результата выбора задачи пользователем.
 *
 * @author aa.sviridov
 */

interface TasksListSelectionResultReceiver {

    /**
     * Вызывается когда пользователь кликнул по задаче в реестре
     * @param uuid идентификатор задачи, по которой кликнул пользователь.
     */
    fun onTaskListSelected(uuid: UUID)
}