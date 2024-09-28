package ru.tensor.sbis.communicator.common.ui.hostfragment.contracts

/** @SelfDocumented */
interface FabKeeper {

    /** @SelfDocumented */
    val fabId: Int

    /** @SelfDocumented */
    fun setFabClickListener(navigationFabClickListener: (() -> Unit)?)
}