package ru.tensor.sbis.catalog_decl.catalog

/**
 *  Результат списочных методов.
 *
 *  @param result список эдементов
 *  @param haveMore есть больше
 *
 *  @author sp.lomakin
 */
class ListResultWrapper<T>(
    val result: MutableList<T>,
    val haveMore: Boolean,
    val syncState: SyncState? = null,
) {

    /**
     *  Состояние синхронизации.
     *
     *  [taskId] идентификатор асинхронной задачи.
     *  [initialCompleted] первичная синхронизация выполнена.
     */
    class SyncState(
        val taskId: String? = "",
        val initialCompleted: Boolean? = null,
    )
}
