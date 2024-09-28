package ru.tensor.sbis.tasks.feature

import android.content.Context
import android.view.View
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import io.reactivex.Single
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Описывает внешний API задач.
 *
 * @author aa.sviridov
 */
interface TasksFeature : Feature {

    /**
     * Получение счётчиков задач сотрудника.
     * @param uuid идентификатор сотрудника.
     * @return [Single], излучающий информацию о счётчиках задач сотрудника, см. [EmployeeTaskCounters].
     */
    fun getEmployeeTaskCounters(uuid: UUID): Single<EmployeeTaskCounters>

    /**
     * Проверяет есть ли доступ пользователя для просмотра реестра задач сотрудника.
     * @param uuid идентификатор сотрудника.
     * @return [Single], излучающий информацию о доступности задач сотрудника, true если доступны,
     * false - нет.
     */
    fun isThereAccessToViewUserTasks(uuid: UUID): Single<Boolean>

    /**
     * Создаёт фрагмент списка задач для внешнего использования.
     * @param args аргументы реестра задач, см. [TasksListArgs].
     */
    fun createTasksListFragment(args: TasksListArgs): Fragment

    /**
     * Аргументы для создания реестра задач.
     *
     * @author aa.sviridov
     */
    sealed interface TasksListArgs {

        /**
         * Комбинированный раздел (с вкладками "на мне" и "от меня") для выбора задач из списка с передачей
         * идентификаторов.
         *
         * aa.sviridov
         */
        object CombinedSelection : TasksListArgs

        /**
         * Реестр задач сотрудника.
         * @property personUuid идентификатор персоны-сотрудника.
         *
         * aa.sviridov
         */
        class Person(
            val personUuid: UUID,
        ) : TasksListArgs

        /**
         * Реестр задач из диалога.
         * @property dialogUuid идентификатор диалога.
         *
         * aa.sviridov
         */
        class Dialog(
            val dialogUuid: UUID,
        ) : TasksListArgs
    }

    /**
     * Создаёт вьюшку карточки задачи как в реестре задач. Холдер возвращает сразу и по мере
     * получения данных отрисовывает её.
     * @param documentUuid идентификатор документа.
     * @param context контекст фрагмента или активности.
     * @param errorHandler коллбек ошибки получения данных, параметр - ошибка [Throwable].
     * @param onTaskClick обработчик клика по задаче.
     * @param onPersonClick обработчик клика по исполнителям.
     * @param onContractorClick обработчик клика по контрагентам.
     * @param onDeleteClick обработчик действия удаления.
     * @return вьюшка, соответствующая документу.
     */
    @MainThread
    fun createTaskCardView(
        documentUuid: UUID,
        context: Context,
        errorHandler: (Throwable) -> Unit = DEFAULT_CREATE_TASK_CARD_VIEW_ERROR_HANDLER,
        onTaskClick: (documentUuid: UUID) -> Unit = DEFAULT_CREATE_TASK_CARD_VIEW_ON_TASK_CLICK,
        onPersonClick: (personUuid: UUID) -> Unit = DEFAULT_CREATE_TASK_CARD_VIEW_ON_PERSON_CLICK,
        onContractorClick: (contractorUrl: String) -> Unit = DEFAULT_CREATE_TASK_CARD_VIEW_ON_CONTRACTOR_CLICK,
        onDeleteClick: (documentUuid: UUID) -> Unit = DEFAULT_CREATE_TASK_CARD_VIEW_ON_DELETE,
    ): View

    /**
     * Модель данных счётчиков задач пользователя.
     * @property values значения счётчиков.
     * @property responsible ответственный по задачам.
     *
     * @author aa.sviridov
     */
    data class EmployeeTaskCounters(
        val values: TasksCounters,
        val responsible: String
    )

    /**
     * Пустая реализация-заглушка [TasksFeature].
     *
     * @author aa.sviridov
     */
    object Stub : TasksFeature {

        override fun getEmployeeTaskCounters(uuid: UUID): Single<EmployeeTaskCounters> =
            throw NotImplementedError("Stub")

        override fun isThereAccessToViewUserTasks(uuid: UUID): Single<Boolean> =
            throw NotImplementedError("Stub")

        override fun createTasksListFragment(args: TasksListArgs): Fragment =
            throw NotImplementedError("Stub")

        override fun createTaskCardView(
            documentUuid: UUID,
            context: Context,
            errorHandler: (Throwable) -> Unit,
            onTaskClick: (documentUuid: UUID) -> Unit,
            onPersonClick: (personUuid: UUID) -> Unit,
            onContractorClick: (contractorUrl: String) -> Unit,
            onDeleteClick: (documentUuid: UUID) -> Unit
        ): View = throw NotImplementedError("Stub")
    }

    companion object {

        /**
         * Коллбек ошибки получения данных по-умолчанию, см. [createTaskCardView].
         */
        val DEFAULT_CREATE_TASK_CARD_VIEW_ERROR_HANDLER: (Throwable) -> Unit = {}

        /**
         * Обработчик клика по задаче по-умолчанию, см. [createTaskCardView].
         */
        val DEFAULT_CREATE_TASK_CARD_VIEW_ON_TASK_CLICK: (documentUuid: UUID) -> Unit = {}

        /**
         * Обработчик клика по исполнителям, см. [createTaskCardView].
         */
        val DEFAULT_CREATE_TASK_CARD_VIEW_ON_PERSON_CLICK: (personUuid: UUID) -> Unit = {}

        /**
         * Обработчик клика по контрагентам, см. [createTaskCardView].
         */
        val DEFAULT_CREATE_TASK_CARD_VIEW_ON_CONTRACTOR_CLICK: (contractorUrl: String) -> Unit = {}

        /**
         * Обработчик действия удаления, см. [createTaskCardView].
         */
        val DEFAULT_CREATE_TASK_CARD_VIEW_ON_DELETE: (documentUuid: UUID) -> Unit = {}
    }
}