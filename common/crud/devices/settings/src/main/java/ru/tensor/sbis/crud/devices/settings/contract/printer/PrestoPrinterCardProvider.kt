package ru.tensor.sbis.crud.devices.settings.contract.printer

import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Провайдер для карточки устройства.
 * Используется в Presto.
 * */
interface PrestoPrinterCardProvider : Feature {

    /**
     * Предоставить карточку на основе идентификатора типа девайса.
     * Создаёт новый девайс, после чего создаёт из него карточку.
     *
     * @param deviceType - тип девайса, представленный в [UUID].
     * @param workplaceId - идентификатор рабочего места.
     * */
    fun createPrinterCardFromDeviceType(deviceType: UUID, workplaceId: Long): PrinterCardBase

    /** Предоставить карточку на основе идентификатора устройства. */
    fun createPrinterCardFromDeviceUUID(deviceUUID: UUID): PrinterCardBase
}