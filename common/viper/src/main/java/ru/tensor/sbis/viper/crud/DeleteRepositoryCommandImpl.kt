package ru.tensor.sbis.viper.crud

import io.reactivex.Single
import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseCRUDCommand
import java.util.UUID

/**
 * @author ga.malinskiy
 */
class DeleteRepositoryCommandImpl<ENTITY>(repository: CRUDRepository<ENTITY>) :
        BaseCRUDCommand<ENTITY>(repository),
        DeleteRepositoryCommand<ENTITY> {

    override fun delete(uuid: UUID): Single<Boolean> =
            performAction(Single.fromCallable { mRepository.delete(uuid) }
                    .compose(getSingleBackgroundSchedulers()))
}
