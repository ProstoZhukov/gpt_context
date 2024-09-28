package ru.tensor.sbis.crud.devices.settings.contract

import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.crud.devices.settings.model.DeviceCanUpdate
import ru.tensor.sbis.crud.devices.settings.model.DeviceInside
import ru.tensor.sbis.crud.devices.settings.model.DeviceCardSaveArgs
import java.util.UUID

/**
 * Интерфейс для предоставления карточки, работающий с участками производства.
 * Не путать с экранами, предоставляется именно карточка.
 * На данный момент используется для предоставления специфических данных и вызовов специфических методов.
 * */
@Deprecated("Будет удалён: https://online.sbis.ru/opendoc.html?guid=dc32bf6b-6d56-47c3-b3e8-f89a7236bf3e")
interface ProductionAreaCard {

    /** Идентификатор устройства. */
    val deviceUUID: UUID

    /**
     * Сохранить/обновить устройство.
     *
     * @param device - модель сохраняемого/обновляемого устройства.
     * @param args - аргументы, передаваемые контролллеру в сценариях сохранения/обновления оборудования.
     * */
    fun saveAsync(device: DeviceInside, args: DeviceCardSaveArgs): Single<DeviceCanUpdate>

    /** Отписаться от операции сохранения/обновления девайса. */
    fun destroySaveSubscription()

    /**
     * Предоставить текстовку для кнопки участков производства.
     * Зависит от количества выбранных участков производства.
     * Внутри себя вешает подписку на события контроллера, которую по хорошему нужно очищать.
     * */
    fun getProductionAreasHintObservable(): Observable<String>

    /** Отписаться от событий контроллера. */
    fun destroyHintSubscription()
}