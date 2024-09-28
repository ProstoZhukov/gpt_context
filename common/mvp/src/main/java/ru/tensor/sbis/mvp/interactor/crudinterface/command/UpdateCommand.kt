package ru.tensor.sbis.mvp.interactor.crudinterface.command

import io.reactivex.Single
import ru.tensor.sbis.common.exceptions.LoadDataException
import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository

/**
 * Реализация команды обновления репозитория
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class UpdateCommand<ENTITY>(repository: CRUDRepository<ENTITY>) : BaseCRUDCommand<ENTITY>(repository),
    UpdateObservableCommand<ENTITY> {
    override fun update(entity: ENTITY): Single<ENTITY> {
        return performAction(Single.fromCallable {
            val result = mRepository.update(entity)
                ?: throw LoadDataException(mRepository.javaClass.simpleName + " update result == null!")
            result
        }
            .compose(getSingleBackgroundSchedulers()))
    }
}