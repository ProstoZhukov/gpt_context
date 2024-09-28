package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.devices.settings.generated.KkmCardSaveArgs
import ru.tensor.sbis.common.util.asArrayList

/** Аргументы, передаваемые контролллеру в сценариях сохранения/обновления оборудования. */
class DeviceCardSaveArgs(val actions: List<Int>, val skipCheckRegNumber: Boolean)

/** @SelfDocumented */
fun DeviceCardSaveArgs.toKkmCardSaveArgs() = KkmCardSaveArgs(actions.asArrayList(), skipCheckRegNumber)