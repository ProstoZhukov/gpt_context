package ru.tensor.sbis.business.common.data

/**
 * Провайдер хэшей фильтров фасадов контроллера
 */
interface HashFilterProvider {

    /** Возвращает хэш по фильтру [filter] */
    fun <CPP_FILTER : Any> getHash(filter: CPP_FILTER): Int

    /** Возвращает имя базового класса */
    fun <CPP_FILTER : Any> getName(filter: CPP_FILTER?): String
}