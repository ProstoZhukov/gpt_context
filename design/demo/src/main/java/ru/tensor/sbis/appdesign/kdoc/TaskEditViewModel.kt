package ru.tensor.sbis.appdesign.kdoc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

/**
 * Интерфейс вьюмодели для экрана редактирования задачи
 *
 * @see TaskModel
 *
 * @author ma.kolpakov
 */
internal interface TaskEditViewModel {

    /**
     * Заголовок задачи.
     * Представлен в виде [MutableLiveData] для двунаправленного связывания данных
     */
    val taskTitle: MutableLiveData<String?>

    /**
     * Описание задачи.
     * Представлен в виде [MutableLiveData] для двунаправленного связывания данных
     */
    val taskDescription: MutableLiveData<String>

    /**
     * Подписка на служебные сообщения
     */
    val errorMessage: LiveData<String?>

    /**
     * Метод для загрузки редактируемой задачи по её [TaskModel.id]
     *
     * @throws IllegalStateException если задача уже загружена для редактирования
     */
    fun loadTask(taskId: UUID)

    /**
     * Метод для сохранения изменений задачи
     *
     * @throws IllegalStateException если задача ещё не загружена методом [loadTask]
     */
    fun onSave()
}