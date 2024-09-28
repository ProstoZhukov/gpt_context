package ru.tensor.sbis.base_components.adapter.vmadapter

/**@SelfDocumented*/
data class CellInfo(
    val layoutId: Int,
    val bindingId: Int,
    val itemChecker: ItemChecker<Any>
)