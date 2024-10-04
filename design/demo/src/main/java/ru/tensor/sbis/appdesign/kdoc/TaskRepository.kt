package ru.tensor.sbis.appdesign.kdoc

import java.nio.channels.NotYetConnectedException
import java.util.*

/**
 * Интерфейс репозитория для моделей задач [TaskModel]
 *
 * @throws NotYetConnectedException каждый метод может бросить исключение, если хранилище ещё не подключено
 *
 * @author ma.kolpakov
 */
internal interface TaskRepository {

    /**
     * Загрузить задачу по её идентификатору [id]
     *
     * @return возвращается `null`, если в хранилище нет задачи `taskModel.id == id`
     */
    fun load(id: UUID): TaskModel?

    /**
     * Сохранение задачи. Если в хранилище уже присутствует [TaskModel] с `taskModel.id == id`, задача в хранилище будет
     * обновлена
     */
    fun save(taskModel: TaskModel)

    /**
     * Удаление задачи по её идентификатору [id]
     *
     * @return `true` при успешном удалении. `false`, если задача с переданным [id] не обнаружена или её не получилось
     * удалить
     */
    fun delete(id: UUID): Boolean
}