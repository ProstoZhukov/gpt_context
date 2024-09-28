package ru.tensor.sbis.crud.sale.crud.refusal_reason

import ru.tensor.sbis.sale.mobile.generated.*

/**
 * Интерфейс для связи с контроллером.
 */
interface RefusalReasonRepository {

    /**
     * Функция для создания причины возврата/удаления
     *
     * @param name - имя причины возврата/удаления
     * @param type - тип причины: возврат или удаление
     * @param isWriteOff - флаг обозначающий производится ли возврат/удаление со списанием
     *
     * @return RefusalReasonModel
     */
    fun create(name: String, type: RefusalReasonType, isWriteOff: Boolean): RefusalReasonModel

    /**
     * Функция для получения причины возврата/удаления по идентификатору
     *
     * @param id - идентификатор причины возврата/удаления
     *
     * @return RefusalReasonModel
     */
    fun read(id: Long): RefusalReasonModel?

    /**
     * Функция для обновления причины возврата/удаления
     *
     * @param entity - модель причины возврата/удаления
     *
     * @return RefusalReasonModel
     */
    fun update(entity: RefusalReasonModel): RefusalReasonModel

    /**
     * Функция для удаления причины возврата/удаления по идентификатору
     *
     *  @param id - идентификатор причины возврата/удаления
     *
     * @return Boolean
     */
    fun delete(id: Long): Boolean

    /**
     * Функция для получения списка причин возвратов и удалений (запускает синхронизацию с облаком)
     *
     * @param filter - модель фильтра по которому делаем запрос
     *
     * @return RefusalReasonListResult
     */
    fun list(filter: RefusalReasonFilter): RefusalReasonListResult

    /**
     * Функция для получения списка причин возвратов/удалений из кэша
     *
     * @param filter - модель фильтра по которому делаем запрос
     *
     * @return RefusalReasonListResult
     */
    fun refresh(filter: RefusalReasonFilter): RefusalReasonListResult

    /**
     * Функция для установки колбэка
     *
     * @param callback - модель колбэка
     *
     * @return RefusalReasonSubscription
     */
    fun setDataRefreshCallback(callback: RefusalReasonDataRefreshCallback): RefusalReasonSubscription
}