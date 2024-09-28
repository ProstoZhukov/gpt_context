package ru.tensor.sbis.crud.devices.settings.model

/** Участок производства. */
data class ProductionArea(
    val productionSite: Long,
    val warehouseSale: Long,
    val id: String,
    val name: String,
    val productionName: String,
    val isMarked: Boolean,
    val isOriginal: Boolean,
    val company: Long
)