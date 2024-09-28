package ru.tensor.sbis.business.common.data.base

/**
 * Интерфейс списочного фасада репозитория с CRUD коммандами
 *
 * @param ENTITY результирующая сущность контроллера
 * @param FILTER фильтр контроллера для одиночной выборки
 * @param ENTITY_LIST результирующая списочная сущность контроллера
 * @param ENTITY_FILTER фильтр контроллера для списочной выборки
 *
 * @author as.chadov
 */
interface BaseCrudListRepository<ENTITY, FILTER, ENTITY_LIST, ENTITY_FILTER> :
    BaseCrudRepository<ENTITY, FILTER> {

    /**
     * Асинхронно делает запрос на синхронизацию с ОБЛАКОМ. Синхронно отдает списочные данные из кэша
     */
    fun readListWithRefresh(filter: ENTITY_FILTER): ENTITY_LIST? =
        throw NotImplementedError()

    /**
     * Синхронно отдает списочные данные из КЭША
     */
    fun readList(filter: ENTITY_FILTER): ENTITY_LIST? =
        throw NotImplementedError()

    /**
     * Синхронно отдает списочные данные с облака без кэширования
     */
    fun search(searchQuery: String, filter: ENTITY_FILTER): ENTITY_LIST? =
        throw NotImplementedError()
}
