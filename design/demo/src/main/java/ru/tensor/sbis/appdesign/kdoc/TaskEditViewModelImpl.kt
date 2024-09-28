package ru.tensor.sbis.appdesign.kdoc

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.tensor.sbis.appdesign.R
import java.util.*

/**
 * Примитивная реализация для [TaskEditViewModel]. Сейчас взаимодействие с [repository] происходит в главном потоке
 * (это неправильно, но _для презентации достаточно_).
 * TODO: 1/31/2021 [работа в фоне будет оформлена по задаче](к каждому TODO обязательна ссылка на задачу)
 *
 * Принципиальная схема инициализации:
 * ```
 * val vm = TaskEditViewModelImpl(repository, appContext)
 * vm.loadTask(id)
 * ```
 * Если нужно сразу вернуть результат (например из фабрики), можно использовать
 * [scoped function](https://kotlinlang.org/docs/reference/scope-functions.html#scope-functions) (в примере **apply**)
 * ```
 * override fun <T : ViewModel> create(modelClass: Class<T>): T {
 *      require(modelClass === TaskEditViewModelImpl::class.java) { "Unexpected view model class $modelClass" }
 *      @Suppress("UNCHECKED_CAST")
 *      return TaskEditViewModelImpl(repository, context).apply {
 *          loadTask(taskId)
 *      } as T
 * }
 * ```
 *
 * @author ma.kolpakov
 */
internal class TaskEditViewModelImpl(
    /**
     * Реализация [TaskRepository], которая используется для загрузки [TaskRepository.load] и сохранения изменений
     * [TaskRepository.save] по задаче
     */
    private val repository: TaskRepository,
    /**
     * Используется только при инициализации. Не захватывается на время жизни [ViewModel]
     */
    context: Context
) : ViewModel(), TaskEditViewModel {

    /**
     * Сообщение о неудачной загрузке задачи
     */
    private val loadErrorMessage = context.getString(R.string.common_data_loading_error)

    /**
     * Редактируемая задача
     *
     * @sample onTaskLoaded
     */
    private var task: TaskModel? = null

    override val taskTitle = MutableLiveData<String?>()
    override val taskDescription = MutableLiveData<String>()
    override val errorMessage = MutableLiveData<String?>()

    override fun loadTask(taskId: UUID) {
        check(task == null) { "Task already loaded" }
        repository.load(taskId).let {
            if (it == null) {
                errorMessage.value = loadErrorMessage
            } else {
                // модель успешно загружена, выставляем данные во view для работы пользователя
                onTaskLoaded(it)
            }
            task = it
        }
    }

    override fun onSave() {
        checkNotNull(task) { "Task is not loaded" }.let {
            // для сохранения копируем модель с обновленными атрибутами
            repository.save(
                it.applyChanges(
                    title = taskTitle.value,
                    // значение всегда есть, обеспечено бизнес логикой
                    description = taskDescription.value!!
                )
            )
        }
    }

    /**
     * Установка данных из модели [task] для view
     */
    private fun onTaskLoaded(task: TaskModel) {
        taskTitle.value = task.title
        taskDescription.value = task.description
    }
}