package ru.tensor.sbis.crud.devices.settings.model

import java.util.ArrayList
import java.util.HashMap

/** Получить первичный TCP - Порт устройства из DeviceType/Device моделей */
fun getDefaultTcpPort(deviceType: DeviceType?, device: DeviceInside?): Int? {
    return deviceType?.port ?: device?.port
}

/** Получить список типов соединений, которые можно редактировать на UI */
fun DeviceType.getEditableConnectionTypesForMobile() = getEditableConnectionTypesForMobile(editableConnectionTypes)

/** См.[DeviceType.getEditableConnectionTypesForMobile] */
fun DeviceInside.getEditableConnectionTypesForMobile() = getEditableConnectionTypesForMobile(editableConnectionTypes)

/**
 * Фильтрует список драйверов.
 * Проходит по типу соединений драйверов и убирает соединения, с которыми нельзя работать на UI.
 * Исключает драйвера, у которых после фильтрации не осталось типов соединений с которыми можно работать.
 * */
fun DeviceType.getDriversWithFilteredConnectionTypesByMobile(): List<DriverInfo> {
    val editableConnectionTypes = getEditableConnectionTypesForMobile()
    return supportedDrivers.filterByEditableConnections(editableConnectionTypes)
}

/** См.[DeviceType.getDriversWithFilteredConnectionTypesByMobile] */
fun DeviceInside.getDriversWithFilteredConnectionTypesByMobile(): List<DriverInfo> {
    val editableConnectionTypes = getEditableConnectionTypesForMobile()
    return supportedDrivers.filterByEditableConnections(editableConnectionTypes)
}

private fun List<DriverInfo>.filterByEditableConnections(editableConnectionTypes: List<Int>) =
    map { driverInfo ->
        val filteredConnections = filterConnectionsListByEditableConnections(driverInfo.connections, editableConnectionTypes)
        driverInfo.copy(connections = filteredConnections)
    }
        .filter { it.connections.isNotEmpty() }

private fun getEditableConnectionTypesForMobile(editableConnectionTypes: HashMap<Int, ArrayList<String>>): List<Int> {
    val mobilePlatformTag = "mobile"
    return editableConnectionTypes
        .filter { it.value.contains(mobilePlatformTag) }
        .map { it.key }
        .excludeConflictConnectionTypes()
}

private fun filterConnectionsListByEditableConnections(
    connectionTypes: List<ConnectionTypeInside>,
    editableConnectionTypes: List<Int>
) = connectionTypes.filter { it.toInt() in editableConnectionTypes }

private fun List<Int>.isEftBaseConnectionTypesConflictExist() =
    contains(ConnectionTypeInside.TCP_IP.toInt()) && contains(ConnectionTypeInside.EFTBASE.toInt())

/** EFTBase должен поглощать TCP_IP, т.к технически EFTBase это TCP_IP с дополнительными настройками. */
private fun List<Int>.excludeConflictConnectionTypes() = if (isEftBaseConnectionTypesConflictExist()) {
    toMutableList().apply { remove(ConnectionTypeInside.TCP_IP.toInt()) }
} else this