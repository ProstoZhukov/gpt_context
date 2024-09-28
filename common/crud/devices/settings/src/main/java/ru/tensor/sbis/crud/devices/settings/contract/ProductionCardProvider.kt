package ru.tensor.sbis.crud.devices.settings.contract

import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Провайдер для карточки устройства.
 * Используется в Presto.
 * */
@Deprecated("Будет удалён: https://online.sbis.ru/opendoc.html?guid=dc32bf6b-6d56-47c3-b3e8-f89a7236bf3e")
interface ProductionCardProvider : Feature {

    /**
     * Предоставить карточку на основе идентификатора типа девайса.
     * Создаёт новый девайс, после чего создаёт из него карточку.
     *
     * @param deviceType - тип девайса, представленный в [UUID].
     * @param workplaceId - идентификатор рабочего места.
     * */
    fun createDeviceCardSyncFromDeviceType(deviceType: UUID, workplaceId: Long): ProductionAreaCard

    /**
     * Предоставить карточку на основе идентификатора устройства.
     *
     * @param deviceUUID - идентификатор девайса.
     * */
    fun createDeviceCardSyncFromDeviceUUID(deviceUUID: UUID): ProductionAreaCard
}