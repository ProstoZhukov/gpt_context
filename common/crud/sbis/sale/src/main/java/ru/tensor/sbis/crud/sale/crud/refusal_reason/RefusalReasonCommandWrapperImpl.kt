package ru.tensor.sbis.crud.sale.crud.refusal_reason

import io.reactivex.Observable
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.crud.sale.model.RefusalReason
import ru.tensor.sbis.crud.sale.model.RefusalReasonType
import ru.tensor.sbis.crud.sale.model.map
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonFilter

/**@SelfDocumented */
@Suppress("DEPRECATION")
internal class RefusalReasonCommandWrapperImpl(private val repository: RefusalReasonRepository,
                                               override val listCommand: ListObservableCommand<PagedListResult<RefusalReason>, RefusalReasonFilter>) :
        RefusalReasonCommandWrapper,
        BaseInteractor() {

    override fun create(name: String, type: RefusalReasonType, isWriteOff: Boolean): Observable<RefusalReason> =
            Observable.fromCallable { repository.create(name, type.map(), isWriteOff).map() }
                    .compose(getObservableBackgroundSchedulers())

    override fun read(id: Long): Observable<RefusalReason> =
            Observable.fromCallable { repository.read(id)?.map() ?: throw IllegalArgumentException("Refusal reason with id=$id not found") }
                    .compose(getObservableBackgroundSchedulers())

    override fun update(entity: RefusalReason): Observable<RefusalReason> =
            Observable.fromCallable { repository.update(entity.map()).map() }
                    .compose(getObservableBackgroundSchedulers())

    override fun delete(id: Long): Observable<Boolean> =
            Observable.fromCallable { repository.delete(id) }
                    .compose(getObservableBackgroundSchedulers())
}