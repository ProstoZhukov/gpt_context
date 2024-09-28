package ru.tensor.sbis.mvp.interactor.crudinterface.command

import io.reactivex.Single
import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository
import java.util.UUID

/**
 * Реализация команды удаления из репозитория
 * @author am.boldinov
 */
@Deprecated(message = "Устаревший подход, переходим на mvi")
class DeleteCommand<ENTITY>(repository: CRUDRepository<ENTITY>) : BaseCRUDCommand<ENTITY>(repository),
    DeleteObservableCommand {
    override fun delete(uuid: UUID): Single<Boolean> {
        return performAction(Single.fromCallable { mRepository.delete(uuid) }
            .compose(getSingleBackgroundSchedulers()))
    }
}