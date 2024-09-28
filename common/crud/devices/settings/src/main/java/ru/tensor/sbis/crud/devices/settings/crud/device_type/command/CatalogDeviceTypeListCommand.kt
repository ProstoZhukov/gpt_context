package ru.tensor.sbis.crud.devices.settings.crud.device_type.command

import io.reactivex.Observable
import ru.tensor.devices.settings.generated.DataRefreshedDeviceTypeFacadeCallback
import ru.tensor.devices.settings.generated.DeviceTypeFilter
import ru.tensor.devices.settings.generated.ListResultOfDeviceTypeMapOfStringString
import ru.tensor.devices.settings.generated.Metadata
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseCommand
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.crud.device_type.DeviceTypeRepository
import ru.tensor.sbis.crud.devices.settings.model.DeviceType
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.platform.generated.Subscription

/** Класс для получения списочных данных о типах подключаемого оборудования */
internal class CatalogDeviceTypeListCommand(private val repository: DeviceTypeRepository,
                                            private val listMapper: BaseModelMapper<ListResultOfDeviceTypeMapOfStringString, PagedListResult<BaseItem<DeviceType>>>) :
        BaseCommand(),
        BaseListObservableCommand<PagedListResult<BaseItem<DeviceType>>, DeviceTypeFilter, DataRefreshedDeviceTypeFacadeCallback> {

    /**
     * Пара: айди и имя родителя
     * Под родителем подразумевается тип оборудования: терминалы, сканнеры,...
     */
    var parentFolderItem: Pair<String, String>? = null

    /**@SelfDocumented */
    override fun list(filter: DeviceTypeFilter): Observable<PagedListResult<BaseItem<DeviceType>>> =
            performAction(Observable.fromCallable { repository.list(filter) }
                    .doOnNext {
                        parentFolderItem =
                                if (filter.byFolder != null && it.metadata != null) Pair(
                                        it.metadata?.get(Metadata.PARENT_FOLDER_ID) ?: "",
                                        it.metadata?.get(Metadata.PARENT_FOLDER_NAME) ?: "")
                                else null
                    }
                    .map(listMapper)
                    .compose(getObservableBackgroundSchedulers()))

    /**@SelfDocumented */
    override fun refresh(filter: DeviceTypeFilter): Observable<PagedListResult<BaseItem<DeviceType>>> =
            performAction(Observable.fromCallable { repository.refresh(filter) }
                    .doOnNext {
                        parentFolderItem =
                                if (filter.byFolder != null && it.metadata != null) Pair(
                                        it.metadata?.get(Metadata.PARENT_FOLDER_ID) ?: "",
                                        it.metadata?.get(Metadata.PARENT_FOLDER_NAME) ?: "")
                                else null
                    }
                    .map(listMapper)
                    .compose(getObservableBackgroundSchedulers()))

    /**@SelfDocumented */
    override fun subscribeDataRefreshedEvent(callback: DataRefreshedDeviceTypeFacadeCallback): Observable<Subscription> =
            Observable.fromCallable { repository.subscribeDataRefreshedEvent(callback) }
                    .compose(getObservableBackgroundSchedulers())
}
