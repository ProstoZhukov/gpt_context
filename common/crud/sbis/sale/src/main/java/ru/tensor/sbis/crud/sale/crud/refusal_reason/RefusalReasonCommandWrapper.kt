package ru.tensor.sbis.crud.sale.crud.refusal_reason

import io.reactivex.Observable
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.crud.sale.model.RefusalReason
import ru.tensor.sbis.crud.sale.model.RefusalReasonType
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonFilter

/**
 * Wrapper команд для контроллера
 */
interface RefusalReasonCommandWrapper {

    /**@SelfDocumented */
    val listCommand: ListObservableCommand<PagedListResult<RefusalReason>, RefusalReasonFilter>

    /**
     * Функция для создания причины возврата/удаления
     *
     * @param name - имя причины возврата/удаления
     * @param type - тип причины: возврат или удаление
     * @param isWriteOff - флаг обозначающий производится ли возврат/удаление со списанием
     */
    fun create(name: String, type: RefusalReasonType, isWriteOff: Boolean = false): Observable<RefusalReason>

    /**
     * Функция для получения причины возврата/удаления по идентификатору
     *
     * @param id - идентификатор причины возврата/удаления
     */
    fun read(id: Long): Observable<RefusalReason>

    /**
     * Функция для обновления причины возврата/удаления
     *
     * @param entity - модель причины возврата/удаления
     */
    fun update(entity: RefusalReason): Observable<RefusalReason>

    /**
     * Функция для удаления причины возврата/удаления по идентификатору
     *
     * @param id - идентификатор причины возврата/удаления
     */
    fun delete(id: Long): Observable<Boolean>
}