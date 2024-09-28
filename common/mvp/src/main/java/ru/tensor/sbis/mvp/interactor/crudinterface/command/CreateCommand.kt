package ru.tensor.sbis.mvp.interactor.crudinterface.command

import io.reactivex.Single
import ru.tensor.sbis.common.exceptions.LoadDataException
import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository

/**
 * Реализация создания команды
 * @author am.boldinov
 */
@Deprecated(message = "Устаревший подход, переходим на mvi")
class CreateCommand<ENTITY>(repository: CRUDRepository<ENTITY>) : BaseCRUDCommand<ENTITY>(repository),
    CreateObservableCommand<ENTITY> {
    override fun create(): Single<ENTITY> {
        return performAction(Single.fromCallable {
            val result = mRepository.create()
                ?: throw LoadDataException(mRepository.javaClass.simpleName + " create result == null!")
            result
        }
            .compose(getSingleBackgroundSchedulers()))
    }
}