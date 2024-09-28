package ru.tensor.sbis.business.common.data.base

import ru.tensor.sbis.CXX.SbisException
import ru.tensor.sbis.platform.generated.Subscription
import java.util.*
import kotlin.collections.HashMap
import kotlin.jvm.Throws

/**
 * Интерфейс фасада репозитория с CRUD коммандами
 *
 * @param ENTITY результирующая сущность контроллера
 * @param FILTER фильтр контроллера
 */
interface BaseCrudRepository<ENTITY, FILTER> {

    /**
     *  Создание нового экземпляра модели с уникальным uuid
     */
    @Throws(SbisException::class, Exception::class)
    fun create(): ENTITY? = throw NotImplementedError()

    /**
     * Синхронно используя переданный экземпляр [entity] создает и обновляет данные локально и на облаке.
     * Компоновка вызовов [BaseCrudRepository.create] и [BaseCrudRepository.update]
     * @return id новой созданной записи
     * @throws SbisException исключение в случае неудачи
     */
    @Throws(SbisException::class, Exception::class)
    fun create(entity: ENTITY): Long? = throw NotImplementedError()

    /**
     * Синхронно возвращает запись [ENTITY] из БД
     */
    @Throws(SbisException::class, Exception::class)
    fun read(filter: FILTER): ENTITY? = throw NotImplementedError()

    /**
     * Синхронно возвращает запись [ENTITY] из БД
     * @param id id созданной записи в локальной БД
     */
    @Throws(SbisException::class, Exception::class)
    fun read(id: Long): ENTITY? = throw NotImplementedError()

    /**
     * Синхронно возвращает данные [ENTITY] из БД и асинхронно запрашивает получение данных с БЛ.
     */
    @Throws(SbisException::class, Exception::class)
    fun readWithRefresh(filter: FILTER): ENTITY? = throw NotImplementedError()

    @Throws(SbisException::class, Exception::class)
    fun update(entity: ENTITY): ENTITY? = throw NotImplementedError()

    @Throws(SbisException::class, Exception::class)
    fun delete(uuid: UUID): Boolean = throw NotImplementedError()

    @Throws(SbisException::class, Exception::class)
    fun delete(id: Long): Boolean = throw NotImplementedError()

    @Throws(SbisException::class, Exception::class)
    fun setDataRefreshCallback(callback: (HashMap<String, String>?)->Unit): Subscription = throw NotImplementedError()
}

