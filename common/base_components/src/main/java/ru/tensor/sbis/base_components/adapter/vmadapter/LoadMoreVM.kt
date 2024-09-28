package ru.tensor.sbis.base_components.adapter.vmadapter

import androidx.databinding.BaseObservable

/**@SelfDocumented*/
data class LoadMoreVM(
    val indeterminate: Boolean = true,
    val delayedShowing: Boolean = false,
) : BaseObservable()