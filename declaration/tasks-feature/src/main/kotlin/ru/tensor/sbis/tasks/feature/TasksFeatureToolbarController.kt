package ru.tensor.sbis.tasks.feature

/**
 * Интерфейс для управления видимости тулбара в карточке профиля. Меняется видимость тулбара для того, чтобы не было
 * вкладок над вкладками.
 *
 * @author aa.sviridov
 */
@Deprecated("Временное решение. В новой реализации подумать над этим.")
interface TasksFeatureToolbarController {

    /**
     * Видимость тулбара. Может быть GONE, VISIBLE, INVISIBLE, null - если не удалось получить видимость (метод
     * вызван слишком рано и инициализация ещё не закончена).
     */
    var toolbarVisibility: Int?
}