package ru.tensor.sbis.crud.sale.model

/**
 * Модель предупреждения, возвращаемая при попытке активировать устройство
 *
 * @param result Boolean - флаг, обозначающий возможность активировать устройство
 * @param message String - сообщение предупреждения
 * @param deviceId Int? - идентификатор устройства, которое необходимо деактивировать прежде, чем активировать новое
 * @param metastate Int? - идентификатор предупреждения
 * @param actions List<Int> - список идентификаторов операций (используется для метода [ru.tensor.sbis.crud.sale.crud.kkm.KkmCommandWrapper.canUpdate])
 */
data class KkmCanUpdate(
    val result: Boolean,
    val message: String,
    val deviceId: Long?,
    val metastate: Int?,
    val actions: List<Int>
)