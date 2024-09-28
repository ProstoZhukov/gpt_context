package ru.tensor.sbis.communication_decl.selection

import java.io.Serializable

/**
 * Use-case компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionUseCase : Serializable {

    /**
     * Наименование use_case, которое используется на контроллере для формирования списка источников.
     * Каждое наименование должно быть поддержано на контроллере, иначе будет ошибка загрузки списка.
     */
    val name: String

    /**
     * Аргументы intent_json, содержащие дополнительную информацию для работы use_case по имени [name].
     * Аргументами могут являться идентификаторы (например, диалога/документа), флаги.
     * Каждая из пар аргументов конкретизируется на контроллере для соответствующих use_case.
     */
    val args: HashMap<String, String?>
        get() = hashMapOf()

    /**
     * Мод выбора.
     */
    val selectionMode: SelectionMode
        get() = SelectionMode.REPLACE_ALL_IF_FIRST

    /**
     * Мод видимости кнопки подтверждения выбора.
     */
    val doneButtonMode: SelectionDoneButtonVisibilityMode
        get() = SelectionDoneButtonVisibilityMode.AT_LEAST_ONE

    /**
     * Мод шапки компонента.
     */
    val headerMode: SelectionHeaderMode
        get() = SelectionHeaderMode.VISIBLE

    /**
     * Лимит количества отображаемых элементов.
     */
    val itemsLimit: Int?
        get() = DEFAULT_ITEMS_LIMIT

    /**
     * Мод механики завершения.
     * true - если подтверждение выбора несет финальный характер.
     * Т.е. после окончательного выбора компонент превратится в тыкву,
     * и в момент перехода к следующему экрану не будет никаких лишних перестроений (промаргиваний)
     * интерфейса.
     * В случае, если компонент выбора после завершения будет оставаться видимым
     * и к нему можно будет вернуться, чтобы изменить выбор - признак должен быть false.
     */
    val isFinalComplete: Boolean
        get() = true
}

private const val DEFAULT_ITEMS_LIMIT = 20