package ru.tensor.sbis.business.common.domain.interactor

import io.reactivex.Observable
import io.reactivex.Single

/*
 * Интерфейс интерактора для модификации данных CRUD командами
 * Создание/обновление/удаление данных через CRUD фасад контроллера
 *
 * @property DATA тип данных
 */
interface ModifyInteractor<DATA : Any> {

    /**
     * Выполнить команду создания новой записи
     *
     * @param entity опциональный новый экземпляр модели с уникальным uuid
     *
     * @return [Observable] результат успешности завершения создания
     */
    @Throws(NotImplementedError::class)
    fun createData(entity: DATA? = null): Single<DATA> =
        throw NotImplementedError()

    /**
     * Выполнить команду обновления существующий записи
     *
     * @param entity существующий экземпляр модели
     *
     * @return [Observable] c обновленной моделью
     */
    fun updateData(entity: DATA): Single<DATA>

    /**
     * Выполнить команду удаления существующий записи
     *
     * @param entity существующий экземпляр модели
     *
     * @return успешность удаления
     */
    fun deleteData(entity: DATA): Single<Boolean>
}