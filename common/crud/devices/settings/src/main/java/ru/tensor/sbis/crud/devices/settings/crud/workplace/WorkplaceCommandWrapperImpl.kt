package ru.tensor.sbis.crud.devices.settings.crud.workplace

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tensor.devices.settings.generated.DataRefreshedWorkplaceFacadeCallback
import ru.tensor.devices.settings.generated.WorkplaceFilter
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.crud.devices.settings.model.Workplace
import ru.tensor.sbis.crud.devices.settings.model.map
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.devices.settings.generated.Workplace as ControllerWorkplace

/** @see WorkplaceCommandWrapper */
internal class WorkplaceCommandWrapperImpl(private val workplaceRepository: WorkplaceRepository,
                                           private val deviceIdRepository: DeviceIdRepository,
                                           override val createCommand: CreateObservableCommand<ControllerWorkplace>,
                                           override val readCommand: ReadObservableCommand<Workplace>,
                                           override val updateCommand: UpdateObservableCommand<ControllerWorkplace>,
                                           override val deleteCommand: DeleteRepositoryCommand<ControllerWorkplace>,
                                           override val listCommand: BaseListObservableCommand<PagedListResult<BaseItem<Workplace>>, WorkplaceFilter, DataRefreshedWorkplaceFacadeCallback>) :
        WorkplaceCommandWrapper,
        BaseInteractor() {

    override fun updateTry(workplace: Workplace): Single<Workplace> =
        Single.fromCallable { workplaceRepository.updateTry(workplace.map()) }
            .map { it.map() }
            .compose(getSingleBackgroundSchedulers())

    override fun readId(workPlaceId: Long): Observable<Workplace> =
            Observable.fromCallable { workplaceRepository.readId(workPlaceId) }
                    .map { it.map() }
                    .compose(getObservableBackgroundSchedulers())

    override fun fetch(): Observable<Workplace> =
            Observable.fromCallable { workplaceRepository.fetch() }
                    .map { it.map() }
                    .compose(getObservableBackgroundSchedulers())

    override fun fetchMaybe(): Maybe<Workplace> {
        return Maybe.fromCallable<Workplace> {
            workplaceRepository.fetchNullable()?.map()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun create(name: String, companyId: Long, deviceId: String?, deviceName: String?): Observable<Workplace> =
            Observable.fromCallable { workplaceRepository.create(name, companyId, deviceId, deviceName) }
                    .map { it.map() }
                    .compose(getObservableBackgroundSchedulers())

    override fun create(deviceId: String?, deviceName: String?): Observable<Workplace> {
        return Observable.fromCallable { workplaceRepository.create(deviceId, deviceName) }
                .map { it.map() }
    }

    override fun delete(workPlaceId: Long): Observable<Boolean> =
            Observable.fromCallable { workplaceRepository.delete(workPlaceId) }
                    .compose(getObservableBackgroundSchedulers())

    override fun deleteTry(workPlaceId: Long): Completable =
            Completable.fromCallable { workplaceRepository.deleteTry(workPlaceId) }
                    .compose(completableBackgroundSchedulers)

    override fun getDefaultName(companyId: Long): Observable<String> =
            Observable.fromCallable { workplaceRepository.getDefaultName(companyId) }
                    .compose(getObservableBackgroundSchedulers())


    override fun setCompanyTry(workplaceId: Long, companyId: Long): Completable =
            Completable.fromCallable { workplaceRepository.setCompanyTry(workplaceId, companyId) }
                    .compose(completableBackgroundSchedulers)

    override fun setCompany(workplaceId: Long, companyId: Long): Completable =
            Completable.fromCallable { workplaceRepository.setCompany(workplaceId, companyId) }
                    .compose(completableBackgroundSchedulers)

    override fun getDeviceId(): Single<String> =
        Single.fromCallable { deviceIdRepository.getDeviceId() }
            .compose(getSingleBackgroundSchedulers())

    override fun restore(workplaceId: Long): Completable =
        Completable.fromCallable { workplaceRepository.restore(workplaceId) }
            .compose(completableBackgroundSchedulers)

    override fun readCurrent(): Maybe<Workplace> =
        Maybe.fromCallable { workplaceRepository.readCurrent() }
            .map { it.map() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override val emptyWorkplace = workplaceRepository.emptyWorkplace.map()
}
